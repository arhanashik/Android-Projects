package com.project.blackspider.musician.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.project.blackspider.musician.activities.DetailActivity;
import com.project.blackspider.musician.activities.MainActivity;
import com.project.blackspider.musician.activities.PlayerActivity;
import com.project.blackspider.musician.database.StorageUtil;
import com.project.blackspider.musician.enums.PlaybackAction;
import com.project.blackspider.musician.enums.PlaybackStatus;
import com.project.blackspider.musician.model.Audio;
import com.project.blackspider.musician.variables.FinalVariables;
import com.project.blackspider.musician.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mr blackSpider on 9/4/2017.
 */

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {
    private MediaPlayer mediaPlayer;
    //path to the audio file
    private String mediaFile;

    //Used to pause/resume MediaPlayer
    private int resumePosition;
    private static long DURATION;

    private AudioManager audioManager;

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    //List of available Audio files
    private List<Audio> audioList = new ArrayList<>();
    private int audioIndex = -1;
    //an object of the currently playing audio
    private Audio activeAudio;

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
        //Listen headset button press
        registerMediaButtonPressReceiver();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        try {
            //Load data from SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        stopMedia();
        //stop the service
        stopSelf();
    }

    //Handle errors
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        String err = "";
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                err += "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK ";
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                err += "MEDIA ERROR SERVER DIED ";
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                err += "MEDIA ERROR UNKNOWN ";
                break;
        }

        err += extra;
        Log.d("MediaPlayer Error",  err);
        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
        skipToNext();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info.
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        if(audioManager != null) removeAudioFocus();

        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);
        unregisterReceiver(mediaButtonPressReceiver);
        Log.d("Service", "Unbinding...");
        PlayerActivity.mBound = false;
        PlayerActivity.mPlayer = null;

        removeNotification();

        //clear cached playlist
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
        new StorageUtil(getApplicationContext()).storeLastAudioIndex(audioIndex);
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        if(audioManager != null) removeAudioFocus();

        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);
        unregisterReceiver(mediaButtonPressReceiver);
        Log.d("Service", "Unbinding...");
        PlayerActivity.mBound = false;
        PlayerActivity.mPlayer = null;

        removeNotification();

        //clear cached playlist
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
        new StorageUtil(getApplicationContext()).storeLastAudioIndex(audioIndex);
        stopSelf();
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeAudio.getData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
        DURATION = activeAudio.getDuration()/1000;
        if(MainActivity.isOpen){
            MainActivity.init();
            if(MainActivity.mAdapter!=null) {
                MainActivity.mAdapter.setSelectedAudioId(activeAudio.getId());
                MainActivity.mAdapter.notifyDataSetChanged();
            }

        }else if(DetailActivity.isOpen) DetailActivity.init();
    }

    public void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            buildNotification(PlaybackStatus.PLAYING);
            if(MainActivity.isOpen){
                MainActivity.init();
                if(MainActivity.mAdapter!=null) {
                    MainActivity.mAdapter.setSelectedAudioId(activeAudio.getId());
                    MainActivity.mAdapter.notifyDataSetChanged();
                }

            }else if(DetailActivity.isOpen) DetailActivity.init();
        }
        new StorageUtil(getApplicationContext()).storeLastAudioIndex(audioIndex);
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        else {
            if (mediaPlayer.isPlaying()) return true;
            else return false;
        }
    }

    public void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            if(MainActivity.isOpen){
                MainActivity.init();
                if(MainActivity.mAdapter!=null) {
                    MainActivity.mAdapter.setSelectedAudioId(-1);
                    MainActivity.mAdapter.notifyDataSetChanged();
                }

            }else if(DetailActivity.isOpen) DetailActivity.init();
        }
        buildNotification(PlaybackStatus.PAUSED);
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            if(MainActivity.isOpen){
                MainActivity.init();
                if(MainActivity.mAdapter!=null) {
                    MainActivity.mAdapter.setSelectedAudioId(-1);
                    MainActivity.mAdapter.notifyDataSetChanged();
                }

            }else if(DetailActivity.isOpen) DetailActivity.init();
        }
        buildNotification(PlaybackStatus.PAUSED);
        new StorageUtil(getApplicationContext()).storeLastAudioIndex(audioIndex);
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            if(MainActivity.isOpen){
                MainActivity.init();
                if(MainActivity.mAdapter!=null) {
                    MainActivity.mAdapter.setSelectedAudioId(activeAudio.getId());
                    MainActivity.mAdapter.notifyDataSetChanged();
                }

            }else if(DetailActivity.isOpen) DetailActivity.init();
        }
        buildNotification(PlaybackStatus.PLAYING);
        new StorageUtil(getApplicationContext()).storeLastAudioIndex(audioIndex);
    }

    public void seekToPosition(int newPosition) {
        if(mediaPlayer==null) return;

        resumePosition = newPosition;
        mediaPlayer.seekTo(resumePosition);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            if(MainActivity.mAdapter!=null) {
                MainActivity.mAdapter.setSelectedAudioId(activeAudio.getId());
                MainActivity.mAdapter.notifyDataSetChanged();
            }
        }
    }

    public int getCurrentAudioIndex(){
        if (mediaPlayer != null) return audioIndex;

        return -1;
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition()/1000;

        return 0;
    }

    public int getResumePosition() {
        if (mediaPlayer != null) return resumePosition;

        return 0;
    }

    public long getDurationInSecond() {
        if(activeAudio!=null) return activeAudio.getDuration()/1000;

        return 0;
    }

    public Audio getCurrentAudio(){
        return activeAudio;
    }

    public MediaPlayer getMediaPlayer(){
        if(mediaPlayer!=null) return mediaPlayer;
        else return null;
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Get the new media index form SharedPreferences
            try {
                audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();

                if (audioIndex != -1 && audioIndex < audioList.size()) {
                    //index is in a valid range
                    activeAudio = audioList.get(audioIndex);
                } else {
                    stopSelf();
                }
            } catch (NullPointerException e) {
                stopSelf();
            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopMedia();
            if(mediaPlayer!=null)mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);

            if(MainActivity.isOpen){
                MainActivity.init();
                if(MainActivity.mAdapter!=null) {
                    MainActivity.mAdapter.setSelectedAudioId(activeAudio.getId());
                    MainActivity.mAdapter.notifyDataSetChanged();
                }

            }else if(DetailActivity.isOpen) DetailActivity.init();
        }
    };

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(FinalVariables.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    //Becoming noisy
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    //pressing media button
    private BroadcastReceiver mediaButtonPressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //play/pause audio on ACTION_MEDIA_BUTTON
            KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            PlaybackStatus playbackStatus = PlaybackStatus.PLAYING;

            if(keyEvent.getAction()==KeyEvent.ACTION_UP){
                int keyCode = keyEvent.getKeyCode();
                if(keyCode==KeyEvent.KEYCODE_MEDIA_NEXT){
                    skipToNext();
                    playbackStatus = PlaybackStatus.PLAYING;
                }
                else if(keyCode==KeyEvent.KEYCODE_MEDIA_PREVIOUS){
                    skipToPrevious();
                    playbackStatus = PlaybackStatus.PLAYING;
                }
                else if(keyCode==KeyEvent.KEYCODE_HEADSETHOOK){
                    if(mediaPlayer.isPlaying()) {
                        pauseMedia();
                        playbackStatus = PlaybackStatus.PAUSED;
                    }
                    else {
                        resumeMedia();
                        playbackStatus = PlaybackStatus.PLAYING;
                    }
                }
                else if(keyCode==KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE){
                    if(mediaPlayer.isPlaying()) {
                        pauseMedia();
                        playbackStatus = PlaybackStatus.PAUSED;
                    }
                    else {
                        resumeMedia();
                        playbackStatus = PlaybackStatus.PLAYING;
                    }
                }
                else if(keyCode==KeyEvent.KEYCODE_MEDIA_PLAY){
                    resumeMedia();
                    playbackStatus = PlaybackStatus.PLAYING;
                }
                else if(keyCode==KeyEvent.KEYCODE_MEDIA_PAUSE){
                    pauseMedia();
                    playbackStatus = PlaybackStatus.PAUSED;
                }
            }

            buildNotification(playbackStatus);
        }
    };

    private void registerMediaButtonPressReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        //
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(mediaButtonPressReceiver, intentFilter);
    }

    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), FinalVariables.PACKAGE_NAME);
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
                seekToPosition((int) position);
            }
        });
    }

    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher); //replace with medias albumArt
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                .build());
    }

    public void skipToNext() {
        int playStyle = new StorageUtil(getApplicationContext()).loadAudioPlayStyle();
        if(playStyle==FinalVariables.REPEAT_ALL){
            if (audioIndex == audioList.size() - 1) {
                //if last in playlist
                audioIndex = 0;
                activeAudio = audioList.get(audioIndex);
            } else {
                //get next in playlist
                activeAudio = audioList.get(++audioIndex);
            }

        }else {
            Random random = new Random();
            audioIndex = random.nextInt((audioList.size() - 1) + 1);
            activeAudio = audioList.get(audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);
        new StorageUtil(getApplicationContext()).storeLastAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    public void skipToPrevious() {

        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }

        //Update stored index
        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);
        new StorageUtil(getApplicationContext()).storeLastAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    public void buildNotification(PlaybackStatus playbackStatus) {
        //needs to be initialized
        int notificationAction = android.R.drawable.ic_media_pause;
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(PlaybackAction.PAUSE);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(PlaybackAction.PLAY);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.default_blue_light))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(activeAudio.getArtist())
                .setContentTitle(activeAudio.getTitle())
                .setContentInfo(activeAudio.getAlbum())
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(PlaybackAction.PREV))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(PlaybackAction.NEXT));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(FinalVariables.NOTIFICATION_ID, notificationBuilder.build());
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(FinalVariables.NOTIFICATION_ID);
        notificationManager.cancelAll();
    }

    private PendingIntent playbackAction(PlaybackAction playbackAction) {
        Intent playbackActionIntent = new Intent(this, MediaPlayerService.class);
        switch (playbackAction) {
            case PLAY:
                // Play
                playbackActionIntent.setAction(FinalVariables.ACTION_PLAY);
                return PendingIntent.getService(this, FinalVariables.PLAY, playbackActionIntent, 0);
            case PAUSE:
                // Pause
                playbackActionIntent.setAction(FinalVariables.ACTION_PAUSE);
                return PendingIntent.getService(this, FinalVariables.PAUSE, playbackActionIntent, 0);
            case NEXT:
                // Next track
                playbackActionIntent.setAction(FinalVariables.ACTION_NEXT);
                return PendingIntent.getService(this, FinalVariables.NEXT, playbackActionIntent, 0);
            case PREV:
                // Previous track
                playbackActionIntent.setAction(FinalVariables.ACTION_PREVIOUS);
                return PendingIntent.getService(this, FinalVariables.PREV, playbackActionIntent, 0);
            default:
                break;
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackActionIntent) {
        if (playbackActionIntent == null || playbackActionIntent.getAction() == null) return;

        String actionString = playbackActionIntent.getAction();
        if (actionString.equalsIgnoreCase(FinalVariables.ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(FinalVariables.ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(FinalVariables.ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(FinalVariables.ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(FinalVariables.ACTION_STOP)) {
            transportControls.stop();
        }

        if(MainActivity.isOpen){
            MainActivity.init();
            if(MainActivity.mAdapter!=null) {
                MainActivity.mAdapter.setSelectedAudioId(activeAudio.getId());
                MainActivity.mAdapter.notifyDataSetChanged();
            }

        }else if(DetailActivity.isOpen) DetailActivity.init();
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}

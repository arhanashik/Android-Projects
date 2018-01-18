package com.project.blackspider.musician.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.project.blackspider.musician.database.StorageUtil;
import com.project.blackspider.musician.model.Audio;
import com.project.blackspider.musician.R;
import com.project.blackspider.musician.service.MediaPlayerService;
import com.project.blackspider.musician.variables.FinalVariables;
import com.project.blackspider.musician.view.ProgressView;
import com.project.blackspider.musician.view.SeekArc;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public abstract class PlayerActivity extends AppCompatActivity {

    public static MediaPlayerService mPlayer;
    public static boolean mBound = false;
    private static TextView mTimeView;
    private static TextView mDurationView;
    public static ProgressView mProgressView;
    public static SeekArc mProgressChangerView;
    int position = 0; long duration = 0;
    int newPosition = 0, audioIndex = 0;

    public List<Audio> audioList = new ArrayList<>();

    public Handler mUpdateProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1) {
                if(mPlayer!=null) {
                    position = mPlayer.getCurrentPosition();
                    duration = mPlayer.getDurationInSecond();
                    onUpdateProgress(position, duration);
                    sendEmptyMessageDelayed(1, DateUtils.SECOND_IN_MILLIS);
                }
            }else {
                position = 0; duration = 0;
                mPlayer = null;
            }
        }
    };
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    public final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to MediaPlayerService, cast the IBinder and get MediaPlayerService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mPlayer = binder.getService();
            mBound = true;
            onBind();
            Log.d("Service", "Bound");
            mPlayer.pauseMedia();
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mBound = false;
            onUnbind();
            Log.d("Service", "Unbound");
        }
    };

    public void onUpdateProgress(int position, long duration) {
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatElapsedTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatElapsedTime(duration));
        }
        if (mProgressView != null) {
            mProgressView.setMax((int) duration-2);
            mProgressView.setProgress(position);

            mProgressChangerView.setMax((int) duration-2);
            mProgressChangerView.setProgress(position);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioIndex = new StorageUtil(getApplicationContext()).loadLastAudioIndex();

        if(checkPermission()) {
            loadAudio();
            // Bind to MediaPlayerService
            if (!mBound) {
                //Store Serializable audioList to SharedPreferences
                StorageUtil storage = new StorageUtil(getApplicationContext());
                storage.storeAudio(audioList);
                storage.storeAudioIndex(audioIndex);

                Intent playerIntent = new Intent(this, MediaPlayerService.class);
                startService(playerIntent);
                bindService(playerIntent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }
        else requestPermission();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", mBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mTimeView = (TextView) findViewById(R.id.time);
        mDurationView = (TextView) findViewById(R.id.duration);
        mProgressView = (ProgressView) findViewById(R.id.progress);
        mProgressChangerView = (SeekArc) findViewById(R.id.progress_changer);

        mProgressChangerView.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                if(mPlayer!=null){
                    if(progress>=mPlayer.getDurationInSecond()-2) {
                        mPlayer.skipToNext();
                        if(MainActivity.isOpen) MainActivity.init();
                        else if(DetailActivity.isOpen) DetailActivity.init();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                newPosition = seekArc.getProgress()*1000;
                if(mPlayer!=null) mPlayer.seekToPosition(newPosition);
                if(DetailActivity.isOpen) {
                    DetailActivity.fab.setImageResource(R.drawable.ic_pause_vector);
                    DetailActivity.mCoverView.start();
                };
            }
        });
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(PlayerActivity.this, new
                String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, FinalVariables.STORAGE_ACCESS_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FinalVariables.STORAGE_ACCESS_PERMISSION_REQUEST_CODE:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        loadAudio();
                        // Bind to MediaPlayerService
                        if (!mBound) {
                            //Store Serializable audioList to SharedPreferences
                            StorageUtil storage = new StorageUtil(getApplicationContext());
                            storage.storeAudio(audioList);
                            storage.storeAudioIndex(audioIndex);

                            Intent playerIntent = new Intent(this, MediaPlayerService.class);
                            startService(playerIntent);
                            bindService(playerIntent, mConnection, Context.BIND_AUTO_CREATE);
                        }
                    } else {
                        Toast.makeText(PlayerActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                        requestPermission();
                    }
                }
                break;
        }
    }

    public void onBind() {
        mUpdateProgressHandler.sendEmptyMessage(1);
    }

    public void onUnbind() {
        mUpdateProgressHandler.removeMessages(0);
    }

    public List<Audio> loadAudio() {
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            audioList.clear();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                int albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                // Save to audioList
                audioList.add(new Audio(id, albumId, data, title, album, artist, duration));
            }
        }
        cursor.close();
        return audioList;
    }


    public void playAudio(int audioIndex) {
        //resume at old position if already playing
        if(audioList.get(audioIndex).getId()==mPlayer.getCurrentAudio().getId()){
            mPlayer.resumeMedia();
        }
        else {
            new StorageUtil(getApplicationContext()).storeLastAudioIndex(audioIndex);
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(FinalVariables.Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    public void pauseAudio(int audioIndex) {
        //Check is service is active
        if(mPlayer!=null) {
            if(audioList.get(audioIndex).getId()==mPlayer.getCurrentAudio().getId()) {
                mPlayer.pauseMedia();
            }
            else {
                playAudio(audioIndex);
            }
        }
    }

}

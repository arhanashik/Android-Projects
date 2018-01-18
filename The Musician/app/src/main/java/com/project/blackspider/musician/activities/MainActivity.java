package com.project.blackspider.musician.activities;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.blackspider.musician.adapters.AudioAdapter;
import com.project.blackspider.musician.adapters.ViewPagerAdapter;
import com.project.blackspider.musician.database.StorageUtil;
import com.project.blackspider.musician.fragments.OneFragment;
import com.project.blackspider.musician.fragments.ThreeFragment;
import com.project.blackspider.musician.fragments.TwoFragment;
import com.project.blackspider.musician.interfaces.AudioAdapterListener;
import com.project.blackspider.musician.utils.TunnelPlayerWorkaround;
import com.project.blackspider.musician.variables.FinalVariables;
import com.project.blackspider.musician.R;
import com.project.blackspider.musician.view.CustomRecycler.IndexFastScrollRecyclerView;
import com.project.blackspider.musician.view.ProgressView;
import com.project.blackspider.musician.view.RhombusDrawable;
import com.project.blackspider.musician.view.ScrollTextView;
import com.project.blackspider.musician.view.bubbles.BubbleLayout;
import com.project.blackspider.musician.view.bubbles.BubblesManager;
import com.project.blackspider.musician.view.bubbles.OnInitializedCallback;
import com.project.blackspider.musician.visualizer.VisualizerView;
import com.project.blackspider.musician.visualizer.renderer.BarGraphRenderer;
import com.project.blackspider.musician.visualizer.renderer.CircleBarRenderer;
import com.project.blackspider.musician.visualizer.renderer.CircleRenderer;
import com.project.blackspider.musician.visualizer.renderer.LineRenderer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends PlayerActivity implements AudioAdapterListener, View.OnClickListener{
    public static View mCoverView, mTitleView, mTimeView, mDurationView, mProgressView, mFabView, mTrackContainer, mControlView;
    public static ScrollTextView titleTxt;
    public static TextView audioProgress, audioDuration;
    public static ImageView cover, playBtn, nextBtn, avater, bubblePrevBtn, bubblePlayBtn, bubbleNextBtn;
    public static Context mContext;
    public static ProgressView progressView;
    private ImageView iconImg;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static IndexFastScrollRecyclerView mRecyclerView;
    public static AudioAdapter mAdapter;
    public static PopupMenu popupMenu;

    public static boolean isOpen = false;
    private boolean isResumed = false;
    public int currPosition = 0;

    public static BubblesManager bubblesManager;
    public static BubbleLayout bubbleLayout;

    public Handler progressHandler = new Handler();
    public Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            position = mPlayer.getCurrentPosition();
            progressView.setProgress(position);
            audioProgress.setText(DateUtils.formatElapsedTime(position));
            progressHandler.postDelayed(this, 1000);
        }
    };
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private MediaPlayer mVisualizerPlayer;  /* to avoid tunnel player issue */
    private MediaPlayer mSilentPlayer;  /* to avoid tunnel player issue */
    private VisualizerView mVisualizerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isOpen = true;
        DetailActivity.isOpen = false;
        mContext = this;

        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.title_view);
        mTrackContainer = findViewById(R.id.tract_container);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);

        mContentView = mCoverView;

        titleTxt = (ScrollTextView) findViewById(R.id.title_txt);
        cover = (ImageView) findViewById(R.id.cover);
        playBtn = (ImageView) findViewById(R.id.btn_play);
        nextBtn = (ImageView) findViewById(R.id.btn_next);

        iconImg = (ImageView) findViewById(R.id.img_icon);
        iconImg.setBackground(new RhombusDrawable(getResources().getColor(R.color.colorAccent)));
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            ((LinearLayout) root).setDividerPadding(40);
            ((LinearLayout) root).setDividerDrawable(getDrawable(R.drawable.ic_m));
        }

        playBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        mFabView.setOnClickListener(this);

        // Set the recycler adapter
        mRecyclerView = (IndexFastScrollRecyclerView) findViewById(R.id.tracks);
        initialiseRecyclerView();

    }

    int position = 0; long duration = 0;
    @Override
    protected void onResume() {
        super.onResume();

        initTunnelPlayerWorkaround();
        try {
            initVisualizer();
        }catch (Exception e){
            e.printStackTrace();
            mVisualizerPlayer.stop();
        }

        hide(); //hide action bar and status bar
        isOpen = true;
        isResumed = true;
        currPosition = new StorageUtil(getApplicationContext()).loadLastAudioIndex();
        init();
        titleTxt.startScroll();
        if(mPlayer!=null) {
            runAudioProgress();

            if(mPlayer.isPlaying()) {
                mAdapter.setSelectedAudioId(mPlayer.getCurrentAudio().getId());
                playBtn.setImageResource(R.drawable.ic_pause_vector);
            }
            else {
                mAdapter.setSelectedAudioId(-1);
                playBtn.setImageResource(R.drawable.ic_play_vector);
            }

            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mPlayer.getCurrentAudioIndex());
        }

        initializeBubblesManager();
        if(bubbleLayout!=null) {
            if(bubbleLayout.isAttachedToWindow()) {
                bubblesManager.removeBubble(bubbleLayout);
            }
        }

        if(getIntent().getBooleanExtra("EXIT", false)) {
            addNewBubble();
            onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        cleanUp();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        isOpen = false;
        cleanUp();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Log.d("Main activity: ", "on destroy");
        isOpen = false;
        progressHandler.removeCallbacks(progressRunnable);
        cleanUp();
        // Unbind from the service
        if (mBound) {
            //mBound = false;
            //unbindService(mConnection);
            //mPlayer.stopSelf();
            //mPlayer = null;
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if(v==playBtn) togglePlay(currPosition);
        else if(v==mFabView) {
            isOpen = false;
            addNewBubble();
            onBackPressed();
        }
        else if(v==nextBtn) {
            if(mPlayer!=null) mPlayer.skipToNext();
            playBtn.setImageResource(R.drawable.ic_pause_vector);
        }
        else if(v==bubblePrevBtn) {
            mPlayer.skipToPrevious();
            bubblePlayBtn.setImageResource(R.drawable.ic_pause_vector);
        }
        else if(v==bubblePlayBtn) togglePlay(currPosition);
        else if(v==bubbleNextBtn) {
            mPlayer.skipToNext();
            bubblePlayBtn.setImageResource(R.drawable.ic_pause_vector);
        }

        currPosition = new StorageUtil(getApplicationContext()).loadAudioIndex();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FinalVariables.STORAGE_ACCESS_PERMISSION_REQUEST_CODE:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        if(audioList.size()>0) {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
        }
    }

    public static void init() {
        if(mPlayer != null) {
            Log.d("Init value", mPlayer.getCurrentAudio().getTitle()+", Index: "+mPlayer.getCurrentAudioIndex());
            titleTxt.setText(mPlayer.getCurrentAudio().getTitle());
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, mPlayer.getCurrentAudio().getAlbumId());
            Glide.with(mContext)
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(cover);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "Recent");
        adapter.addFragment(new TwoFragment(), "All songs");
        adapter.addFragment(new ThreeFragment(), "Favourites");
        adapter.addFragment(new ThreeFragment(), "Folders");
        adapter.addFragment(new ThreeFragment(), "Playlist");
        adapter.addFragment(new ThreeFragment(), "Artists");
        adapter.addFragment(new ThreeFragment(), "Albums");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    protected void initialiseRecyclerView() {
        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AudioAdapter(this, audioList, this);
        mRecyclerView.setAdapter(mAdapter);
        if(audioList.size()>0) {
            mAdapter.setSelectedAudioId(audioList.get(audioIndex).getId());
            mAdapter.notifyDataSetChanged();
            //mRecyclerView.smoothScrollToPosition(audioIndex);
            mRecyclerView.scrollToPosition(audioIndex);
        }

        mRecyclerView.setIndexTextSize(12);
        mRecyclerView.setIndexBarColor("#00000000");
        mRecyclerView.setIndexBarCornerRadius(0);
        mRecyclerView.setIndexBarTransparentValue((float) 0); //full transparent - 0
        mRecyclerView.setIndexbarMargin(0);
        mRecyclerView.setIndexbarWidth(40);
        mRecyclerView.setPreviewPadding(0);
        mRecyclerView.setIndexBarTextColor("#FFFFFF");

        mRecyclerView.setIndexBarVisibility(true);
        mRecyclerView.setIndexbarHighLateTextColor("#FD4848"); //color accent
        mRecyclerView.setIndexBarHighLateTextVisibility(true);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState==RecyclerView.SCROLL_STATE_IDLE) mTrackContainer.setVisibility(View.VISIBLE);
                else {
                    if(mTrackContainer.getVisibility()==View.VISIBLE) mTrackContainer.setVisibility(View.GONE);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public void runAudioProgress(){
        duration = mPlayer.getDurationInSecond();
        progressView = (ProgressView) mProgressView;
        audioProgress = (TextView) mTimeView;
        audioDuration = (TextView) mDurationView;
        progressView.setMax((int) duration);
        audioDuration.setText(DateUtils.formatElapsedTime(duration-2));
        progressRunnable.run();
    }

    public void togglePlay(int position){
        if(mPlayer!=null){
            if(!mPlayer.isPlaying()) {
                playAudio(position);
                playBtn.setImageResource(R.drawable.ic_pause_vector);
                if(bubblePlayBtn!=null) bubblePlayBtn.setImageResource(R.drawable.ic_pause_vector);
            }
            else {
                pauseAudio(position);
                playBtn.setImageResource(R.drawable.ic_play_vector);
                if(bubblePlayBtn!=null) bubblePlayBtn.setImageResource(R.drawable.ic_play_vector);
            }
        }
    }

    public void onFabClick(View view) {
        isOpen = false;
        //noinspection unchecked
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<>(mCoverView, ViewCompat.getTransitionName(mCoverView)),
                //new Pair<>(mTitleView, ViewCompat.getTransitionName(mTitleView)),
                //new Pair<>(mTimeView, ViewCompat.getTransitionName(mTimeView)),
                //new Pair<>(mDurationView, ViewCompat.getTransitionName(mDurationView)),
                //new Pair<>(mProgressView, ViewCompat.getTransitionName(mProgressView)),
                new Pair<>(mFabView, ViewCompat.getTransitionName(mFabView)));
        ActivityCompat.startActivity(this, new Intent(this, DetailActivity.class), options.toBundle());
    }

    @Override
    public void onAudioClicked(View view, int position) {
        isOpen = true;
        currPosition = position;

        if(mAdapter.getSelectedAudioId()==audioList.get(position).getId()) togglePlay(currPosition);
        else {
            playAudio(position);
            playBtn.setImageResource(R.drawable.ic_pause_vector);
            titleTxt.startScroll();
        }
        init();
        runAudioProgress();

    }

    @Override
    public void onAudioOptionClicked(final View view, final int position) {
        if(mPlayer!=null){
            popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_audio_option, popupMenu.getMenu());
            if(mPlayer.isPlaying() && mAdapter.getSelectedAudioId()==audioList.get(position).getId()){
                popupMenu.getMenu().getItem(0).setTitle(R.string.action_pause);
                popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_pause_vector);
            }
            setForceShowIcon(popupMenu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.action_play:
                            isOpen = true;
                            if(!mPlayer.isPlaying()) playAudio(position);
                            else pauseAudio(position);
                            init();
                            runAudioProgress();
                            break;
                        case R.id.action_favourite:
                            Toast.makeText(getApplicationContext(), "Added to favourite",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.action_add_to_playlist:
                            Toast.makeText(getApplicationContext(), "ID "+audioList.get(position).getId(),
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.action_share:
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
            //error - strange thing - status bar is showing - so need to hide that
            hide();
        }
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initializeBubblesManager() {
        bubblesManager = new BubblesManager.Builder(this)
                .setTrashLayout(R.layout.area_bubble_trash)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                        //addNewBubble();
                    }
                })
                .build();
        bubblesManager.initialize();
    }

    private void addNewBubble() {
        bubbleLayout = (BubbleLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.area_bubble, null);
        avater = (ImageView) bubbleLayout.findViewById(R.id.avatar);
        mControlView = bubbleLayout.findViewById(R.id.controls);
        bubblePrevBtn = (ImageView) bubbleLayout.findViewById(R.id.previous);
        bubblePlayBtn = (ImageView) bubbleLayout.findViewById(R.id.play);
        bubbleNextBtn = (ImageView) bubbleLayout.findViewById(R.id.next);
        bubblePrevBtn.setOnClickListener(this);
        bubblePlayBtn.setOnClickListener(this);
        bubbleNextBtn.setOnClickListener(this);
        if(mPlayer.isPlaying()) bubblePlayBtn.setImageResource(R.drawable.ic_pause_vector);
        bubbleLayout.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) { }
        });
        bubbleLayout.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                if(mControlView.getVisibility()==View.VISIBLE) {
                    mControlView.setVisibility(View.GONE);
                    bubbleLayout.goToWall();
                }
                else mControlView.setVisibility(View.VISIBLE);
            }
        });

        bubbleLayout.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleLayout, 0, 150);
        bubbleLayout.goToWall();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void initVisualizer()
    {
        mVisualizerPlayer = MediaPlayer.create(this, R.raw.test);
        mVisualizerPlayer.setLooping(true);
        mVisualizerPlayer.start();

        // We need to link the visualizer view to the media player so that
        // it displays something
        mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
        mVisualizerView.link(mVisualizerPlayer);

        // Start with just line renderer
        addLineRenderer();
    }

    private void cleanUp()
    {
        if (mVisualizerPlayer != null)
        {
            mVisualizerView.release();
            mVisualizerPlayer.release();
            mVisualizerPlayer = null;
        }

        if (mSilentPlayer != null)
        {
            mSilentPlayer.release();
            mSilentPlayer = null;
        }
    }

    // Workaround (for Galaxy S4)
    //
    // "Visualization does not work on the new Galaxy devices"
    //    https://github.com/felixpalmer/android-visualizer/issues/5
    //
    // NOTE:
    //   This code is not required for visualizing default "test.mp3" file,
    //   because tunnel player is used when duration is longer than 1 minute.
    //   (default "test.mp3" file: 8 seconds)
    //
    private void initTunnelPlayerWorkaround() {
        // Read "tunnel.decode" system property to determine
        // the workaround is needed
        if (TunnelPlayerWorkaround.isTunnelDecodeEnabled(this)) {
            mSilentPlayer = TunnelPlayerWorkaround.createSilentMediaPlayer(this);
        }
    }

    // Methods for adding renderers to visualizer
    private void addBarGraphRenderers()
    {
        Paint paint = new Paint();
        paint.setStrokeWidth(50f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 56, 138, 252));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
        mVisualizerView.addRenderer(barGraphRendererBottom);

        Paint paint2 = new Paint();
        paint2.setStrokeWidth(12f);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.argb(200, 181, 111, 233));
        BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
        mVisualizerView.addRenderer(barGraphRendererTop);
    }

    private void addCircleBarRenderer()
    {
        Paint paint = new Paint();
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        paint.setColor(Color.argb(255, 222, 92, 143));
        CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
        mVisualizerView.addRenderer(circleBarRenderer);
    }

    private void addCircleRenderer()
    {
        Paint paint = new Paint();
        paint.setStrokeWidth(3f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(255, 222, 92, 143));
        CircleRenderer circleRenderer = new CircleRenderer(paint, true);
        mVisualizerView.addRenderer(circleRenderer);
    }

    private void addLineRenderer()
    {
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1f);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.argb(88, 0, 128, 255));

        Paint lineFlashPaint = new Paint();
        lineFlashPaint.setStrokeWidth(5f);
        lineFlashPaint.setAntiAlias(true);
        lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
        LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
        mVisualizerView.addRenderer(lineRenderer);
    }

    // Actions for buttons defined in xml
    public void startPressed(View view) throws IllegalStateException, IOException
    {
        if(mVisualizerPlayer.isPlaying())
        {
            return;
        }
        mVisualizerPlayer.prepare();
        mVisualizerPlayer.start();
    }

    public void stopPressed(View view)
    {
        mVisualizerPlayer.stop();
    }

    public void barPressed(View view)
    {
        addBarGraphRenderers();
    }

    public void circlePressed(View view)
    {
        addCircleRenderer();
    }

    public void circleBarPressed(View view)
    {
        addCircleBarRenderer();
    }

    public void linePressed(View view)
    {
        addLineRenderer();
    }

    public void clearPressed(View view)
    {
        mVisualizerView.clearRenderers();
    }
}

package com.project.blackspider.quarrelchat.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.blackspider.quarrelchat.CustomView.SimpleLoveTextView;
import com.project.blackspider.quarrelchat.Database.DBHelper;
import com.project.blackspider.quarrelchat.FCM.UpdateFcmId;
import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Model.Soulmate;
import com.project.blackspider.quarrelchat.R;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GreetingsActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
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
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
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

    private static ImageView imgFullscreenContent, loadingGif;
    private static SimpleLoveTextView appTitle;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private static DBHelper dbHelper;
    private static ArrayList<String> myInfo = new ArrayList<>();
    private static Soulmate soulmate;

    private final Handler mChangeActivityHandler = new Handler();
    private final Runnable mShowLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            loadingGif.setVisibility(View.VISIBLE);
            Glide.with(GreetingsActivity.this).load(R.drawable.loading_blue).asGif().into(loadingGif);
            checkSessionValidity();
        }
    };
    private final Runnable mGoToWelcomeActivityRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(GreetingsActivity.this, WelcomeActivity.class);
            Pair<View, String> pair1 = Pair.create((View)imgFullscreenContent, ViewCompat.getTransitionName(imgFullscreenContent));
            Pair<View, String> pair2 = Pair.create((View) appTitle, ViewCompat.getTransitionName(appTitle));
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(GreetingsActivity.this,
                            pair1,
                            pair2);
            startActivity(intent, options.toBundle());
        }
    };
    private final Runnable mGoToMainActivityRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(GreetingsActivity.this, "Welcome back "+soulmate.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(GreetingsActivity.this, MainActivity.class);
            intent.putStringArrayListExtra("my_info", myInfo);
//            Pair<View, String> pair1 = Pair.create((View)appTitle, ViewCompat.getTransitionName(appTitle));
//            Pair<View, String> pair2 = Pair.create((View) loadingGif, ViewCompat.getTransitionName(loadingGif));
//            ActivityOptionsCompat options = ActivityOptionsCompat.
//                    makeSceneTransitionAnimation(GreetingsActivity.this,
//                            pair1,
//                            pair2);
//            startActivity(intent, options.toBundle());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greetings);
        if(Build.VERSION.SDK_INT>=21)
        {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.shared_element_transation));
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

//        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });
        hide();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        imgFullscreenContent = (ImageView) findViewById(R.id.fullscreen_content);
        loadingGif = (ImageView) findViewById(R.id.loading_gif);
        appTitle = (SimpleLoveTextView) findViewById(R.id.app_title);
        dbHelper = new DBHelper(this);
        sharedPreferences = getSharedPreferences(FinalVariables.SHARED_PREFERENCES_SIGN_IN_INFO, Context.MODE_PRIVATE);
        Glide.with(this).load(R.mipmap.ic_launcher).thumbnail(1.0f).into(imgFullscreenContent);
        loadingGif.setVisibility(View.GONE);
        //imgFullscreenContent.setOnTouchListener(mDelayHideTouchListener);

        if(getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }else {
            mChangeActivityHandler.removeCallbacks(mGoToWelcomeActivityRunnable);
            mChangeActivityHandler.removeCallbacks(mGoToMainActivityRunnable);
            mChangeActivityHandler.postDelayed(mShowLoadingRunnable, 2000);
        }
    }

    private void checkSessionValidity(){
        mChangeActivityHandler.removeCallbacks(mShowLoadingRunnable);
        if(sharedPreferences.getBoolean(FinalVariables.IS_SESSION_EXIST, false)){
            soulmate = new Soulmate();
            soulmate = dbHelper.getUserInfo();

            UpdateFcmId ufi = new UpdateFcmId(this, soulmate.getUsername());
            ufi.update(FirebaseInstanceId.getInstance().getToken(), FinalVariables.UPDATE_FCM_ID_URL);

            myInfo.clear();
            myInfo.add(soulmate.getUsername());
            myInfo.add(soulmate.getName());
            myInfo.add(soulmate.getEmail());
            myInfo.add(soulmate.getPhone());
            myInfo.add(soulmate.getPassword());
            myInfo.add(soulmate.getImagePath());
            myInfo.add(soulmate.getFcmId());
            mChangeActivityHandler.postDelayed(mGoToMainActivityRunnable, 2000);
        }else {
            mChangeActivityHandler.postDelayed(mGoToWelcomeActivityRunnable, 2000);
        }
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
        mControlsView.setVisibility(View.GONE);
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

    @Override
    protected void onPause() {
        mChangeActivityHandler.removeCallbacks(mGoToWelcomeActivityRunnable);
        mChangeActivityHandler.removeCallbacks(mGoToMainActivityRunnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        hide();
        mChangeActivityHandler.removeCallbacks(mGoToWelcomeActivityRunnable);
        mChangeActivityHandler.removeCallbacks(mGoToMainActivityRunnable);
        mChangeActivityHandler.postDelayed(mShowLoadingRunnable, 2000);
        super.onResume();
    }
}

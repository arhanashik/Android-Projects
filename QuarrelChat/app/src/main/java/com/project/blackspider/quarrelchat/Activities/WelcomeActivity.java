package com.project.blackspider.quarrelchat.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.project.blackspider.quarrelchat.FinalClasses.FinalVariables;
import com.project.blackspider.quarrelchat.Fragments.Login_Fragment;
import com.project.blackspider.quarrelchat.R;
import com.project.blackspider.quarrelchat.Receiver.ConnectivityReceiver;
import com.project.blackspider.quarrelchat.Utils.CustomAnimation;

public class WelcomeActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    private TextView tvConnection, textView1, textView2;

    private Handler mHandler = new Handler();
    private boolean isConnected = false;

    private CustomAnimation customAnimation;

    private boolean isShowingConnected = false;
    private boolean isShowingConnecting = false;

    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if(Build.VERSION.SDK_INT>=21)
        {
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                    .inflateTransition(R.transition.shared_element_transation));
        }

        customAnimation = new CustomAnimation(this);
//
//        tvConnection = (TextView) findViewById(R.id.connectionTextView);
        textView1 = (TextView) findViewById(R.id.textViewWelcomeNote1);
        textView2 = (TextView) findViewById(R.id.textViewWelcomeNote2);
        customAnimation.crossFade(textView1, textView2);
//        checkConnection();
//        btnSignIn.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new Login_Fragment(),
                            FinalVariables.Login_Fragment).commit();
        }

        // On close icon click finish activity
        findViewById(R.id.img_app_logo).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        customAnimation.crossFade(textView1, textView2);

                    }
                });

        //Toast.makeText(this, "FCM ID: "+FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_SHORT).show();

        if (getIntent().getExtras() != null) {
            //Toast.makeText(this, "Soulmate:" + getIntent().getExtras().get("message"), Toast.LENGTH_SHORT ).show();
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(FinalVariables.TAG, "Key: " + key + " Value: " + value);
            }
        }

        if(getIntent().getBooleanExtra("EXIT", false)) {
            Intent intent = new Intent(WelcomeActivity.this, GreetingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            finish();
        }
    }

    // Replace Login Fragment with animation
    public void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.bottom_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        FinalVariables.Login_Fragment).commit();
    }

    // Replace Login Fragment with animation
    public void replaceForgotPasswordFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.left_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        FinalVariables.Login_Fragment).commit();
    }

    // Replace Login Fragment with animation
    public void replaceSoulmatesFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.zoom_in, R.anim.right_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        FinalVariables.Login_Fragment).commit();
    }

    @Override
    public void onBackPressed() {

        // Find the tag of signup and forgot password fragment
        Fragment SignUp_Fragment = fragmentManager
                .findFragmentByTag(FinalVariables.SignUp_Fragment);
        Fragment ForgotPassword_Fragment = fragmentManager
                .findFragmentByTag(FinalVariables.ForgotPassword_Fragment);
        Fragment Soulmates_Fragment = fragmentManager
                .findFragmentByTag(FinalVariables.Search_Soulmates_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUp_Fragment != null)
            replaceLoginFragment();
        else if (ForgotPassword_Fragment != null)
            replaceForgotPasswordFragment();
        else if (Soulmates_Fragment != null)
            replaceSoulmatesFragment();
        else {
            Intent intent = new Intent(WelcomeActivity.this, GreetingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            finish();
            //super.onBackPressed();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }

    // Method to manually check connection status
    private void checkConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                isConnected = ConnectivityReceiver.isConnected();
                                if (isConnected){
                                    tvConnection.setText("Connected");
                                    tvConnection.setBackgroundColor(Color.GREEN);
                                    //tvConnection.setVisibility(View.GONE);
                                    if(!isShowingConnected) customAnimation.slideUp(tvConnection);
                                    isShowingConnected = true;
                                    isShowingConnecting = false;
                                }else{
                                    tvConnection.setVisibility(View.VISIBLE);
                                    tvConnection.setText("Connecting...");
                                    tvConnection.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                    if(!isShowingConnecting) customAnimation.slideDown(tvConnection);
                                    isShowingConnecting = true;
                                    isShowingConnected = false;
                                }
                            }
                        });
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}

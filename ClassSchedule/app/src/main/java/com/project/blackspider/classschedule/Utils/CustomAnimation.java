package com.project.blackspider.classschedule.Utils;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.content.Context;

import com.project.blackspider.classschedule.R;

/**
 * Created by Mr blackSpider on 12/14/2016.
 */

public class CustomAnimation extends android.view.animation.Animation implements android.view.animation.Animation.AnimationListener{
    private android.view.animation.Animation animFadeIn;
    private android.view.animation.Animation animFadeOut;
    private android.view.animation.Animation animRoll;
    private android.view.animation.Animation animSlideUp;
    private android.view.animation.Animation animSlideDown;
    private android.view.animation.Animation animUp;
    private android.view.animation.Animation animDown;
    private Context context;
    private TextView textView1;
    private TextView textView2;
    private View v;
    private int flag = -1;

    public CustomAnimation(Context context) {
        this.context = context;
    }

    public void crossFade(TextView tv1, TextView tv2) {
        this.textView1 = tv1;
        this.textView2 = tv2;
        // load animations
        animFadeIn = AnimationUtils.loadAnimation(context,
                R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(context,
                R.anim.fade_out);

        // set animation listeners
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);

        // start fade in animation
        textView1.setVisibility(View.VISIBLE);
        textView1.startAnimation(animFadeOut);

        textView2.setVisibility(View.VISIBLE);
        // start fade out animation
        textView2.startAnimation(animFadeIn);

    }

    public void roll(View v){
        this.v = v;
        //load animation
        animRoll = AnimationUtils.loadAnimation(context,
                R.anim.roll);
        //set animation listener
        animRoll.setAnimationListener(this);

        v.startAnimation(animRoll);
    }

    public void slideUp(View v){
        this.v = v;
        //load animation
        animSlideUp = AnimationUtils.loadAnimation(context,
                R.anim.slide_up);
        //set animation listener
        animSlideUp.setAnimationListener(this);

        v.startAnimation(animSlideUp);
    }

    public void slideDown(View v){
        this.v = v;
        //load animation
        animSlideDown = AnimationUtils.loadAnimation(context,
                R.anim.slide_down);
        //set animation listener
        animSlideDown.setAnimationListener(this);
        v.startAnimation(animSlideDown);
    }

    public void animUp(View v){
        this.v = v;
        animUp = AnimationUtils.loadAnimation(context, R.anim.anim_up);
        v.setVisibility(View.VISIBLE);
        v.clearAnimation();
        animUp.setAnimationListener(this);
        v.startAnimation(animUp);
    }

    public void animDown(View v){
        this.v = v;
        animDown = AnimationUtils.loadAnimation(context, R.anim.anim_down);
        v.clearAnimation();
        animDown.setAnimationListener(this);
        v.startAnimation(animDown);
    }

    @Override
    public void onAnimationStart(android.view.animation.Animation animation) {
        if(animation == animFadeIn){
            textView1.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onAnimationEnd(android.view.animation.Animation animation) {
        if(animation == animDown) v.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(android.view.animation.Animation animation) {

    }
}

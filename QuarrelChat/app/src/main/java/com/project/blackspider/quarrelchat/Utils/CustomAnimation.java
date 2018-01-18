package com.project.blackspider.quarrelchat.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.content.Context;

import com.project.blackspider.quarrelchat.R;

import io.codetail.animation.ViewAnimationUtils;

/**
 * Created by Mr blackSpider on 12/14/2016.
 */

public class CustomAnimation extends android.view.animation.Animation implements android.view.animation.Animation.AnimationListener{
    private android.view.animation.Animation animFadeIn;
    private android.view.animation.Animation fadeIn;
    private android.view.animation.Animation fadeOut;
    private android.view.animation.Animation animFadeOut;
    private android.view.animation.Animation animRoll;
    private android.view.animation.Animation animSlideUp;
    private android.view.animation.Animation animSlideDown;
    private android.view.animation.Animation animUp;
    private android.view.animation.Animation animDown;
    private android.view.animation.Animation shakeAnimation;
    private android.view.animation.Animation leftEnter;
    private android.view.animation.Animation leftOut;
    private android.view.animation.Animation rightEnter;
    private android.view.animation.Animation rightOut;
    private android.view.animation.Animation bottomEnter;
    private android.view.animation.Animation bottomOut;
    private android.view.animation.Animation zoomIn;
    private android.view.animation.Animation zoomOut;
    private Context context;
    private TextView textView1;
    private TextView textView2;
    private View v;
    private int flag = -1;

    public CustomAnimation(Context context) {
        this.context = context;
    }

    public void animFadeIn(TextView tv1){
        this.textView1 = tv1;
        fadeIn = AnimationUtils.loadAnimation(context,
                R.anim.fade_in);
        fadeIn.setAnimationListener(this);

        textView1.startAnimation(fadeIn);
    }

    public void animFadeOut(TextView tv1){
        this.textView1 = tv1;
        fadeOut = AnimationUtils.loadAnimation(context,
                R.anim.fade_out);
        fadeOut.setAnimationListener(this);

        textView1.startAnimation(fadeOut);
    }

    public void shakeAnim(View v){
        shakeAnimation = AnimationUtils.loadAnimation(context,
                R.anim.shake);
        v.startAnimation(shakeAnimation);
    }

    public void animLeftEnter(View v){
        leftEnter = AnimationUtils.loadAnimation(context,
                R.anim.left_enter);
        v.startAnimation(leftEnter);
    }
    public void animLeftOut(View v){
        leftOut = AnimationUtils.loadAnimation(context,
                R.anim.left_out);
        v.startAnimation(leftOut);
    }

    public void animRightEnter(View v){
        rightEnter = AnimationUtils.loadAnimation(context,
                R.anim.right_enter);
        v.startAnimation(rightEnter);
    }
    public void animRightOut(View v){
        rightOut = AnimationUtils.loadAnimation(context,
                R.anim.right_out);
        v.startAnimation(rightOut);
    }

    public void animBottomEnter(View v){
        bottomEnter = AnimationUtils.loadAnimation(context,
                R.anim.bottom_enter);
        v.startAnimation(bottomEnter);
    }
    public void animBottomOut(View v){
        bottomOut = AnimationUtils.loadAnimation(context,
                R.anim.bottom_out);
        v.startAnimation(bottomOut);
    }

    public void animZoomIn(View v){
        zoomIn = AnimationUtils.loadAnimation(context,
                R.anim.zoom_in);
        v.startAnimation(zoomIn);
    }
    public void animZoomOut(View v){
        zoomOut = AnimationUtils.loadAnimation(context,
                R.anim.zoom_out);
        v.startAnimation(zoomOut);
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

    public void revealShow(final View view, boolean b, final Dialog dialog) {
        final int w = view.getLeft();
        final int h = view.getBottom();

        int endRadius = (int) Math.hypot(w, h);

        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        int radius = Math.max(view .getWidth(), view.getHeight());
        int finalRadius = (int) (Math.hypot(view.getWidth(), view.getHeight()));

        if (b) {
            // Android native animator
            Animator animator =
                    ViewAnimationUtils.createCircularReveal(view, w, h, 0, finalRadius);
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration(300);
            animator.start();

        } else {

            // Android native animator
            Animator animator =
                    ViewAnimationUtils.createCircularReveal(view, w, h, finalRadius, 0);
            animator.setInterpolator(new LinearInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            animator.setDuration(300);
            animator.start();

        }
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

        if(animation == animSlideUp) v.setVisibility(View.GONE);
        if(animation == animSlideDown) v.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(android.view.animation.Animation animation) {

    }
}

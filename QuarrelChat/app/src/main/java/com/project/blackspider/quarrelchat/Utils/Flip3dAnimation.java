package com.project.blackspider.quarrelchat.Utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Mr blackSpider on 6/6/2017.
 */

public class Flip3dAnimation extends Animation {
    private float mFromDegrees;
    private float mToDegrees;
    private float mCX;
    private float mCY;
    private Camera mCamera;

    public Flip3dAnimation(View view, float fromDegrees, float toDegrees){
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCX = view.getWidth()/2.0f;
        mCY = view.getHeight()/2.0f;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    public void applyPropertiesInRotation(int duration){
        this.setDuration(duration);
        this.setFillAfter(true);
        this.setInterpolator(new AccelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - mFromDegrees) * interpolatedTime);
        final float cX = mCX;
        final float cY = mCY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();

        camera.rotateY(degrees);

        camera.getMatrix(matrix);

        camera.restore();

        matrix.preTranslate(-cX, -cY);
        matrix.postTranslate(cX, cY);
    }
}

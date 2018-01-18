package com.project.blackspider.musician.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mr blackSpider on 9/14/2017.
 */

public class RhombusView extends ImageView {


    Paint mBoarderPaint;
    Paint mInnerPaint;

    public RhombusView(Context context) {
        super(context);
        init();
    }

    public RhombusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RhombusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        mBoarderPaint = new Paint();
        mBoarderPaint.setAntiAlias(true);
        mBoarderPaint.setColor(Color.BLACK);
        mBoarderPaint.setStyle(Paint.Style.STROKE);
        mBoarderPaint.setStrokeWidth(6);

        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setColor(Color.parseColor("#ffffff"));
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setStrokeJoin(Paint.Join.ROUND);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Path path = new Path();
        path.moveTo(0,0);
        path.lineTo(0, getHeight());
        path.lineTo(getWidth()/1.3f, getHeight());
        path.lineTo(getWidth(), 0);
        canvas.drawPath(path, mInnerPaint);
    }

}

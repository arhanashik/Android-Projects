package com.project.blackspider.musician.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by Mr blackSpider on 9/14/2017.
 */

public class RhombusDrawable extends Drawable {

    private Path rhombusInSide = new Path();
    private Paint paintOutSide = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintInSide = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int colorInside = Color.WHITE;
    private boolean fillInside = false;

    public RhombusDrawable(int colorInside) {
        this.colorInside = colorInside;
        paintInSide.setColor(this.colorInside);

        rhombusInSide.setFillType(Path.FillType.EVEN_ODD);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(rhombusInSide, paintInSide);
    }

    @Override
    public void setAlpha(int alpha) {
        paintOutSide.setAlpha(alpha);
        paintInSide.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paintOutSide.setColorFilter(cf);
        paintInSide.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        Paint p = paintOutSide;
        if (fillInside) p = paintInSide;
        if (p.getXfermode() == null) {
            final int alpha = p.getAlpha();
            if (alpha == 0) {
                return PixelFormat.TRANSPARENT;
            }
            if (alpha == 255) {
                return PixelFormat.OPAQUE;
            }
        }
        // not sure, so be safe
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        computeRhombus(bounds);
        invalidateSelf();
    }

    public void computeRhombus(Rect bounds) {

        final int width = bounds.width();
        final int height = bounds.height();
        rhombusInSide.reset();

        rhombusInSide.addPath(createRhombus(width, height));
    }

    private Path createRhombus(int width, int height) {
        Path path = new Path();
        path.moveTo(0,0);
        path.lineTo(0, height);
        path.lineTo(width/1.302f, height);
        path.lineTo(width, 0);

        return path;
    }
}
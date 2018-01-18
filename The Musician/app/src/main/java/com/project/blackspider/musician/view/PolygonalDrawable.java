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
 * Originally Created by AnderWeb (Gustavo Claramunt) on 7/10/14.
 */
public class PolygonalDrawable extends Drawable {

	private int numberOfSides = 3;
	private Path polygonOutSide = new Path();
	private Path polygonInSide = new Path();
	private Path polygonLeftSide = new Path();
	private Path polygonRightSide = new Path();
	private Path temporal = new Path();
	private Paint paintOutSide = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintInSide = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintLeftSide = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintRightSide = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int colorInside = Color.WHITE;
	private int colorOutside = Color.WHITE;
	private int colorLeftSide = Color.MAGENTA;
	private int colorRightSide = Color.YELLOW;
	private boolean fillInside = false;

	public PolygonalDrawable(int colorInside, int colorOutside, int sides, boolean fillInside) {
        this.colorInside = colorInside;
        this.colorOutside = colorOutside;
        if (fillInside){
            this.colorInside = colorOutside;
            this.colorOutside = colorInside;
        }

        paintInSide.setColor(this.colorInside);
        paintOutSide.setColor(this.colorOutside);
        paintLeftSide.setColor(colorLeftSide);
        paintRightSide.setColor(colorRightSide);

        polygonInSide.setFillType(Path.FillType.EVEN_ODD);
		polygonOutSide.setFillType(Path.FillType.EVEN_ODD);
        polygonLeftSide.setFillType(Path.FillType.EVEN_ODD);
        polygonRightSide.setFillType(Path.FillType.EVEN_ODD);

		this.numberOfSides = sides;

        this.fillInside = fillInside;
	}

	@Override
	public void draw(Canvas canvas) {
        if(fillInside) {
            canvas.drawPath(polygonInSide, paintInSide);
        }
        else canvas.drawPath(polygonOutSide, paintOutSide);

	}

	@Override
	public void setAlpha(int alpha) {
		paintOutSide.setAlpha(alpha);
		paintInSide.setAlpha(alpha);
        paintLeftSide.setAlpha(alpha);
        paintRightSide.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		paintOutSide.setColorFilter(cf);
		paintInSide.setColorFilter(cf);
		paintLeftSide.setColorFilter(cf);
		paintRightSide.setColorFilter(cf);
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
		computeHex(bounds);
		invalidateSelf();
	}

	public void computeHex(Rect bounds) {

		final int width = bounds.width();
		final int height = bounds.height();
		final int size = Math.min(width, height);
		final int centerX = bounds.left + (width / 2);
		final int centerY = bounds.top + (height / 2);

		polygonOutSide.reset();
		polygonInSide.reset();

		polygonOutSide.addPath(createHexagon((int) (size * 0.92f), centerX, centerY));
		polygonOutSide.addPath(createHexagon(size, centerX, centerY));

        polygonInSide.addPath(createHexagon((int) (size * 0.92f), centerX, centerY));

        polygonLeftSide.addPath(createHalfHexagon((int) (size * 0.92f), centerX, centerY, true));
        polygonLeftSide.addPath(createHalfHexagon(size, centerX, centerY, true));

        polygonRightSide.addPath(createHalfHexagon((int) (size * 0.92f), centerX, centerY, false));
        polygonRightSide.addPath(createHalfHexagon(size, centerX, centerY, false));
	}

	private Path createHexagon(int size, int centerX, int centerY) {
		final float section = (float) (2.0 * Math.PI / numberOfSides);
		int radius = size / 2;
		Path polygonPath = temporal;
		polygonPath.reset();
		polygonPath.moveTo((centerY + radius * (float)Math.sin(0)),
				(centerX + radius * (float)Math.cos(0)));

		for (int i = 1; i < numberOfSides; i++) {
			polygonPath.lineTo((centerY + radius * (float)Math.sin(section * i)),
					(centerX + radius * (float)Math.cos(section * i)));
		}

		polygonPath.close();
		return polygonPath;
	}

    private Path createHalfHexagon(int size, int centerX, int centerY, boolean isLeft) {
        final float section = (float) (2.0 * Math.PI / numberOfSides);
        int radius = size / 2;
        Path polygonPath = temporal;
        polygonPath.reset();
        polygonPath.moveTo((centerY + radius * (float)Math.sin(0)),
                (centerX + radius * (float)Math.cos(0)));

        int start = 1, end = 3;
        if(isLeft){
            start = 3; end = 6;
        }

        for (int i = start; i <= end; i++) {
            polygonPath.lineTo((centerY + radius * (float)Math.sin(section * i)),
                    (centerX + radius * (float)Math.cos(section * i)));
        }

        polygonPath.close();
        return polygonPath;
    }
}
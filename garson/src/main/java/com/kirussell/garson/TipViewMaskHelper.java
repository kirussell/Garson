package com.kirussell.garson;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by russellkim on 08/03/16.
 * Helper to generate and position mask
 */
public class TipViewMaskHelper {

    private int inset;

    public TipViewMaskHelper() {
        this.inset = 0;
    }

    private Point getViewLocation(View viewToTip, View areaView) {
        Point viewLocation = new Point();
        int[] location = new int[2];
        viewToTip.getLocationInWindow(location);
        viewLocation.set(location[0], location[1]);
        areaView.getLocationInWindow(location);
        viewLocation.x -= location[0];
        viewLocation.y -= location[1];
        return viewLocation;
    }

    Bitmap generateMask(final View viewToTip, @Nullable Drawable tipViewShape, int dimColor) {
        int width = viewToTip.getWidth() + 2 * inset;
        int height = viewToTip.getHeight() + 2 * inset;
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        if (tipViewShape != null) {
            tipViewShape.setBounds(0, 0, width, height);
            tipViewShape.draw(new Canvas(mask));
        } else {
            viewToTip.draw(new Canvas(mask));
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(dimColor);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawBitmap(mask, 0, 0, paint);
        mask.recycle();
        return result;
    }

    public Rect getMaskBounds(View viewToTip, View areaView) {
        Rect bounds = new Rect();
        Point loc = getViewLocation(viewToTip, areaView);
        bounds.set(loc.x, loc.y, loc.x + viewToTip.getWidth(), loc.y + viewToTip.getHeight());
        bounds.inset(-inset, -inset);
        return bounds;
    }

    public void setInset(int inset) {
        this.inset = inset;
    }
}

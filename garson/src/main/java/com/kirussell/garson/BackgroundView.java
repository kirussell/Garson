package com.kirussell.garson;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by russellkim on 07/02/16.
 * View to draw dim color with hole according to given mask
 */
public class BackgroundView extends View implements GestureDetector.OnGestureListener {

    private Paint paint;
    private int backgroundColor;
    private Bitmap mask;
    private Point maskLocation;
    private Rect maskBounds = new Rect();
    private Rect[] rects = new Rect[4];
    private Paint bgPaint;
    private ClickCallbacks clicks;
    private GestureDetectorCompat gestureDetectorCompat;

    BackgroundView(Context context) {
        this(context, null);
    }

    BackgroundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    BackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    BackgroundView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        paint = new Paint();
        for (int i = 0; i < rects.length; i++) {
            rects[i] = new Rect();
        }
        gestureDetectorCompat = new GestureDetectorCompat(getContext(), BackgroundView.this);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetectorCompat.onTouchEvent(event);
            }
        });
    }

    void setState(int backgroundColor, Bitmap mask, Point maskLocation) {
        this.backgroundColor = backgroundColor;
        this.mask = mask;
        this.maskLocation = maskLocation;
        bgPaint.setColor(this.backgroundColor);
        initBgRects(getWidth(), getHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initBgRects(w, h);
    }

    private void initBgRects(int w, int h) {
        if (maskLocation != null && mask != null && w > 0 && h > 0) {
            rects[0].set(0, 0, w, maskLocation.y);
            rects[1].set(0, maskLocation.y, maskLocation.x, h);
            rects[2].set(maskLocation.x + mask.getWidth(), maskLocation.y, w, h);
            rects[3].set(maskLocation.x, maskLocation.y + mask.getHeight(), maskLocation.x + mask.getWidth(), h);
            maskBounds.set(
                    maskLocation.x, maskLocation.y,
                    maskLocation.x + mask.getWidth(),
                    maskLocation.y + mask.getHeight()
            );
            invalidate();
        }
    }

    void setClickCallbacks(ClickCallbacks clicks) {
        this.clicks = clicks;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Rect rect : rects) {
            canvas.drawRect(rect, bgPaint);
        }
        if (mask != null) {
            canvas.drawBitmap(mask, maskLocation.x, maskLocation.y, paint);
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (clicks != null) {
            if (maskBounds.contains((int)e.getX(), (int)e.getY())) {
                clicks.onTipViewClicked(null);
            } else {
                clicks.onBackgroundClicked(null);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // empty
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // empty
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // empty
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // empty
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // empty
        return false;
    }
}

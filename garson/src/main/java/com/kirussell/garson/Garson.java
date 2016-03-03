package com.kirussell.garson;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kirussell.garson.ViewTreeObserverUtils.*;

/**
 * Created by russellkim on 26/01/16.
 */
public class Garson {

    private static final ViewGroup.LayoutParams PARAMS = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
    );
    private static final int GARSON_ID = R.id.garson_id;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private FrameLayout areaView;
    private View viewToTip;
    private BackgroundView backgroundView;
    private TextView hintTextView;
    private CharSequence withText;
    private int dimColor = Color.argb(122, 0, 0, 0);
    private int hintTextStyleResId;
    private int hintTextSizeDimenId;
    private int hintTextColorResId;
    private TextViewHelper hintTextHelper;
    private int padding;
    private Drawable tipViewShape;
    private ClickCallbacks clicks;

    private Garson(FrameLayout areaView) {
        this.areaView = areaView;
        padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16,
                this.areaView.getResources().getDisplayMetrics()
        );
        hintTextHelper = new TextViewHelper(areaView.getContext(), executor);
    }

    public static Garson in(final Activity area) {
        FrameLayout frame = new FrameLayout(area);
        area.getWindow().addContentView(
                frame,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        frame.setTag(GARSON_ID, Boolean.TRUE);
        return new Garson(frame);
    }

    public static Garson in(FrameLayout area) {
        area.setTag(GARSON_ID, Boolean.FALSE);
        return new Garson(area);
    }

    public Garson with(CharSequence text) {
        this.withText = text;
        return this;
    }

    public Garson with(CharSequence text, @DimenRes int textSizeResId, @ColorRes int textColorResId) {
        this.withText = text;
        this.hintTextSizeDimenId = textSizeResId;
        this.hintTextColorResId = textColorResId;
        return this;
    }

    public Garson with(CharSequence text, @StyleRes int styleResId) {
        this.withText = text;
        this.hintTextStyleResId = styleResId;
        return this;
    }

    public Garson withDimColor(int color) {
        this.dimColor = color;
        return this;
    }

    public Garson callback(ClickCallbacks clicks) {
        this.clicks = clicks;
        return this;
    }

    public void tip(View view, Drawable withShape) {
        tipViewShape = withShape;
        tip(view);
    }

    public void tip(View view) {
        viewToTip = view;
        insertTipView();
        insertTextView();
    }

    private void insertTextView() {
        hintTextView = hintTextHelper.createTextView(
                hintTextSizeDimenId, hintTextColorResId,
                hintTextStyleResId, withText
        );
        onPreDrawAction(new Runnable() {
            @Override
            public void run() {
                hintTextHelper.addTextView(
                        areaView,
                        new Point(viewToTip.getWidth(), viewToTip.getHeight()),
                        getMaskLocation(viewToTip, areaView), padding, hintTextView
                );
            }
        }, areaView, viewToTip);
        hintTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicks.onHintTextClicked(Garson.this);
            }
        });
    }

    private void insertTipView() {
        backgroundView = new BackgroundView(areaView.getContext());
        areaView.addView(backgroundView, PARAMS);
        onPreDrawAction(viewToTip, new Runnable() {
            @Override
            public void run() {
                backgroundView.setState(
                        dimColor,
                        generateAndSetMask(viewToTip, tipViewShape, dimColor),
                        getMaskLocation(viewToTip, areaView)
                );
            }
        });
        backgroundView.setClickCallbacks(new ClickCallbacksAdapter() {
            @Override
            public void onBackgroundClicked(Garson garson) {
                if (clicks != null) {
                    clicks.onBackgroundClicked(Garson.this);
                }
            }

            @Override
            public void onTipViewClicked(Garson garson) {
                if (clicks != null) {
                    clicks.onTipViewClicked(Garson.this);
                }
            }
        });
    }

    private Bitmap generateAndSetMask(final View viewToTip, @Nullable Drawable tipViewShape, int color) {
        int width = viewToTip.getWidth();
        int height = viewToTip.getHeight();
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        if (tipViewShape != null) {
            tipViewShape.setBounds(0, 0, width, height);
            tipViewShape.draw(new Canvas(mask));
        } else {
            viewToTip.draw(new Canvas(mask));
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(color);
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawBitmap(mask, 0, 0, paint);
        mask.recycle();
        return result;
    }

    private Point getMaskLocation(View viewToTip, View areaView) {
        Point maskLocation = new Point();
        int[] location = new int[2];
        viewToTip.getLocationInWindow(location);
        maskLocation.set(location[0], location[1]);
        areaView.getLocationOnScreen(location);
        maskLocation.x -= location[0];
        maskLocation.y -= location[1];
        return maskLocation;
    }

    public void dismiss() {
        onDestroy();
    }

    private void onDestroy() {
        if ((Boolean) areaView.getTag(GARSON_ID)) {
            ViewParent parent = areaView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(areaView);
            }
        } else {
            areaView.removeView(backgroundView);
            areaView.removeView(hintTextView);
        }
        backgroundView = null;
        areaView = null;
    }
}
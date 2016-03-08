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
import android.os.Build;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.kirussell.garson.ViewTreeObserverUtils.*;

/**
 * Created by russellkim on 26/01/16.
 * <p>
 * more info in README.md
 */
public class Garson {

    private static final ViewGroup.LayoutParams PARAMS = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
    );

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextViewHelper textHelper;

    private FrameLayout areaView;
    private View viewToTip;
    private BackgroundView backgroundView;
    private TextView tipTextView;
    private CharSequence withText;
    private int dimColor = Color.argb(122, 0, 0, 0);
    private int textStyleResId;
    private int textSizeDimenId;
    private int textColorResId;
    private int padding;
    private Drawable tipViewShape;
    private DismissClicksCallbackWrapper clicks;
    private int enterAnimation;
    private int exitAnimation;
    private ExtensionCallback extension;

    private Garson(FrameLayout areaView) {
        this.areaView = areaView;
        padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16,
                this.areaView.getResources().getDisplayMetrics()
        );
        textHelper = new TextViewHelper(areaView.getContext(), executor);
        clicks = new DismissClicksCallbackWrapper();
    }

    /**
     * Creates Garson to highlight something inside activity
     *
     * @param area to dim
     * @return created Gason obj
     */
    public static Garson in(final Activity area) {
        FrameLayout frame = new FrameLayout(area);
        area.getWindow().addContentView(frame, PARAMS);
        return new Garson(frame);
    }

    /**
     * Creates Garson to highlight something inside FrameLayout
     *
     * @param area to dim
     * @return created Garson obj
     */
    public static Garson in(FrameLayout area) {
        FrameLayout frame = new FrameLayout(area.getContext());
        area.addView(frame, PARAMS);
        return new Garson(area);
    }

    /**
     * Text to explain tip
     */
    public Garson with(CharSequence text) {
        this.withText = text;
        return this;
    }

    /**
     * Text to explain tip
     */
    public Garson with(CharSequence text, @DimenRes int textSizeResId, @ColorRes int textColorResId) {
        this.withText = text;
        this.textSizeDimenId = textSizeResId;
        this.textColorResId = textColorResId;
        return this;
    }

    /**
     * Text to explain tip
     */
    public Garson with(CharSequence text, @StyleRes int styleResId) {
        this.withText = text;
        this.textStyleResId = styleResId;
        return this;
    }

    /**
     * Color.argb(122,0,0,0) by default
     *
     * @param color for dim
     * @return self
     */
    public Garson withDimColor(int color) {
        this.dimColor = color;
        return this;
    }

    /**
     * Animations for dimming
     * default enter animation - circular reveal
     * default exit animation - fade out
     *
     * @param enterAnim animation to show views
     * @param exitAnim  animation to hide views
     * @return self
     */
    public Garson withAnimations(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        enterAnimation = enterAnim;
        exitAnimation = exitAnim;
        return this;
    }

    /**
     * By default tip will be dismissed when user clicks anywhere
     *
     * @param clicks callback to handle manually
     * @return self
     */
    public Garson callback(ClickCallbacks clicks) {
        this.clicks.setHost(clicks);
        return this;
    }

    public Garson callback(ExtensionCallback extension) {
        this.extension = extension;
        return this;
    }

    /**
     * Will show Garson-tip with mask obtained from shape
     *
     * @param view      object that will be highlighted
     * @param withShape shape to create mask
     */
    public void tip(View view, Drawable withShape) {
        tipViewShape = withShape;
        tip(view);
    }

    /**
     * Will show Garson-tip with mask obtained from view
     *
     * @param view object that will be highlighted
     */
    public void tip(View view) {
        viewToTip = view;
        insertTipView();
        insertTextView();
        if (extension != null) {
            extension.onTipCreation(this, areaView);
            extension = null;
        }
    }

    private void insertTextView() {
        if (!TextUtils.isEmpty(withText)) {
            tipTextView = textHelper.createTextView(
                    textSizeDimenId, textColorResId,
                    textStyleResId, withText
            );
            onPreDrawAction(new Runnable() {
                @Override
                public void run() {
                    textHelper.addTextView(
                            areaView,
                            new Point(viewToTip.getWidth(), viewToTip.getHeight()),
                            getViewLocation(viewToTip, areaView), padding, tipTextView
                    );
                }
            }, areaView, viewToTip);
            tipTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicks.onHintTextClicked(Garson.this);
                }
            });
        }
    }

    private void insertTipView() {
        backgroundView = new BackgroundView(areaView.getContext());
        areaView.addView(backgroundView, PARAMS);
        onPreDrawAction(new Runnable() {
            @Override
            public void run() {
                Point viewLocation = getViewLocation(viewToTip, areaView);
                backgroundView.setState(
                        dimColor,
                        generateMask(viewToTip, tipViewShape, dimColor),
                        viewLocation
                );
                animateBackgroundReveal(backgroundView, viewLocation, viewToTip, areaView);
            }
        }, viewToTip, areaView);
        backgroundView.setClickCallbacks(new ClickCallbacksAdapter() {
            @Override
            public void onBackgroundClicked(Garson garson) {
                clicks.onBackgroundClicked(Garson.this);
            }

            @Override
            public void onTipViewClicked(Garson garson) {
                clicks.onTipViewClicked(Garson.this);
            }
        });
    }

    private void animateBackgroundReveal(BackgroundView backgroundView, Point viewLocation,
                                         View viewToTip, View areaView) {
        if (enterAnimation > 0) {
            AnimationUtils.loadAnimation(backgroundView.getContext(), enterAnimation).start();
        } else {
            int startRadius = Math.min(viewToTip.getWidth(), viewToTip.getHeight());
            int endRadius = Math.max(areaView.getWidth(), areaView.getHeight());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewAnimationUtils.createCircularReveal(
                        backgroundView,
                        viewLocation.x + viewToTip.getWidth() / 2,
                        viewLocation.y + viewToTip.getHeight() / 2,
                        startRadius, endRadius
                ).start();
            }
        }
    }

    private Bitmap generateMask(final View viewToTip, @Nullable Drawable tipViewShape, int color) {
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

    private Point getViewLocation(View viewToTip, View areaView) {
        Point viewLocation = new Point();
        int[] location = new int[2];
        viewToTip.getLocationInWindow(location);
        viewLocation.set(location[0], location[1]);
        areaView.getLocationOnScreen(location);
        viewLocation.x -= location[0];
        viewLocation.y -= location[1];
        return viewLocation;
    }

    /**
     * Closes Garson tip and cleans related objects
     */
    public void dismiss() {
        Animation anim;
        if (exitAnimation > 0) {
            anim = AnimationUtils.loadAnimation(areaView.getContext(), exitAnimation);
        } else {
            anim = AnimationUtils.loadAnimation(areaView.getContext(), android.R.anim.fade_out);
        }
        anim.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                onDestroy();
            }
        });
        areaView.startAnimation(anim);
    }

    private void onDestroy() {
        ViewParent parent = areaView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(areaView);
        }
        backgroundView = null;
        tipTextView = null;
        viewToTip = null;
        areaView = null;
    }

}

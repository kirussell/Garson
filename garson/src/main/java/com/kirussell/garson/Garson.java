package com.kirussell.garson;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnimRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
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
 * <p/>
 * more info in README.md
 */
public class Garson {

    private static final ViewGroup.LayoutParams PARAMS = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
    );

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextViewHelper textHelper;
    private TipViewMaskHelper maskHelper;

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
    private int tipViewShapeInsetDimen;
    private DismissClicksCallbackWrapper clicks;
    private int enterAnimation;
    private int exitAnimation;
    private ExtensionCallback extension;
    private OnDestroyListener onDestroyListener;

    private Garson(FrameLayout areaView) {
        this.areaView = areaView;
        padding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16,
                this.areaView.getResources().getDisplayMetrics()
        );
        textHelper = new TextViewHelper(areaView.getContext(), executor);
        maskHelper = new TipViewMaskHelper();
        clicks = new DismissClicksCallbackWrapper();
    }

    /**
     * Creates Garson to highlight something inside activity
     *
     * @param area to dim
     * @return created Gason obj
     */
    public static Garson in(final Activity area) {
        return new Garson(createFrameForGarson(area));
    }

    static FrameLayout createFrameForGarson(Activity area) {
        FrameLayout frame = new FrameLayout(area);
        area.getWindow().addContentView(frame, PARAMS);
        return frame;
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

    public Garson withShape(Drawable shape) {
        return withShape(shape, 0);
    }

    public Garson withShape(Drawable shape, @DimenRes int insetDimen) {
        tipViewShape = shape;
        tipViewShapeInsetDimen = insetDimen;
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
     * @param view  object that will be highlighted
     * @param shape shape to create mask
     */

    /**
     * Will show Garson-tip with mask obtained from view
     *
     * @param view object that will be highlighted
     */
    public void tip(View view) {
        viewToTip = view;
        if (tipViewShapeInsetDimen > 0) {
            maskHelper.setInset(
                    viewToTip.getResources().getDimensionPixelSize(tipViewShapeInsetDimen)
            );
        }
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
                    textHelper.addTextView(areaView, maskHelper.getMaskBounds(viewToTip, areaView),
                            padding, tipTextView);
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
                Rect maskBounds = maskHelper.getMaskBounds(viewToTip, areaView);
                backgroundView.setState(
                        dimColor,
                        maskHelper.generateMask(viewToTip, tipViewShape, dimColor),
                        new Point(maskBounds.left, maskBounds.top)
                );
                animateBackgroundReveal(backgroundView, maskBounds, areaView);
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

    private void animateBackgroundReveal(BackgroundView backgroundView, Rect maskBounds, View areaView) {
        if (enterAnimation > 0) {
            backgroundView.startAnimation(
                    AnimationUtils.loadAnimation(backgroundView.getContext(), enterAnimation)
            );
        } else {
            int width = maskBounds.width();
            int height = maskBounds.height();
            int startRadius = Math.min(width, height);
            int endRadius = Math.max(areaView.getWidth(), areaView.getHeight());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewAnimationUtils.createCircularReveal(
                        backgroundView,
                        maskBounds.left + width / 2,
                        maskBounds.top + height / 2,
                        startRadius, endRadius
                ).start();
            }
        }
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
        if (onDestroyListener != null) {
            onDestroyListener.onDestroy();
        }
    }

    void setOnDestroyListener(OnDestroyListener listener) {
        onDestroyListener = listener;
    }

    interface OnDestroyListener {
        void onDestroy();
    }
}

package com.kirussell.garson;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kirussell.garson.compat.ContextCompat;

import java.util.concurrent.Executor;

/**
 * Created by russellkim on 23/02/16.
 * Helper methods to create text inside tip layout
 */
public class TextViewHelper {

    private final Context ctx;
    private final Executor executor;
    private final Handler uiHandler;

    public TextViewHelper(Context context, Executor executor) {
        this.ctx = context;
        this.executor = executor;
        this.uiHandler = new Handler(Looper.myLooper());
    }

    @NonNull
    @UiThread
    TextView createTextView(@DimenRes int textSizeDimenId,
                            @ColorRes int textColorResId,
                            @StyleRes int textWithStyle,
                            CharSequence text) {
        final TextView textView = new TextView(ctx, null, textWithStyle);
        if (textSizeDimenId > 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    ctx.getResources().getDimensionPixelSize(textSizeDimenId));
        }
        if (textColorResId > 0) {
            textView.setTextColor(ContextCompat.getColor(ctx, textColorResId));
        } else {
            textView.setTextColor(Color.WHITE);
        }
        textView.setText(text);
        return textView;
    }

    @UiThread
    void addTextView(final FrameLayout container, final Rect maskBounds,
                     final int padding, final TextView text) {
        final Point areaSize = new Point(container.getWidth(), container.getHeight());
        final Typeface typeface = text.getTypeface();
        final float textSize = text.getTextSize();
        final float spaceadd = text.getLineSpacingExtra();
        final float spacemult = text.getLineSpacingMultiplier();
        final CharSequence content = text.getText();
        executor.execute(new Runnable() {
            @Override
            @WorkerThread
            public void run() {
                final TextViewHelper.TextMetrics metrics = createTextMetrics(
                        areaSize,
                        maskBounds,
                        content,
                        textSize, typeface, spacemult, spaceadd,
                        padding
                );
                if (metrics != null) {
                    final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(metrics.margins.left, metrics.margins.top,
                            metrics.margins.right, metrics.margins.bottom);
                    uiHandler.post(new Runnable() {
                        @Override
                        @UiThread
                        public void run() {
                            text.setMaxLines(metrics.maxLines);
                            container.addView(text, params);
                        }
                    });
                } else {
                    Log.e("Garson", "No room for text, try to display it manually");
                }
            }
        });
    }

    @Nullable
    TextMetrics createTextMetrics(Point areaSize, Rect maskBounds,
                                  CharSequence text, float textSize, Typeface textTypeface,
                                  float spacingmult, float spacingadd, int padding) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        paint.setTypeface(textTypeface);
        int areaWidth = areaSize.x - 2 * padding;
        int areaHeight = areaSize.y - 2 * padding;
        int leftAreaWidth = maskBounds.left - 2 * padding;
        int rightAreaWidth = areaSize.x - maskBounds.left - maskBounds.width() - 2 * padding;
        int topAreaHeight = maskBounds.top - 2 * padding;
        int bottomAreaHeight = areaSize.y - maskBounds.top - maskBounds.height() - 2 * padding;

        Rect margins = new Rect();
        TextMetrics textMetrics = null;
        StaticLayout sl;
        sl = new StaticLayout(text, paint, areaWidth, Layout.Alignment.ALIGN_NORMAL, spacingmult, spacingadd, false);
        final int linesCount = sl.getLineCount();
        int textWidth = linesCount == 1 ? (int) paint.measureText(text.toString()) : sl.getWidth();
        int textHeight = sl.getHeight();
        if (topAreaHeight > bottomAreaHeight) {
            if (textHeight <= topAreaHeight) {
                // top
                calcPositionX(maskBounds, areaWidth, textWidth, padding, margins);
                margins.top = maskBounds.top - textHeight - padding;
                textMetrics = new TextMetrics(margins, linesCount);
            }
        } else {
            if (textHeight < bottomAreaHeight) {
                // bottom
                calcPositionX(maskBounds, areaWidth, textWidth, padding, margins);
                margins.top = maskBounds.top + maskBounds.height() + textHeight + padding;
                textMetrics = new TextMetrics(margins, linesCount);
            }
        }
        if (textMetrics == null) {
            if (leftAreaWidth > rightAreaWidth) {
                // left
                sl = new StaticLayout(text, paint, leftAreaWidth, Layout.Alignment.ALIGN_NORMAL, spacingmult, spacingadd, false);
                margins.left = maskBounds.left - padding - textWidth;
            } else {
                // right
                sl = new StaticLayout(text, paint, rightAreaWidth, Layout.Alignment.ALIGN_NORMAL, spacingmult, spacingadd, false);
                margins.left = maskBounds.left + maskBounds.width() + padding;
            }
            margins.top = maskBounds.top + (maskBounds.height() - textHeight) / 2;
            if (sl.getHeight() <= areaHeight) {
                textMetrics = new TextMetrics(margins, linesCount);
            }
        }
        return textMetrics;
    }

    private void calcPositionX(Rect maskBounds, int areaWidth, int textWidth, int padding, Rect margins) {
        if (areaWidth == textWidth) {
            margins.left = padding;
            margins.right = padding;
            return;
        }
        int pos = maskBounds.left + (maskBounds.width() - textWidth) / 2;
        if (areaWidth < pos + textWidth) {
            pos = pos - (pos + textWidth - areaWidth);
        }
        margins.left = pos;
    }

    static class TextMetrics {
        Rect margins;
        int maxLines;

        private TextMetrics(Rect bounds, int maxLines) {
            this.margins = bounds;
            this.maxLines = maxLines;
        }
    }
}

package com.kirussell.garson;

import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by russellkim on 23/02/16.
 */
public class ViewTreeObserverUtils {

    static void onPreDrawAction(final Runnable action, final View... views) {
        final int length = views.length;
        Runnable sharedAction = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                count++;
                if (count >= length) {
                    action.run();
                }
            }
        };
        for (View view : views) {
            onPreDrawAction(view, sharedAction);
        }
    }

    static void onPreDrawAction(final View view, final Runnable action) {
        if (view.getWidth() > 0 && view.getHeight() > 0) {
            action.run();
        } else {
            ViewTreeObserver vto = view.getViewTreeObserver();
            if (vto.isAlive()) {
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        ViewTreeObserver vto = view.getViewTreeObserver();
                        if (vto.isAlive()) {
                            vto.removeOnPreDrawListener(this);
                            action.run();
                        }
                        return true;
                    }
                });
            }
        }
    }
}

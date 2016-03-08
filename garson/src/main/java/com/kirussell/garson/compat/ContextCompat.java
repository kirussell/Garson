package com.kirussell.garson.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;

/**
 * Created by russellkim on 08/03/16.
 * Compat methods from support v4
 */
public class ContextCompat {

    public static int getColor(Context context, @ColorRes int color) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.M) {
            return getColorMarshmallow(context, color);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(color);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static int getColorMarshmallow(Context context, @ColorRes int color) {
        return context.getColor(color);
    }
}

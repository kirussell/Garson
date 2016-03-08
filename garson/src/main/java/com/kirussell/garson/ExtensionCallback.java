package com.kirussell.garson;

import android.widget.FrameLayout;

/**
 * Created by russellkim on 08/03/16.
 * Callbacks to extend tip ui with own views and logic
 */
public interface ExtensionCallback {
    
    void onTipCreation(Garson garson, FrameLayout tipArea);
}

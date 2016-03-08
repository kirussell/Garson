package com.kirussell.garson;

/**
 * Created by russellkim on 08/03/16.
 * Dismisses garson on clicks or delegates callback to host
 */
class DismissClicksCallbackWrapper implements ClickCallbacks {

    private ClickCallbacks host;

    void setHost(ClickCallbacks host) {
        this.host = host;
    }

    @Override
    public void onHintTextClicked(Garson garson) {
        if (host != null) {
            host.onHintTextClicked(garson);
        } else {
            garson.dismiss();
        }
    }

    @Override
    public void onBackgroundClicked(Garson garson) {
        if (host != null) {
            host.onBackgroundClicked(garson);
        } else {
            garson.dismiss();
        }
    }

    @Override
    public void onTipViewClicked(Garson garson) {
        if (host != null) {
            host.onTipViewClicked(garson);
        } else {
            garson.dismiss();
        }
    }
}

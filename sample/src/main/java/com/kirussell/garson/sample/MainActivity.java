package com.kirussell.garson.sample;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kirussell.garson.ClickCallbacks;
import com.kirussell.garson.ClickCallbacksAdapter;
import com.kirussell.garson.Garson;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView iv = (ImageView) findViewById(R.id.icon);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Garson.in(MainActivity.this)
                        .with("Just simple example", R.dimen.textSize, R.color.colorAccent)
                        .withDimColor(Color.argb(122, 255, 0, 0))
                        .callback(new ClickCallbacksAdapter() {
                            @Override
                            public void onBackgroundClicked(Garson garson) {
                                garson.dismiss();
                            }
                        })
                        .tip(iv, ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_launcher));
            }
        });
        iv.performClick();

        final TextView tv = (TextView) findViewById(R.id.ninjaText);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Garson.in(MainActivity.this)
                        .with(getString(R.string.hidden_text_tip), R.dimen.textSize, R.color.colorPrimary)
                        .withDimColor(Color.argb(122, 0, 0, 0))
                        .callback(new ClickCallbacksAdapter() {
                            @Override
                            public void onTipViewClicked(Garson garson) {
                                garson.dismiss();
                            }
                        })
                        .tip(tv);
            }
        });

    }
}

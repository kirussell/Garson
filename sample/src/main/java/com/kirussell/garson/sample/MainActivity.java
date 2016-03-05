package com.kirussell.garson.sample;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kirussell.garson.ClickCallbacksAdapter;
import com.kirussell.garson.Garson;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView iv = (ImageView) findViewById(R.id.icon);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightLogo(v);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iv.performClick();
            }
        }, 1500);
    }

    private void highlightLogo(final View v) {
        Garson.in(MainActivity.this)
                .with(getString(R.string.hint_android_logo))
                .callback(new ClickCallbacksAdapter() {
                    @Override
                    public void onBackgroundClicked(Garson garson) {
                        garson.dismiss();
                        highlightVest(v);
                    }
                })
                .tip(v);
    }

    private void highlightVest(final View v) {
        Garson.in(MainActivity.this)
                .with(getString(R.string.hint_android_logo_vest), R.dimen.textSize, 0)
                .withDimColor(ContextCompat.getColor(this, R.color.vestDimColor))
                .callback(new ClickCallbacksAdapter() {
                    @Override
                    public void onTipViewClicked(Garson garson) {
                        garson.dismiss();
                        if (v instanceof ImageView) {
                            ((ImageView) v).setImageResource(R.drawable.vest);
                        }
                        highlightNinjaText();
                    }
                })
                .tip(v, ContextCompat.getDrawable(MainActivity.this, R.drawable.vest));
    }

    private void highlightNinjaText() {
        View text = findViewById(R.id.ninjaText);
        Garson.in(MainActivity.this)
                .with(getString(R.string.hidden_text_tip), R.dimen.textSize, 0)
                .callback(new ClickCallbacksAdapter() {
                    @Override
                    public void onHintTextClicked(Garson garson) {
                        garson.dismiss();
                    }
                })
                .tip(text);
    }
}

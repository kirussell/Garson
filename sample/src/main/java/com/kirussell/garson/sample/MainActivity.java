package com.kirussell.garson.sample;

import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.kirussell.garson.ClickCallbacksAdapter;
import com.kirussell.garson.ExtensionCallback;
import com.kirussell.garson.Garson;

public class MainActivity extends AppCompatActivity {

    private View ninjaText;

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
        ninjaText = findViewById(R.id.ninjaText);
        ninjaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highlightNinjaText();
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
        Garson.in(this)
                .with(getString(R.string.hint_android_logo))
                .callback(new ClickCallbacksAdapter() {
                    @Override
                    public void onBackgroundClicked(Garson garson) {
                        garson.dismiss();
                        highlightWithExtension(v);
                    }
                })
                .tip(v);
    }

    private void highlightWithExtension(final View logo) {
        Garson.in(this)
                .with(getString(R.string.hint_android_logo_ext))
                .callback(new ExtensionCallback() {
                    @Override
                    public void onTipCreation(final Garson garson, FrameLayout tipArea) {
                        View area = View.inflate(tipArea.getContext(), R.layout.custom_tip_ui, tipArea);
                        area.findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View btn) {
                                garson.dismiss();
                                highlightVest(logo);
                            }
                        });
                    }
                })
                .tip(logo);
    }

    private void highlightVest(final View v) {
        Garson.in(this)
                .with(getString(R.string.hint_android_logo_vest), R.dimen.textSize, 0)
                .withDimColor(ContextCompat.getColor(this, R.color.vestDimColor))
                .callback(new ClickCallbacksAdapter() {
                    @Override
                    public void onTipViewClicked(Garson garson) {
                        garson.dismiss();
                        if (v instanceof ImageView) {
                            ((ImageView) v).setImageResource(R.drawable.vest);
                        }
                        highlightShapeWithOffset(v);
                    }
                })
                .withShape(ContextCompat.getDrawable(MainActivity.this, R.drawable.vest))
                .tip(v);
    }

    private void highlightShapeWithOffset(final View v) {
        Garson.in(this)
                .with(getString(R.string.hint_android_logo_shape_offset), R.dimen.textSize, 0)
                .withDimColor(ContextCompat.getColor(this, R.color.vestDimColor))
                .callback(new ClickCallbacksAdapter() {
                    @Override
                    public void onTipViewClicked(Garson garson) {
                        garson.dismiss();
                        highlightNinjaText();
                    }
                })
                .withShape(
                        ContextCompat.getDrawable(MainActivity.this, R.drawable.circle_shape),
                        R.dimen.shape_offset
                )
                .tip(v);
    }

    private void highlightNinjaText() {
        Garson.in(this)
                .with(getString(R.string.hidden_text_tip), R.dimen.textSize, 0)
                .callback(new ClickCallbacksAdapter() {
                    @Override
                    public void onHintTextClicked(Garson garson) {
                        garson.dismiss();
                        hightlightWithCustomAnimations(ninjaText);
                    }
                })
                .tip(ninjaText);
    }

    private void hightlightWithCustomAnimations(View v) {
        Garson.in(this)
                .with(getString(R.string.custom_animations_tip))
                .withAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .tip(v);
    }
}

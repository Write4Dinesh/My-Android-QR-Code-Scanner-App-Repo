package com.sgdm.qrscanner.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;
import com.sgdm.qrscanner.R;
import com.sgdm.qrscanner.analytics.SGGoogleAnalytics;
import com.sgdm.qrscanner.util.SGUtils;


public class SGSplashActivity extends SGSuperActivity {
    public static final long LOAD_ANIMATION_DELAY_IN_MILLIS = 2 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SGUtils.configLocale(this);
        setContentView(R.layout.activity_splash);
        ImageView loadingIv = (ImageView) findViewById(R.id.splash_image_view);
        loadingIv.postDelayed(new Runnable() {
            @Override
            public void run() {
                launchHomeScreen();
            }
        }, LOAD_ANIMATION_DELAY_IN_MILLIS);
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SGGoogleAnalytics.sendScreenViewEvent("Splash Screen");
    }

    private void launchHomeScreen() {
        Intent homeIntent = new Intent(this, SGHomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}

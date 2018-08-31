package com.sgdm.qrscanner.analytics;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by shrinvigroup on 25/03/2018.
 */

public class SGGoogleAnalytics {
    private static final String GA_TRACKER_ID = "qr-scanner-5e4b4";
    private static Tracker mTracker;


    synchronized public static void init(Context context) {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            mTracker = analytics.newTracker(GA_TRACKER_ID);
            mTracker.enableExceptionReporting(true);
            mTracker.enableAutoActivityTracking(true);
        }
    }

    public static void sendScreenViewEvent(String screenName) {
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void sendCustomEvent(String eventCategory, String eventName) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(eventCategory)
                .setAction(eventName)
                .build());
    }
}

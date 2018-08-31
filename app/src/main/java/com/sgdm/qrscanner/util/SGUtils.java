package com.sgdm.qrscanner.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by shrinvigroup on 24/03/2018.
 */

public class SGUtils {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        }
        return ((activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) || (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE));
    }
    public  static void configLocale(Context context) {
        Locale myLocale = new Locale("kn");
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
    public static void showDialog(Context context, String errorMessage, String buttonLabel) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
          final AlertDialog mAlertDialog = alertDialogBuilder.create();
        mAlertDialog.setMessage(errorMessage);
        mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, buttonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog.show();
    }
}

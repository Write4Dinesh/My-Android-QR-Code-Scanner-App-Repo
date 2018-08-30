package com.sgdm.qrscanner;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.blikoon.qrcodescanner.QrCodeActivity;

public class SGHomeActivity extends AppCompatActivity {
    private Button mScanButton;
    private boolean mIsScanTapped = false;
    String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    int PERM_REQUEST_CODE = 200;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final String LOGTAG = "SGHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScanButton = (Button) findViewById(R.id.button_start_scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBothPermissionsGranted()) {
                    doScan();
                } else {
                    mIsScanTapped = true;
                    requestForPermission();
                }
            }
        });

    }

    private void doScan() {
        //Start the qr scan activity
        Intent i = new Intent(SGHomeActivity.this, QrCodeActivity.class);
        startActivityForResult(i, REQUEST_CODE_QR_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Log.d(LOGTAG, "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(SGHomeActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            //Getting the passed result
            final String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            Log.d(LOGTAG, "Have scan result in your app activity :" + result);
            AlertDialog alertDialog = new AlertDialog.Builder(SGHomeActivity.this).create();
            alertDialog.setTitle("Scan result");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Share",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            share(result);
                        }
                    });
            alertDialog.show();

        }
    }

    private void share(String str) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, str);
        startActivity(Intent.createChooser(sharingIntent, "choose apps to share"));
    }

    private void requestForPermission() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !isBothPermissionsGranted()) {
            boolean showRationale1 = ActivityCompat.shouldShowRequestPermissionRationale(this, perms[0]);
            boolean showRationale2 = ActivityCompat.shouldShowRequestPermissionRationale(this, perms[1]);
            if (showRationale1 && showRationale2) {
                ActivityCompat.requestPermissions(this, perms, PERM_REQUEST_CODE);
            } else {
                //TODO: redirect the user to app settings so that they can enable both permissons from there.
                showAlert();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        if (permsRequestCode == PERM_REQUEST_CODE) {
            boolean storagePermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean cameraPermissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (mIsScanTapped && storagePermissionGranted && cameraPermissionGranted) {
                mIsScanTapped = false;
                doScan();
            }
        }
    }

    private boolean isBothPermissionsGranted() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), perms[0]);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), perms[1]);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void showAlert() {
        final String result = "The Camera & Storage permissions are required to permform this operations. Please enable the permissions" +
                "from the App settings.";
        AlertDialog alertDialog = new AlertDialog.Builder(SGHomeActivity.this).create();
        alertDialog.setTitle("Permission Required");
        alertDialog.setMessage(result);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Goto App Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openAppSettings();
                    }
                });
        alertDialog.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 120);
    }
}

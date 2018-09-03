package com.sgdm.qrscanner.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.sgdm.qrscanner.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SGHomeActivity extends AppCompatActivity {
    private Button mScanButton;
    private Button mSearchOnWebBtn;
    private Button mShareBtn;
    private Button mCopyBtn;
    private TextView mBarcodeTv;
    private boolean mIsScanTapped = false;
    String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"};
    int PERM_REQUEST_CODE = 200;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private final String LOGTAG = "SGHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBarcodeTv = (TextView) findViewById(R.id.barcode_data_tv);
        mScanButton = (Button) findViewById(R.id.button_start_scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areBothPermissionsGranted()) {
                    doScan();
                } else {
                    mIsScanTapped = true;
                    requestForPermission();
                }
            }
        });
        mSearchOnWebBtn = (Button) findViewById(R.id.search_button);
        mSearchOnWebBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBarcodeTv != null && mBarcodeTv.getText() != null && !mBarcodeTv.getText().toString().isEmpty()) {
                    searchWeb((mBarcodeTv.getText().toString()));
                } else {
                    showToast("Invalid Data");
                }
            }
        });

        mShareBtn = (Button) findViewById(R.id.share_button);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBarcodeTv != null && mBarcodeTv.getText() != null && !mBarcodeTv.getText().toString().isEmpty()) {
                    shareWithOtherApps(mBarcodeTv.getText().toString());
                } else {
                    showToast("Invalid Data");
                }
            }
        });
        mCopyBtn = (Button) findViewById(R.id.copy_button);
        mCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBarcodeTv != null && mBarcodeTv.getText() != null && !mBarcodeTv.getText().toString().isEmpty()) {
                    copyToClip(mBarcodeTv.getText().toString());
                } else {
                    showToast("Invalid Data");
                }
            }
        });

    }

    private void searchWeb(String query) {
        String escapedQuery = null;
        try {
            escapedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
                alertDialog.setTitle(getString(R.string.scan_error_title));
                alertDialog.setMessage(getString(R.string.scan_error_body));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_label_close),
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
            mBarcodeTv.setText(result);
        }
    }

    private void shareWithOtherApps(String qrcodeData) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, qrcodeData);
        startActivity(Intent.createChooser(sharingIntent, "choose apps to shareWithOtherApps"));
    }

    private void requestForPermission() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            boolean showRationale1 = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
            boolean showRationale2 = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1]);
            if (showRationale1 && showRationale2) {
                //Redirect the user to app settings so that they can enable both permissons from there.
                showAppSettingsDialog();
            } else {
                ActivityCompat.requestPermissions(this, permissions, PERM_REQUEST_CODE);
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

    private boolean areBothPermissionsGranted() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void showAppSettingsDialog() {
        final String message = getString(R.string.why_permissions_required);
        AlertDialog alertDialog = new AlertDialog.Builder(SGHomeActivity.this).create();
        alertDialog.setTitle(getString(R.string.permission_required));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.btn_label_close),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.goto_app_settings),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openAppSettings();
                    }
                });
        alertDialog.show();
    }

    private void copyToClip(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("CLIP_LABEL", text);
        clipboard.setPrimaryClip(clip);
        showToast(getString(R.string.copied_to_clipboard));
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 120);
    }
}

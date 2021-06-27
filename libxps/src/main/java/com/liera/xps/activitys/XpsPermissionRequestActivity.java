package com.liera.xps.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.liera.xps.interfaces.XpsCallback;
import com.liera.xps.utils.XpsUtil;

import java.util.ArrayList;

public class XpsPermissionRequestActivity extends AppCompatActivity {

    private static final String REQUEST_PERMISSIONS = "REQUEST_PERMISSIONS";
    private static final String REQUEST_CODE = "REQUEST_CODE";

    private static final int REQUEST_CODE_DEFAULT = 0;

    private static XpsCallback permissionXCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            this.finish();
            return;
        }

        String[] requestPermissions = intent.getStringArrayExtra(REQUEST_PERMISSIONS);
        int requestCode = intent.getIntExtra(REQUEST_CODE, REQUEST_CODE_DEFAULT);
        if (permissionXCallback == null || requestPermissions == null || requestCode == REQUEST_CODE_DEFAULT) {
            try {
                throw new Exception("");
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.finish();
            return;
        }

        /**
         * 版本小于23直接通过
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.finish();
            permissionXCallback.permissionXSuccess();
            return;
        }

        /**
         * 检查权限是否全部赋予
         */
        if (XpsUtil.checkPermissions(this, requestPermissions)) {
            this.finish();
            permissionXCallback.permissionXSuccess();
            return;
        }

        /**
         * 开始申请权限
         */
        ActivityCompat.requestPermissions(this, requestPermissions, requestCode);
    }

    public static void launchActivity(Context context, String[] values, int requestCode, XpsCallback callback) {
        permissionXCallback = callback;

        Bundle bundle = new Bundle();
        bundle.putStringArray(REQUEST_PERMISSIONS, values);
        bundle.putInt(REQUEST_CODE, requestCode);

        Intent intent = new Intent();
        intent.setClass(context, XpsPermissionRequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /**
         * 判断权限是否全部通过
         */
        if (XpsUtil.checkPermissionResult(grantResults)) {
            this.finish();
            permissionXCallback.permissionXSuccess();
            return;
        }

        ArrayList<String> notShowLists = new ArrayList<>();
        ArrayList<String> showLists = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(permission))
                    notShowLists.add(permission);
                else
                    showLists.add(permission);
            }
        }

        String[] showPermissions = new String[showLists.size()];
        if (!showLists.isEmpty())
            showLists.toArray(showPermissions);

        if (!notShowLists.isEmpty()) {
            String[] notShowPermissions = new String[notShowLists.size()];
            notShowLists.toArray(notShowPermissions);

            finish();
            permissionXCallback.permissionXDenied(showPermissions, notShowPermissions);
            return;
        }

        finish();
        permissionXCallback.permissionXCancel(showPermissions);
        return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionXCallback = null;
    }
}

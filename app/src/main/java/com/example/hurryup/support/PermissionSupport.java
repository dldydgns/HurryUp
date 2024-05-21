package com.example.hurryup.support;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionSupport {
    public static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1001;

    public static boolean checkBluetoothPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionState = context.checkSelfPermission(Manifest.permission.BLUETOOTH);
            return permissionState == PackageManager.PERMISSION_GRANTED;
        } else {
            // 안드로이드 버전이 M 미만인 경우 Bluetooth 권한이 항상 허용된 것으로 간주합니다.
            return true;
        }
    }

    public static void requestBluetoothPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, BLUETOOTH_PERMISSION_REQUEST_CODE);
        }
    }

    // 다른 권한 확인 및 요청 메서드를 추가할 수 있습니다.
}

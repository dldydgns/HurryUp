package com.example.hurryup.support;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hurryup.service.BluetoothService;

import org.jetbrains.annotations.NotNull;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class PermissionSupport {
    private static final String[] permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,
            Manifest.permission.FOREGROUND_SERVICE_REMOTE_MESSAGING,
            Manifest.permission.POST_NOTIFICATIONS
    };
    private static List<String> permissionList;
    public static final int PERMISSION_REQUEST_CODE = 1001;

    public static boolean checkPermission(Context context) {
        int result;
        permissionList = new ArrayList<>();

        for(String pm : permissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(pm);
            }
        }
        return permissionList.isEmpty();
    }

    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, (String[])permissionList.toArray(new String[permissionList.size()]), PERMISSION_REQUEST_CODE);
    }

    public static boolean permissionResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults){
        if(requestCode == PERMISSION_REQUEST_CODE && (grantResults.length > 0)){
            for(int grantResult : grantResults){
                if(grantResult == -1){
                    return false;
                }
            }
        }
        return true;
    }
}
package com.example.hurryup.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.hurryup.MainActivity;
import com.example.hurryup.R;

import java.util.UUID;

public class BluetoothService extends Service {

    private static final String DEVICE_ADDRESS = "00:00:00:00:00:00"; // HC-06 모듈의 Bluetooth 주소
    private static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP UUID

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothThread bluetoothThread;

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
    }

    private void startForegroundService() {
        // Foreground 서비스를 위한 Notification 생성
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Bluetooth Service")
                .setContentText("Bluetooth 서비스 실행 중")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build();

        // 권한 체크 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION); // 포그라운드 서비스 시작
            } else {
                // 권한이 없는 경우 처리
                // 예를 들어 사용자에게 권한을 요청하는 다이얼로그를 표시하거나 다른 조치를 취할 수 있습니다.
            }
        } else {
            // Android 12 미만 버전에서는 추가 권한이 필요하지 않음
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Bluetooth 기능 초기화
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth를 지원하지 않거나 활성화되어 있지 않으면 서비스를 중지
            stopSelf();
        }
        startBluetoothConnection();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스 종료 시 Bluetooth 연결 종료
        if (bluetoothThread != null) {
            bluetoothThread.cancel();
        }
    }

    // Bluetooth 연결 시작
    private void startBluetoothConnection() {
        // Bluetooth 디바이스 가져오기
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
        // Bluetooth 연결 스레드 시작
        bluetoothThread = new BluetoothThread(bluetoothDevice, DEVICE_UUID, this); // context를 전달
        bluetoothThread.start();
    }

}

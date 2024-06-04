package com.example.hurryup.service;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.hurryup.MainActivity;
import com.example.hurryup.R;
import com.example.hurryup.support.PermissionSupport;
import com.example.hurryup.ui.home.HomeFragment;

import java.util.Set;
import java.util.UUID;

public class BluetoothService extends Service {
    private static String DEVICE_ADDRESS; // = "00:23:04:00:19:FA"; // HC-06 모듈의 Bluetooth 주소
    private static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP UUID

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothThread bluetoothThread;

    NotificationManager manager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager = getSystemService(NotificationManager.class);
            NotificationChannel serviceChannel = new NotificationChannel(
                    "bluetooth_service_channel",
                    "bluetooth_service_channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void startForegroundService() {
        // Foreground 서비스를 위한 Notification 생성
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, "bluetooth_service_channel")
                .setContentTitle("Bluetooth Service")
                .setContentText("Bluetooth 서비스 실행 중")
                .setSmallIcon(R.drawable.ic_home_black_24dp)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();
        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION); // 포그라운드 서비스 시작
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Bluetooth 기능 초기화
        bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth를 지원하지 않거나 활성화되어 있지 않으면 서비스를 중지
            Toast.makeText(getApplicationContext(), "블루투스를 실핼할 수 없습니다.", Toast.LENGTH_SHORT);
            stopSelf();
        }

        startBluetoothConnection();
        createNotificationChannel();
        startForegroundService();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // 서비스 종료 시 Bluetooth 연결 종료
        if (bluetoothThread != null) {
            bluetoothThread.cancel();
        }
        stopSelf();
        Toast.makeText(this, "블루투스 서비스 종료", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    // Bluetooth 연결 시작
    private void startBluetoothConnection() {
        // Bluetooth 디바이스 가져오기
        DEVICE_ADDRESS = MainActivity.DEVICE_ADDRESS;
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
        // Bluetooth 연결 스레드 시작
        BluetoothServiceHandler handler = new BluetoothServiceHandler();
        bluetoothThread = new BluetoothThread(bluetoothDevice, DEVICE_UUID, this, handler); // context를 전달
        bluetoothThread.start();
    }

    class BluetoothServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
                case 1:
                    Toast.makeText(BluetoothService.this, "블루투스 연결 성공", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(BluetoothService.this, "블루투스 연결에 실패했습니다\n쿠션 상태를 확인해주세요", Toast.LENGTH_SHORT).show();
                    stopSelf();
                    break;
                case 0:
                    // STRETCH_TIME 초 이상 착석중인 경우
                    Toast.makeText(BluetoothService.this, "스트레칭 시간입니다!", Toast.LENGTH_SHORT).show();
                    if(HomeFragment.Haptic) {
                        VibratorManager vibratorManager = (VibratorManager) getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                        Vibrator vibrator = vibratorManager.getDefaultVibrator();
                        vibrator.vibrate(VibrationEffect.createOneShot(1000,200));
                    }
                    break;
            }
        }
    }
}

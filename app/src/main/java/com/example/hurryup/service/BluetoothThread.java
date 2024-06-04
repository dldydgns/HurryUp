package com.example.hurryup.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.hurryup.MainActivity;
import com.example.hurryup.database.Converters;
import com.example.hurryup.database.User;
import com.example.hurryup.database.UserRepository;
import com.example.hurryup.support.PermissionSupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.UUID;

public class BluetoothThread extends Thread {
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Context context;
    private Handler mainActivityHandler;

    // 스트레칭 관련 변수들(단위 : 초)
    private static final int STRETCH_TIME = 5;   // 스트레칭 알람 주기
    private static final int LEAVING_THRESHOLD = 60;    // 앉아있었다고 판별하는 최소시간
    private int stretchTimer;
    private int leavingTimer;

    // BluetoothThread.java
    public BluetoothThread(BluetoothDevice device, UUID uuid, Context context, Handler handler) { // context를 받도록 수정
        BluetoothSocket tmp = null;
        mainActivityHandler = handler;

        this.context = context; // context를 초기화
        try {
            if (PermissionSupport.checkPermission(context)) {
                tmp = device.createRfcommSocketToServiceRecord(uuid);
            } else {
                Log.e("BluetoothThread", "Bluetooth 권한이 없습니다.");
            }
        } catch (IOException e) {
            Log.e("BluetoothThread", "Socket's create() method failed", e);
        }
        bluetoothSocket = tmp;
    }

    @Override
    public void run() {
        if (bluetoothSocket == null) {
            Log.e("BluetoothThread", "BluetoothSocket이 null입니다. 연결을 시작할 수 없습니다.");
            return;
        }

        try {
            // BluetoothSocket 연결 시도
            if (PermissionSupport.checkPermission(context)) {
                bluetoothSocket.connect();
                Log.d("BluetoothThread", "BluetoothSocket 연결 성공");

                // InputStream과 OutputStream 가져오기
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();

                // 블루투스 연결 성공
                mainActivityHandler.sendEmptyMessage(1);
                readData(); // 데이터 수신 메서드 호출
            } else {
                Log.e("BluetoothThread", "Bluetooth 권한이 없습니다.");
            }
        } catch (IOException connectException) {
            // 연결 실패 시 예외 처리
            Log.e("BluetoothThread", "BluetoothSocket 연결 실패", connectException);
            mainActivityHandler.sendEmptyMessage(-1);
            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                Log.e("BluetoothThread", "BluetoothSocket 닫기 실패", closeException);
            }
            return;
        }
    }

    // 데이터 수신 메서드
    private void readData() {
        byte[] buffer = new byte[4]; // 데이터를 읽을 버퍼 크기 설정
        int bytes; // 읽은 바이트 수를 저장할 변수
        UserRepository userRepository = new UserRepository(context); // UserRepository 인스턴스 생성

        stretchTimer = 0;
        leavingTimer = 0;
        while (true) {
            try {
                // InputStream에서 데이터 읽기
                bytes = inputStream.read(buffer);
                // 읽은 데이터 처리
                if (bytes != -1) {
                    // 읽은 데이터가 있는 경우 처리
                    int receivedState = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).getInt(); // 바이트 배열을 int로 변환
                    Log.d("BluetoothThread", "Received data: " + receivedState);

                    // 착석중이 아닌경우
                    if (receivedState == 0) {
                        // 일어서있는 시간 증가
                        leavingTimer++;

                        // LEAVING_THRESHOLD 초 이상 자리를 비우면
                        if(leavingTimer > LEAVING_THRESHOLD) {
                            // 어떠한 활동을 했다고 판단하여 타이머 초기화
                            stretchTimer = 0;
                            leavingTimer = 0;
                        }
                    }
                    else {
                        // 데이터를 DB에 저장하기 위한 코드 추가
                        User user = new User();
                        user.timestamp = Converters.dateToTimestamp(new Date());
                        user.state = receivedState;

                        // Room 데이터베이스에 저장
                        userRepository.insert(user);

                        // 앉아있는시간 초기화
                        leavingTimer = 0;
                        // 착석시간 증가
                        stretchTimer++;
                        // 1시간 이상 착석중인경우
                        if(stretchTimer > STRETCH_TIME) {
                            // UI 업데이트 요청
                            mainActivityHandler.sendEmptyMessage(0);
                            stretchTimer = 0;
                        }
                    }
                }
            } catch (IOException e) {
                Log.e("BluetoothThread", "Error reading from InputStream", e);
                break;
            }
        }
    }


    // 소켓을 닫는 메서드
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.e("BluetoothThread", "Could not close the connect socket", e);
        }
    }
}

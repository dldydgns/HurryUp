package com.example.hurryup.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.example.hurryup.database.User;
import com.example.hurryup.database.UserRepository;
import com.example.hurryup.support.PermissionSupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothThread extends Thread {
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Context context;

    // BluetoothThread.java
    public BluetoothThread(BluetoothDevice device, UUID uuid, Context context) { // context를 받도록 수정
        BluetoothSocket tmp = null;
        this.context = context; // context를 초기화
        try {
            if (PermissionSupport.checkBluetoothPermission(context)) {
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
            if (PermissionSupport.checkBluetoothPermission(context)) {
                bluetoothSocket.connect();
                Log.d("BluetoothThread", "BluetoothSocket 연결 성공");

                // InputStream과 OutputStream 가져오기
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();

                // 데이터 송수신을 원하는 경우 여기에 코드 추가
                readData(); // 데이터 수신 메서드 호출
            } else {
                Log.e("BluetoothThread", "Bluetooth 권한이 없습니다.");
            }
        } catch (IOException connectException) {
            // 연결 실패 시 예외 처리
            Log.e("BluetoothThread", "BluetoothSocket 연결 실패", connectException);
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
        byte[] buffer = new byte[1024]; // 데이터를 읽을 버퍼 크기 설정
        int bytes; // 읽은 바이트 수를 저장할 변수
        UserRepository userRepository = new UserRepository(context); // UserRepository 인스턴스 생성

        while (true) {
            try {
                // InputStream에서 데이터 읽기
                bytes = inputStream.read(buffer);
                // 읽은 데이터 처리
                if (bytes != -1) {
                    // 읽은 데이터가 있는 경우 처리
                    String receivedData = new String(buffer, 0, bytes);
                    Log.d("BluetoothThread", "Received data: " + receivedData);

                    // 받은 데이터를 처리하고 DB에 저장하는 코드 추가
                    // 예시: Bluetooth로부터 받은 데이터를 상태 정보로 가정하여 DB에 저장
                    int receivedState = Integer.parseInt(receivedData);

                    // 데이터를 DB에 저장하기 위한 코드 추가
                    User user = new User();
                    user.timestamp = System.currentTimeMillis(); // 현재 시간을 timestamp로 설정
                    user.state = receivedState;

                    // Room 데이터베이스에 저장
                    userRepository.insert(user);
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

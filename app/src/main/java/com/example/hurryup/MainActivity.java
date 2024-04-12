package com.example.hurryup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.hurryup.support.PermissionSupport;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hurryup.databinding.ActivityMainBinding;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private PermissionSupport permission;
    public BluetoothAdapter mBluetoothAdapter;


    // launcher 선언
    private ActivityResultLauncher<Intent> mStartForResult ;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //여기서도 리턴이 false로 들어온다면 (사용자가 권한 허용 거부)
        if (!permission.permissionResult(requestCode, permissions, grantResults)) {
            // 다시 permission 요청
            permission.requestPermission();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void permissionCheck() {
        // PermissionSupport.java 클래스 객체 생성
        permission = new PermissionSupport(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission()) {
            //권한 요청
            permission.requestPermission();
        }
    }

    private void SetBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 장치가 블루투스를 지원하지 않는 경우.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스 미지원 기기입니다.", Toast.LENGTH_LONG).show();

            // Safe종료
            finish();
            return;
        }

        // 블루투스가 비활성화 상태 (기기에 블루투스가 꺼져있음)
        if (!mBluetoothAdapter.isEnabled()) {
            if (!permission.checkPermission()) {
                permission.requestPermission();
            }
            Intent bIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(bIntent, 0); -> 런처로 대체
            mStartForResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        //result.getResultCode()를 통하여 결과값 확인
                        if(result.getResultCode() == RESULT_OK) {
                            //ToDo
                        }
                        if(result.getResultCode() == RESULT_CANCELED){
                            // 블루투스 켤때까지 물어봄
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            mStartForResult.launch(enableBtIntent);
                        }
                    }
            );
            mStartForResult.launch(bIntent);
        }


    }

    private void SetNavigation(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionCheck();
        SetBluetooth();
        SetNavigation();
    }

}
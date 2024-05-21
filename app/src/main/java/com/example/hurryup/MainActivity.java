package com.example.hurryup;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.example.hurryup.service.BluetoothService;
import com.example.hurryup.support.PermissionSupport;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hurryup.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1001;
    private static final int BLUETOOTH_ENABLE_REQUEST_CODE = 1002;


    private void setNavigation() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.container);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            // Bluetooth 권한 요청 결과 처리
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우 Bluetooth 활성화 상태 확인
                checkBluetoothEnabledAndStartService();
            } else {
                // 권한이 거부된 경우 처리
                // 필요한 작업 수행
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_ENABLE_REQUEST_CODE) {
            // 사용자가 Bluetooth를 켰는지 확인
            if (resultCode == RESULT_OK) {
                // 사용자가 Bluetooth를 켰으면 BluetoothService 시작
                startBluetoothService();
            } else {
                // 사용자가 Bluetooth를 켜지 않은 경우 처리
                // 예: 앱 종료 또는 다른 작업 수행
            }
        }
    }

    private void startBluetoothService() {
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);
    }

    private void checkBluetoothEnabledAndStartService() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth가 비활성화된 경우 Bluetooth 활성화를 요청
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            PermissionSupport.checkBluetoothPermission(this);
            startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_REQUEST_CODE);
        } else {
            // Bluetooth가 활성화된 경우 BluetoothService 시작
            startBluetoothService();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bluetooth 권한 확인
        if (!PermissionSupport.checkBluetoothPermission(this)) {
            // Bluetooth 권한이 없는 경우 권한 요청
            PermissionSupport.requestBluetoothPermission(this);
        } else {
            // Bluetooth 활성화 상태 확인 및 서비스 시작
            checkBluetoothEnabledAndStartService();
        }

        setNavigation();
    }
}

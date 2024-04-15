package com.example.hurryup;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.hurryup.support.PermissionSupport;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hurryup.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private PermissionSupport permission;
    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;


    // launcher 선언
    private ActivityResultLauncher<Intent> mStartForResult ;

    // 블루투스 페어링
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> adapter;

    public void selectPairedDevice() {
        if (!permission.checkPermission()) {
            permission.requestPermission();
        }
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("장치 선택");

        List<String> pairedList = new ArrayList<>();
        for(BluetoothDevice device : pairedDevices) {
            pairedList.add(device.getName());
        }
        pairedList.add("취소");

        // 페어링된 장치중에 "HurryUp"문자열을 포함하는 장치가 있는지 확인.
        // 없다면 블루투스 설정페이지를 열고 프로그램 종료
        if(!pairedList.stream().anyMatch(element -> element.contains("HurryUp"))){
            Toast.makeText(this, "최초 사용시 블루투스 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);
            finish();
            return;
        }

        final CharSequence[] devices = pairedList.toArray(new CharSequence[pairedList.size()]);
        builder.setItems(devices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == pairedList.size()-1) {
                    // 취소버튼 클릭
                } else {
                    if (!permission.checkPermission()) {
                        permission.requestPermission();
                    }
                    // 페어링된 기기 클릭 했을 때 그 기기 연결
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 블루투스 권한 설정 함수들
    private void permissionCheck() {
        // PermissionSupport.java 클래스 객체 생성
        permission = new PermissionSupport(this, this);

        // 권한 체크 후 리턴이 false로 들어오면
        if (!permission.checkPermission()) {
            //권한 요청
            permission.requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //리턴이 true로 들어온다면 (사용자가 권한 허용)
        if (permission.permissionResult(requestCode, permissions, grantResults)) {
            SetBluetooth();
        }
        else {
            // 한번 거부한 권한은 직접 권한을 허용해야 앱 실행 가능하다 알림
            Toast.makeText(getApplicationContext(), "설정->권한에 있는 모든 권한을 허용해주세요.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);

            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void SetBluetooth() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // 장치가 블루투스를 지원하지 않는 경우.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스 미지원 기기입니다.", Toast.LENGTH_LONG).show();
            // Safe종료
            finish();
            return;
        }

        // 블루투스가 비활성화 상태 (기기에 블루투스가 꺼져있음)
        if (!mBluetoothAdapter.isEnabled()) {
            // 블루투스를 활성화 하도록 안내
            Intent bIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mStartForResult = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if(result.getResultCode() == RESULT_CANCELED){
                            // 블루투스 켤때까지 물어봄
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            mStartForResult.launch(enableBtIntent);
                        }
                    }
            );
            mStartForResult.launch(bIntent);
        }


        selectPairedDevice();
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
        SetNavigation();
    }
}
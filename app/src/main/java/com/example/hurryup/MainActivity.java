package com.example.hurryup;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.hurryup.database.UserRepository;
import com.example.hurryup.service.BluetoothService;
import com.example.hurryup.support.PermissionSupport;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hurryup.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static String DEVICE_ADDRESS;
    private ActivityMainBinding binding;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.bluetooth) {
            checkBluetoothEnabledAndStartService();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    private void setNavigation() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.container);

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
        if(!PermissionSupport.permissionResult(requestCode, permissions, grantResults)) {
            Toast.makeText(this, "설정->권한에 있는 모든 권한을 허용해주세요", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);

            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);// 액티비티 매니져를 통해 작동중인 서비스 가져오기

        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {// 작동중인 서비스수 만큼 반복
            if(BluetoothService.class.getName().equals(service.service.getClassName())) {// 비교한 서비스의 이름이 MyForgroundService 와 같다면
                return true;// true 반환
            }
        }
        return false;// 기본은 false 로 설정
    }

    public void selectPairedDevice() {
        if (PermissionSupport.checkPermission(this)) {
            BluetoothAdapter bluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("장치 선택");

            List<String> pairedList = new ArrayList<>();
            for (BluetoothDevice device : pairedDevices) {
                pairedList.add(device.getName());
            }
            pairedList.add("취소");

            // 페어링된 장치중에 "HurryUp"문자열을 포함하는 장치가 있는지 확인.
            // 없다면 블루투스 설정페이지를 열고 프로그램 종료
           if(!pairedList.stream().anyMatch(element -> element.contains("HurryUp"))){
               Toast.makeText(this, "최초 사용시 블루투스 페어링 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
               startActivity(intent);
               finish();
               return;
           }

            final CharSequence[] devices = pairedList.toArray(new CharSequence[pairedList.size()]);
            builder.setItems(devices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == pairedList.size() - 1) {
                        // 취소버튼 클릭
                        Toast.makeText(getApplicationContext(), "블루투스 연결 취소", Toast.LENGTH_SHORT).show();
                    } else {
                        if (PermissionSupport.checkPermission(getApplicationContext())) {
                            for (BluetoothDevice device : pairedDevices) {
                                if (devices[which].equals(device.getName())) {
                                    DEVICE_ADDRESS = device.getAddress();
                                    break;
                                }
                            }

                            if(!foregroundServiceRunning()){
                                Intent serviceIntent = new Intent(MainActivity.this, BluetoothService.class);
                                startService(serviceIntent);
                            }
                        }
                    }
                }
            });
            builder.setCancelable(false);
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            PermissionSupport.requestPermission(this);
        }
    }

    private void startBluetoothService() {
        // 페어링할 기기 선택 후 블루투스 서비스 실행
        selectPairedDevice();
    }

    private ActivityResultLauncher<Intent> enableBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // 사용자가 Bluetooth를 켰으면 BluetoothService 시작
                    startBluetoothService();
                } else {
                    // 사용자가 Bluetooth를 켜지 않은 경우 처리
                    // 예: 앱 종료 또는 다른 작업 수행
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("[안내]")
                            .setMessage("Bluetooth를 연결하지 않으면 착석 데이터를 받아올 수 없습니다.")
                            .setPositiveButton("네, 통계 데이터만 확인하겠습니다", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 사용자가 '네'를 선택한 경우, 특별한 처리 없이 다이얼로그만 닫습니다.
                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("블루투스를 켜겠습니다", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    PermissionSupport.checkPermission(getApplicationContext());
                                    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                                    bluetoothManager.getAdapter().enable();
                                }
                            })
                            .setCancelable(false) // 뒤로 가기 버튼으로 다이얼로그를 닫지 못하도록 설정
                            .show();
                }
            });

    private void checkBluetoothEnabledAndStartService() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth가 비활성화된 경우 Bluetooth 활성화를 요청
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        } else {
            // Bluetooth가 활성화된 경우 BluetoothService 시작
            startBluetoothService();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigation();

        if (!PermissionSupport.checkPermission(this)) {
            // Bluetooth 권한이 없는 경우 권한 요청
            PermissionSupport.requestPermission(this);
        }
    }
}

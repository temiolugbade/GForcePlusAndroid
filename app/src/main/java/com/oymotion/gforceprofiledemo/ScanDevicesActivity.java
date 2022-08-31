package com.oymotion.gforceprofiledemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oymotion.gforceprofile.GForceProfile;
import com.oymotion.gforceprofile.ScanCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import butterknife.OnClick;

public class ScanDevicesActivity extends AppCompatActivity {
    @BindView(R.id.scan_toggle_btn)
    Button scanToggleButton;
    @BindView(R.id.btn_next)
    Button nextButton;
    @BindView(R.id.scan_results)
    RecyclerView recyclerView;
    TextView leftSelected;
    TextView rightSelected;
    private ScanResultsAdapter resultsAdapter;


    private GForceDatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;


    private final static String TAG = ScanDevicesActivity.class.getSimpleName();
    private BluetoothAdapter bluetoothAdapter;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private GForceProfile gForceProfile;
    private final int ACCESS_LOCATION = 1;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
    private boolean isScanning = false;

    private SharedPreferences preferences;
    private SharedPreferences .Editor editor;

    private int count_selected;
    int itemBackgroundColor;
//    private String extra_device_name_l;
//    private String extra_mac_address_l;
//    private String extra_device_name_r;
//    private String extra_mac_address_r;
    private List<Device> btList;



    @SuppressLint("WrongConstant")
    private void getPermission() {
        int permissionCheck = 0;
        permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionCheck += this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionCheck += this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            //未获得权限
            this.requestPermissions( // 请求授权
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ACCESS_LOCATION);// 自定义常量,任意整型
        }
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);
        ButterKnife.bind(this);
        this.setTitle("Search for Armbands");

        leftSelected = findViewById(R.id.tv_left_select);
        rightSelected = findViewById(R.id.tv_right_select);
        configureResultList();
        getPermission();

        try {
            dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
            db.delete("Device", null, null);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        /*
        initialized the data
         */
        //set up the view background color of clicked item
        itemBackgroundColor = ContextCompat.getColor(ScanDevicesActivity.this, R.color.selected);
        btList = new ArrayList<Device>();
        count_selected = 0;
        nextButton.setEnabled(false);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        gForceProfile = new GForceProfile(new GForceProfile.GForceErrorCallback(){
            @Override
            public void onGForceErrorCallback(String errorMsg) {
                Toast.makeText(ScanDevicesActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.scan_toggle_btn)
    public void onScanToggleClick() {

        if (isScanning) {
            gForceProfile.stopScan();
            isScanning = false;
        } else {
            scanBleDevices();
        }

        updateButtonUIState();
    }

    // when both armbands are selected, click next to transfer to "SetupDevicesActivity"
    // paras：
    // EXTRA_DEVICE_NAME_L,
    // EXTRA_MAC_ADDRESS_L,
    // EXTRA_DEVICE_NAME_R,
    // EXTRA_MAC_ADDRESS_R,
    @OnClick(R.id.btn_next)
    public void onNextClick() {
        if((btList.size() == 2)){
            final Intent intent = new Intent(this, SetupDevicesActivity.class);
//            intent.putExtra(SetupDevicesActivity.EXTRA_DEVICE_NAME_L, btList.get(0).getName());
//            intent.putExtra(SetupDevicesActivity.EXTRA_MAC_ADDRESS_L, btList.get(0).getMacAddress());
//            intent.putExtra(SetupDevicesActivity.EXTRA_DEVICE_NAME_R, btList.get(1).getName());
//            intent.putExtra(SetupDevicesActivity.EXTRA_MAC_ADDRESS_R, btList.get(1).getMacAddress());
            updateSharePreference();

            startActivity(intent);
        }else{
            Log.i(TAG,"wrong devices number");
        }

    }

    private void updateSharePreference() {

        if((btList.size() == 2)){
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            editor = preferences.edit();
            for (Device d : btList) {
                if(d.getType() == 0){
                    editor.putString("extra_device_name_l", d.getDevice_name());
                    editor.putString("extra_mac_address_l", d.getMac_address());
                }else if(d.getType() == 1){
                    editor.putString("extra_mac_address_r", d.getDevice_name());
                    editor.putString("extra_device_name_r", d.getMac_address());
                }
            }
            editor.apply();
            editor.clear();

        }else{
            Log.i(TAG,"wrong devices number");
        }
    }



    private void scanBleDevices() {
        resultsAdapter.clearScanResults();

        gForceProfile.startScan(new ScanCallback() {
            @Override
            public void onScanResult(BluetoothDevice bluetoothDevice, int rssi) {
                runOnUiThread(() -> {
                    //Log.d(TAG, "Device discovered: " + bluetoothDevice.toString() + ", Rssi:" + rssi);

                    if (bluetoothDevice != null && bluetoothDevice.getName() != null &&
                            bluetoothDevice.getName().contains("gForce")) {
                        Log.i(TAG, "Device discovered: " + bluetoothDevice.toString() + ", Rssi:" + rssi);

                        bluetoothDevices.add(bluetoothDevice);

                        resultsAdapter.addScanResult(new ScanResult(bluetoothDevice, rssi));
                    }
                });
            }

            @Override
            public void onScanFailed(int i) {
                Log.e(TAG, "Error code: " + i);
            }
        });

        isScanning = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_LOCATION:
                if (hasAllPermissionGranted(grantResults)) {
                    Log.i(TAG, "onRequestPermissionsResult: 用户允许权限");
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: 拒绝搜索设备权限");
                }
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isScanning) {
            /*
             * Stop scanning in onPause callback.
             */
            isScanning = false;
            gForceProfile.stopScan();
        }
    }

    private void configureResultList() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        resultsAdapter = new ScanResultsAdapter();
        recyclerView.setAdapter(resultsAdapter);
        resultsAdapter.setOnAdapterItemClickListener(view -> {
            final int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            final ScanResult itemAtPosition = resultsAdapter.getItemAtPosition(childAdapterPosition);
            onAdapterItemClick(itemAtPosition);
            //when selected, item view is highlighted
            view.setBackgroundColor(itemBackgroundColor);
        });
    }

    private void onAdapterItemClick(ScanResult scanResults) {
//        final Intent intent = new Intent(this, DeviceActivity.class);
//        intent.putExtra(DeviceActivity.EXTRA_DEVICE_NAME, scanResults.getBluetoothDevice().getName());
//        intent.putExtra(DeviceActivity.EXTRA_MAC_ADDRESS, scanResults.getBluetoothDevice().getAddress());
//        startActivity(intent);

        //test for save bluetooth devices info and transfer
        String extra_device_name = scanResults.getBluetoothDevice().getName();
        String extra_mac_address = scanResults.getBluetoothDevice().getAddress();
        int left_hand = 0;
        int right_hand = 1;
        boolean left_hand_added = Device.checkDeviceLROther(db, String.valueOf(left_hand));
        boolean right_hand_added = Device.checkDeviceLROther(db, String.valueOf(right_hand));
        if(!isSelected(extra_mac_address)) {
            if (!left_hand_added){
                leftSelected.setBackground(getDrawable(R.drawable.round_purple));
                Device.insertDeviceInfo(db, left_hand, extra_device_name, extra_mac_address, left_hand);
                btList.add(new Device(extra_device_name,extra_mac_address,left_hand));
                count_selected ++;
            }else if(!right_hand_added){
                rightSelected.setBackground(getDrawable(R.drawable.round_purple));
                Device.insertDeviceInfo(db, left_hand, extra_device_name, extra_mac_address, right_hand);
                btList.add(new Device(extra_device_name,extra_mac_address,right_hand));
                count_selected ++;
            }


        }
        if(count_selected == 2){
            nextButton.setEnabled(true);
        }

    }

    private void updateButtonUIState() {
        scanToggleButton.setText(isScanning ? R.string.stop_scan : R.string.start_scan);
    }

    private boolean isSelected(String extra_mac_address) {
        for(Device bluetooth : btList){
            if(bluetooth.isSame(extra_mac_address)){
                return true;
            }
        }
        return false;
    }

}
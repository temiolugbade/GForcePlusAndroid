package com.oymotion.newgforceprofiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oymotion.gforceprofile.CommandResponseCallback;
import com.oymotion.gforceprofile.DataNotificationCallback;
import com.oymotion.gforceprofile.GForceProfile;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetupDevicesActivity extends AppCompatActivity {

    @BindView(R.id.connect_l)
    Button btn_connect_l;
    @BindView(R.id.connect_r)
    Button btn_connect_r;
//    @BindView(R.id.start)
//    Button btn_start;
//    @BindView(R.id.get_firmware_version)
//    Button btn_getFirmwareVersion;
    @BindView(R.id.set)
    Button btn_set;

    @BindView(R.id.btn_next1)
    Button btn_next;

    boolean set_left = false;
    boolean set_right = false;

    private SharedPreferences preferences;
    private SharedPreferences .Editor editor;

    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final String EXTRA_MAC_ADDRESS = "extra_mac_address";

    /*
     * paras for set up both devices
     */
    public static final String EXTRA_DEVICE_NAME_L = "extra_device_name_l";
    public static final String EXTRA_MAC_ADDRESS_L = "extra_mac_address_l";
    public static final String EXTRA_DEVICE_NAME_R = "extra_device_name_r";
    public static final String EXTRA_MAC_ADDRESS_R = "extra_mac_address_r";
    public String extra_device_name_l;
    public String extra_mac_address_l;
    public String extra_device_name_r;
    public String extra_mac_address_r;
    private static final String TAG = "SetupDevicesActivity";

    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;
    Experiment experiment;
    int itr_type;
    int p_id;
    int phone_id;
    int armband_id_l;
    int armband_id_r;

    int itr_state;


    private GForceProfile.BluetoothDeviceStateEx state_l = GForceProfile.BluetoothDeviceStateEx.disconnected;
    private GForceProfile.BluetoothDeviceStateEx state_r = GForceProfile.BluetoothDeviceStateEx.disconnected;

    private TextView textViewState_l;
    private TextView textViewState_r;
//    private TextView textViewQuaternion_l;
//    private TextView textViewQuaternion_r;
    private TextView textFirmwareVersion_l;
    private TextView textFirmwareVersion_r;
    private String textErrorMsg = "";
    private Handler handler;
    private Runnable runnable;
    private boolean notifying = false;


    //    private GForceProfile gForceProfile;
    private GForceProfile gForceProfile_l;
    private GForceProfile gForceProfile_r;

    private MyApplication app;

    @OnClick(R.id.connect_l)
    public void onConnectClickLeft() {
        Log.i(TAG, "[onConnectClick] Before state_l = " + state_l);
        clickConnectBtn(state_l, gForceProfile_l, extra_mac_address_l, 0);
        Log.i(TAG, "[onConnectClick] After state_l = " + state_l);
    }

    @OnClick(R.id.connect_r)
    public void onConnectClickRight() {
        Log.i(TAG, "[onConnectClick] Before state_r = " + state_r);
        clickConnectBtn(state_r, gForceProfile_r, extra_mac_address_r, 1);
        Log.i(TAG, "[onConnectClick] After state_r = " + state_r);

    }

    private void clickConnectBtn(GForceProfile.BluetoothDeviceStateEx state, GForceProfile gForceProfile, String macAddress, int hand) {

        // the phone try to connect the device only when it isn't in three states(connected, connecting and ready)
        if (state != GForceProfile.BluetoothDeviceStateEx.connected &&
                state != GForceProfile.BluetoothDeviceStateEx.connecting &&
                state != GForceProfile.BluetoothDeviceStateEx.ready) {

            //try connect
            GForceProfile.GF_RET_CODE ret_code = gForceProfile.connect(macAddress, false);
            Log.i(TAG, "connect ret_code: " + ret_code + macAddress);

            //connect fail
            if (ret_code != GForceProfile.GF_RET_CODE.GF_SUCCESS) {
                Log.e(TAG, "Connect failed, ret_code: " + ret_code);
                if (hand == 0) {
                    textViewState_l.setText("Connect failed, ret_code: " + ret_code);
                } else if (hand == 1) {
                    textViewState_r.setText("Connect failed, ret_code: " + ret_code);
                }
                return;
            }
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);

        } else {
            //disconnect
            boolean success = gForceProfile.disconnect();

            // if disconnect success, reset the view and stop notifying
            if (success) {
                Log.i(TAG, "disconnect" + hand + ":" + macAddress);
//                btn_getFirmwareVersion.setEnabled(false);
                btn_set.setEnabled(false);
//                btn_start.setEnabled(false);

                notifying = false;

//                btn_start.setText("Start Data Notification");
//                textViewQuaternion_l.setText("W: " + "\nX: " + "\nY: " + "\nZ: ");
//                textViewQuaternion_r.setText("W: " + "\nX: " + "\nY: " + "\nZ: ");
//                textFirmwareVersion_l.setText("FirmwareVersion: ");
//                textFirmwareVersion_r.setText("FirmwareVersion: ");
            }
        }
    }


    @OnClick(R.id.set)
    public void onSetClick() {
        if (state_l != GForceProfile.BluetoothDeviceStateEx.ready || state_r != GForceProfile.BluetoothDeviceStateEx.ready) {
            Toast.makeText(SetupDevicesActivity.this, "not both devices are ready", Toast.LENGTH_SHORT).show();
            return;
        }

        setDataSwitch(gForceProfile_l, 0);
        setDataSwitch(gForceProfile_r, 1);
        btn_next.setEnabled(true);
    }

    private int response = -1;

    private void setDataSwitch(GForceProfile gForceProfile, int hand) {
//        int flags = GForceProfile.DataNotifFlags.DNF_EMG_RAW | GForceProfile.DataNotifFlags.DNF_QUATERNION | GForceProfile.DataNotifFlags.DNF_EULERANGLE;
        int flags = GForceProfile.DataNotifFlags.DNF_EMG_RAW | GForceProfile.DataNotifFlags.DNF_QUATERNION
                | GForceProfile.DataNotifFlags.DNF_EULERANGLE | GForceProfile.DataNotifFlags.DNF_GYROSCOPE
                | GForceProfile.DataNotifFlags.DNF_ACCELERATE | GForceProfile.DataNotifFlags.DNF_MAGNETOMETER;
//        int flags = GForceProfile.DataNotifFlags.DNF_EMG_RAW | GForceProfile.DataNotifFlags.DNF_QUATERNION
//                | GForceProfile.DataNotifFlags.DNF_EULERANGLE;
        GForceProfile.GF_RET_CODE result;

        response = -1;

        result = gForceProfile.setDataNotifSwitch(flags, new CommandResponseCallback() {
            @Override
            public void onSetCommandResponse(int resp) {
                Log.i(TAG, "response of setDataNotifSwitch(): " + resp);
                response = resp;

                String msg;

                if (resp == GForceProfile.ResponseResult.RSP_CODE_SUCCESS) {
                    msg = "Set Data Switch succeeded";

                    if (hand == 0) {
                        set_left = true;
                    } else if (hand == 1) {
                        set_right = true;
                    }
                } else {
                    msg = "Set Data Switch failed, resp code: " + resp;

                }


                runOnUiThread(new Runnable() {
                    public void run() {
                        if(set_left && set_right){
                            btn_next.setEnabled(true);
                        }
                        if (hand == 0) {
                            textViewState_l.setText(msg);
                        } else if (hand == 1) {
                            textViewState_r.setText(msg);
                        }
                    }
                });
            }
        }, 5000);

        Log.i(TAG, "setDataNotifSwitch() result:" + result);

        if (result != GForceProfile.GF_RET_CODE.GF_SUCCESS) {
            if (hand==0) {
                textViewState_l.setText("setDataNotifSwitch() failed.");
            } else if (hand == 1) {
                textViewState_r.setText("setDataNotifSwitch() failed.");
            }

        }

        while (response == -1) {
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (response != GForceProfile.ResponseResult.RSP_CODE_SUCCESS) return;

        if ((flags | GForceProfile.DataNotifFlags.DNF_EMG_RAW) == 0) {
//            btn_start.setEnabled(true);
        } else {
            result = gForceProfile.setEmgRawDataConfig(500, 0xFF, 128, 8, new CommandResponseCallback() {
                @Override
                public void onSetCommandResponse(int resp) {
                    Log.i(TAG, "response of setEmgRawDataConfig(): " + resp);

                    String msg;

                    if (resp == GForceProfile.ResponseResult.RSP_CODE_SUCCESS) {
                        msg = "Set EMG Config succeeded";
                        if (hand == 0) {
                            set_left = true;
                        } else if (hand == 1) {
                            set_right = true;
                        }
                    } else {
                        msg = "Set EMG Config failed, resp code: " + resp;
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(set_left && set_right){
                                btn_next.setEnabled(true);
                            }
                            if (resp == GForceProfile.ResponseResult.RSP_CODE_SUCCESS) {
//                                btn_start.setEnabled(true);
                            }
                            if (hand==0) {
                                textViewState_l.setText(msg);
                            } else if (hand==1) {
                                textViewState_r.setText(msg);
                            }
                        }
                    });
                }
            }, 5000);

            if (result != GForceProfile.GF_RET_CODE.GF_SUCCESS) {
                if (hand==0) {
                    textViewState_l.setText("setEmgRawDataConfig failed.");
                } else if (hand==1) {
                    textViewState_r.setText("setEmgRawDataConfig failed.");
                }

            }
        }
    }
//
//    @OnClick(R.id.start)
//    public void onStartClick() {
//        if (notifying) {
//            btn_start.setText("Start Data Notification");
//
//            gForceProfile_l.stopDataNotification();
//            gForceProfile_r.stopDataNotification();
//
//            notifying = false;
//        } else {
//            if (state_l != GForceProfile.BluetoothDeviceStateEx.ready || state_r != GForceProfile.BluetoothDeviceStateEx.ready) {
//                Toast.makeText(SetupDevicesActivity.this, "not both devices are ready", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//
//            dataNotification(gForceProfile_r, 0);
//            dataNotification(gForceProfile_l, 1);
//
//            btn_start.setText("Stop Data Notification");
//            notifying = true;
//        }
//    }

    private void dataNotification(GForceProfile gForceProfile, int hand) {
        gForceProfile.startDataNotification(new DataNotificationCallback() {
            @Override
            public void onData(byte[] data) {
                Log.i(TAG, "data type: " + data[0] + ", len: " + data.length);

                if (data[0] == GForceProfile.NotifDataType.NTF_QUAT_FLOAT_DATA && data.length == 17) {
                    Log.i(TAG, "Quat data: " + Arrays.toString(data)+"\nhand use: " + hand);
                    Log.i(TAG, "hand use: " + hand);

                    byte[] W = new byte[4];
                    byte[] X = new byte[4];
                    byte[] Y = new byte[4];
                    byte[] Z = new byte[4];

                    System.arraycopy(data, 1, W, 0, 4);
                    System.arraycopy(data, 5, X, 0, 4);
                    System.arraycopy(data, 9, Y, 0, 4);
                    System.arraycopy(data, 13, Z, 0, 4);

                    float w = getFloat(W);
                    float x = getFloat(X);
                    float y = getFloat(Y);
                    float z = getFloat(Z);

                    if (hand == 0) {
                        Log.i(TAG, "#####LEFT QUAT####" + +w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);
                    } else if (hand == 1) {
                        Log.i(TAG, "****RIGHT QUAT****" + +w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);
                    }

                    ContentValues values = new ContentValues();
                    values.put("p_id", 1);
                    values.put("e_id", 1);
                    values.put("itr_id", 1);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("w", w);
                    values.put("x", x);
                    values.put("y", y);
                    values.put("z", z);
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("Quaternion", null, values));
                    values.clear();

//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            if (hand == 0) {
//                                textViewQuaternion_l.setText("W: " + w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);
//                            } else if (hand == 1) {
//                                textViewQuaternion_r.setText("W: " + w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);//use fragment to set up the update on sree
//                            }
//                        }
//                    });


                }else if(data[0] == GForceProfile.NotifDataType.NTF_EMG_ADC_DATA && data.length == 129) {
                    Log.i("DeviceActivity", "EMG data: " + Arrays.toString(data)+"hand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);
//                        byte[] CH0 = new byte[16];
//                        byte[] CH1 = new byte[16];
//                        byte[] CH2 = new byte[16];
//                        byte[] CH3 = new byte[16];
//                        byte[] CH4 = new byte[16];
//                        byte[] CH5 = new byte[16];
//                        byte[] CH6 = new byte[16];
//                        byte[] CH7 = new byte[16];
                    ArrayList CH0 = new ArrayList<Byte>(16);
                    ArrayList CH1 = new ArrayList<Byte>(16);
                    ArrayList CH2 = new ArrayList<Byte>(16);
                    ArrayList CH3 = new ArrayList<Byte>(16);
                    ArrayList CH4 = new ArrayList<Byte>(16);
                    ArrayList CH5 = new ArrayList<Byte>(16);
                    ArrayList CH6 = new ArrayList<Byte>(16);
                    ArrayList CH7 = new ArrayList<Byte>(16);

                    byte[] raw_EMG = new byte[128];
                    System.arraycopy(data,1,raw_EMG,0,128);
                    int count = 0;
                    for(byte i : raw_EMG){
                        switch (count){
                            case 0:
                                CH0.add(i);
                                count++;
                                break;
                            case 1:
                                CH1.add(i);
                                count++;
                                break;
                            case 2:
                                CH2.add(i);
                                count++;
                                break;
                            case 3:
                                CH3.add(i);
                                count++;
                                break;
                            case 4:
                                CH4.add(i);
                                count++;
                                break;
                            case 5:
                                CH5.add(i);
                                count++;
                                break;
                            case 6:
                                CH6.add(i);
                                count++;
                                break;
                            case 7:
                                CH7.add(i);
                                count = 0;
                                break;
                        }

                    }
//                    System.out.println("CH0"+CH0.toString());
//                    System.out.println("CH1"+CH1.toString());
//                    System.out.println("CH2"+CH2.toString());
//                    System.out.println("CH3"+CH3.toString());
//                    System.out.println("CH4"+CH4.toString());
//                    System.out.println("CH5"+CH5.toString());
//                    System.out.println("CH6"+CH6.toString());
//                    System.out.println("CH7"+CH7.toString());
//                    ContentValues values = new ContentValues();
//                    values.put("p_id", 1);
//                    values.put("e_id", 1);
//                    values.put("itr_id", 1);
//                    values.put("itr_type", itr_type);
//                    values.put("hand", hand);
//                    values.put("ch_01", pitch);
//                    values.put("ch_02", roll);
//                    values.put("ch_03", yaw);
//                    values.put("ch_04", yaw);
//                    values.put("ch_05", yaw);
//                    values.put("ch_06", yaw);
//                    values.put("ch_07", yaw);
//                    values.put("ch_08", yaw);
//                    values.put("state", 0);
//                    values.put("timestamp", DatabaseUtil.getTimestamp());
//                    db.insert("Euler_Angle", null, values);
//                    values.clear();


                }else if(data[0] == GForceProfile.NotifDataType.NTF_EULER_DATA && data.length == 13) {
                    Log.i("DeviceActivity", "NTF_EULER_DATA: " + Arrays.toString(data)+"\nhand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);
                    byte[] b_pitch = new byte[4];
                    byte[] b_roll = new byte[4];
                    byte[] b_yaw = new byte[4];

                    System.arraycopy(data, 1, b_pitch, 0, 4);
                    System.arraycopy(data, 5, b_roll, 0, 4);
                    System.arraycopy(data, 9, b_yaw, 0, 4);

                    float pitch = getFloat(b_pitch);
                    float roll = getFloat(b_roll);
                    float yaw = getFloat(b_yaw);
                    Log.i("DeviceActivity", "NTF_EULER_DATA: " + " pitch:" + pitch + " roll:" +roll +" yaw:" +yaw);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ContentValues values = new ContentValues();
                            values.put("p_id", 1);
                            values.put("e_id", 1);
                            values.put("itr_id", 1);
                            values.put("itr_type", itr_type);
                            values.put("hand", hand);
                            values.put("pitch", pitch);
                            values.put("roll", roll);
                            values.put("yaw", yaw);
                            values.put("state", 0);
                            values.put("timestamp", DatabaseUtil.getTimestamp());
                            db.insert("Euler_Angle", null, values);
                            values.clear();
                        }
                    });


                }else if(data[0] == GForceProfile.NotifDataType.NTF_GYO_DATA && data.length == 13){
                    Log.i("DeviceActivity", "NTF_GYO_DATA : " + Arrays.toString(data)+"hand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);
                    byte[] b_gyo_x = new byte[4];
                    byte[] b_gyo_y = new byte[4];
                    byte[] b_gyo_z = new byte[4];

                    System.arraycopy(data, 1, b_gyo_x, 0, 4);
                    System.arraycopy(data, 5, b_gyo_y, 0, 4);
                    System.arraycopy(data, 9, b_gyo_z, 0, 4);

                    System.out.println(Arrays.toString(b_gyo_x));
                    System.out.println(Arrays.toString(b_gyo_y));
                    System.out.println(Arrays.toString(b_gyo_z));

                    long gyo_x = getLong(b_gyo_x);
                    long gyo_y = getLong(b_gyo_y);
                    long gyo_z = getLong(b_gyo_z);

                    Log.i("DeviceActivity", " NTF_GYO_DATA:" + " gyo_x:" + gyo_x + " gyo_y:" +gyo_y +" gyo_z:" +gyo_z);
                    ContentValues values = new ContentValues();
                    values.put("p_id", 1);
                    values.put("e_id", 1);
                    values.put("itr_id", 1);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("x", gyo_x);
                    values.put("y", gyo_y);
                    values.put("z", gyo_z);
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("Gyroscope", null, values));
                    values.clear();

                }else if(data[0] == GForceProfile.NotifDataType.NTF_ACC_DATA && data.length == 13){
                    Log.i("DeviceActivity", "NTF_ACC_DATA : " + Arrays.toString(data)+"hand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);
                    byte[] b_acc_x = new byte[4];
                    byte[] b_acc_y = new byte[4];
                    byte[] b_acc_z = new byte[4];

                    System.arraycopy(data, 1, b_acc_x, 0, 4);
                    System.arraycopy(data, 5, b_acc_y, 0, 4);
                    System.arraycopy(data, 9, b_acc_z, 0, 4);

                    System.out.println(Arrays.toString(b_acc_x));
                    System.out.println(Arrays.toString(b_acc_y));
                    System.out.println(Arrays.toString(b_acc_z));

                    long acc_x = getLong(b_acc_x);
                    long acc_y = getLong(b_acc_y);
                    long acc_z = getLong(b_acc_z);

                    Log.i("DeviceActivity", "NTF_ACC_DATA: " + " acc_x:" + acc_x + " acc_y:" +acc_y +" acc_z:" +acc_z);

                    ContentValues values = new ContentValues();
                    values.put("p_id", 1);
                    values.put("e_id", 1);
                    values.put("itr_id", 1);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("x", acc_x);
                    values.put("y", acc_y);
                    values.put("z", acc_z);
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("Acceletor", null, values));
                    values.clear();

                }else if(data[0] == GForceProfile.NotifDataType.NTF_MAG_DATA && data.length == 13){
                    Log.i("DeviceActivity", "NTF_MAG_DATA : " + Arrays.toString(data));
                    Log.i("DeviceActivity", "hand use: " + hand+"hand use: " + hand);
                    byte[] b_mag_x = new byte[4];
                    byte[] b_mag_y = new byte[4];
                    byte[] b_mag_z = new byte[4];

                    System.arraycopy(data, 1, b_mag_x, 0, 4);
                    System.arraycopy(data, 5, b_mag_y, 0, 4);
                    System.arraycopy(data, 9, b_mag_z, 0, 4);

                    System.out.println(Arrays.toString(b_mag_x));
                    System.out.println(Arrays.toString(b_mag_y));
                    System.out.println(Arrays.toString(b_mag_z));

                    long mag_x = getLong(b_mag_x);
                    long mag_y = getLong(b_mag_y);
                    long mag_z = getLong(b_mag_z);
                    Log.i("DeviceActivity", "NTF_MAG_DATA: " + " mag_x:" + mag_x + " mag_y:" +mag_y +" mag_z:" +mag_z);
                    ContentValues values = new ContentValues();
                    values.put("p_id", 1);
                    values.put("e_id", 1);
                    values.put("itr_id", 1);
                    values.put("itr_type", itr_type);
                    values.put("hand", hand);
                    values.put("x", mag_x);
                    values.put("y", mag_y);
                    values.put("z", mag_z);
                    values.put("state", 0);
                    values.put("timestamp", DatabaseUtil.getTimestamp());
                    System.out.println(db.insert("Magnetometer", null, values));
                    values.clear();
                }else if(data[0] == GForceProfile.NotifDataType.NTF_ROTA_DATA && data.length == 37){
                    Log.i("DeviceActivity", "NTF_ROTA_DATA : " + Arrays.toString(data) +"hand use: " + hand);
                    Log.i("DeviceActivity", "hand use: " + hand);

                }
            }
        });
    }


//    @OnClick(R.id.get_firmware_version)
//    public void onGetFirmwareVersionClick() {
//        getFirmwareVersion(gForceProfile_l, 0);
//        getFirmwareVersion(gForceProfile_r, 1);
//    }

    private void getFirmwareVersion(GForceProfile gForceProfile, int hand) {
        GForceProfile.GF_RET_CODE result = gForceProfile.getControllerFirmwareVersion(new CommandResponseCallback() {
            @Override
            public void onGetControllerFirmwareVersion(int resp, String firmwareVersion) {
                Log.i(TAG, "\nfirmwareVersion: " + hand + firmwareVersion);
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (hand == 0) {
                            textFirmwareVersion_l.setText("FirmwareVersion: " + firmwareVersion);
                        } else if (hand == 1) {
                            textFirmwareVersion_r.setText("FirmwareVersion: " + firmwareVersion);
                        }
                    }
                });
            }
        }, 5000);


        Log.i(TAG, "getControllerFirmwareVersion() result " + result);

        if (result != GForceProfile.GF_RET_CODE.GF_SUCCESS) {
            if (hand == 0) {
                textFirmwareVersion_l.setText("FirmwareVersion: Error : " + result);
            } else if (hand == 1) {
                textFirmwareVersion_r.setText("FirmwareVersion: Error : " + result);
            }
        }
    }


    @OnClick(R.id.btn_next1)
    public void onNextClick(){
        if(itr_state == Interaction.State.CONNECT_ERROR){
            finish();
        }else{
            try {
                phone_id = 2;
                armband_id_l = 5;
                armband_id_r = 6;
                experiment = new Experiment(p_id, phone_id, armband_id_l, armband_id_r);
                int e_id =experiment.insertExperiment(db);
//            Intent intent = new Intent(SetupDevicesActivity.this,ImagePickerActivity.class);
                Intent intent = new Intent(SetupDevicesActivity.this,MaterialListActivity.class);
//            intent.putExtra("e_id", e_id);
//                app.setExperimentID(e_id);
//                app.setExperimentState(Experiment.State.START);
//                app.setInteractionType(Interaction.Type.RELAX);

                startActivity(intent);
                Log.i(TAG, "jump to image picker");
            }catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }


    }


    public static float getFloat(byte[] b) {
        int accum = 0;
        accum = accum | (b[0] & 0xff) << 0;
        accum = accum | (b[1] & 0xff) << 8;
        accum = accum | (b[2] & 0xff) << 16;
        accum = accum | (b[3] & 0xff) << 24;
        System.out.println(accum);
        return Float.intBitsToFloat(accum);
    }

    public static long getLong(byte[] b) {

        long accum = 0;
        accum = accum | (b[0] & 0xffL) << 0;
        accum = accum | (b[1] & 0xffL) << 8;
        accum = accum | (b[2] & 0xffL) << 16;
        accum = accum | (b[3] & 0xffL) << 24;

        System.out.println(accum);
        return accum;

    }


    void updateState() {
        GForceProfile.BluetoothDeviceStateEx newState_l = gForceProfile_l.getState();
        GForceProfile.BluetoothDeviceStateEx newState_r = gForceProfile_r.getState();

        if (state_l != newState_l) {
            runOnUiThread(new Runnable() {
                public void run() {
                    textViewState_l.setText(newState_l.toString());
                }
            });
            state_l = newState_l;
            updateConnectBtn(state_l, btn_connect_l);
        }

        if (state_r != newState_r) {
            runOnUiThread(new Runnable() {
                public void run() {
//                    textViewState.setText("Device State: " + newState.toString());
//                    state_r = newState_l;
                    textViewState_r.setText(newState_r.toString());
                }
            });
            state_r = newState_r;
            updateConnectBtn(state_r, btn_connect_r);
        }

    }

    private void updateConnectBtn(GForceProfile.BluetoothDeviceStateEx state, Button button) {
        if (state == GForceProfile.BluetoothDeviceStateEx.disconnected) {
            runOnUiThread(new Runnable() {
                public void run() {
                    button.setText("Connect");
                }
            });
        } else if (state == GForceProfile.BluetoothDeviceStateEx.connected) {
            runOnUiThread(new Runnable() {
                public void run() {
                    button.setText("Disconnect");
                }
            });
        } else if (state == GForceProfile.BluetoothDeviceStateEx.connecting) {
            runOnUiThread(new Runnable() {
                public void run() {
                    button.setText("Disconnect");
                }
            });
        } else if (state == GForceProfile.BluetoothDeviceStateEx.disconnecting) {
            runOnUiThread(new Runnable() {
                public void run() {
                    button.setText("Connect");
                }
            });
        } else if (state == GForceProfile.BluetoothDeviceStateEx.ready) {
            runOnUiThread(new Runnable() {
                public void run() {
                    button.setText("Disconnect");
//                    btn_getFirmwareVersion.setEnabled(true);
                    if(state_l == GForceProfile.BluetoothDeviceStateEx.ready
                            && state_r == GForceProfile.BluetoothDeviceStateEx.ready){
                        btn_set.setEnabled(true);
                    }


                    // btn_start.setEnabled(true);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_devices);
        ButterKnife.bind(this);
        this.setTitle("Setup Devices");
        app = (MyApplication) getApplication();
        if (app.getInteractionType() != -1){
            btn_next.setText("BACK");
        }
        btn_next.setEnabled(false);

        dbHelper = new GForceDatabaseOpenHelper(this, "GForce.db", null, 1);
        db = dbHelper.getReadableDatabase();
        // devices info
//        extra_device_name_l = getIntent().getStringExtra(EXTRA_DEVICE_NAME_L);
//        extra_mac_address_l = getIntent().getStringExtra(EXTRA_MAC_ADDRESS_L);
//        extra_device_name_r = getIntent().getStringExtra(EXTRA_DEVICE_NAME_R);
//        extra_mac_address_r = getIntent().getStringExtra(EXTRA_MAC_ADDRESS_R);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        extra_device_name_l = preferences.getString("extra_device_name_l",null);
        extra_mac_address_l = preferences.getString("extra_mac_address_l",null);
        extra_device_name_r = preferences.getString("extra_mac_address_r",null);
        extra_mac_address_r = preferences.getString("extra_device_name_r",null);

        if(extra_mac_address_l == null || extra_mac_address_r == null){
            Toast.makeText(this,"faill to get devices information, exit App",Toast.LENGTH_LONG).show();
            this.finish();
            System.exit(0);
        }

        Log.i(TAG, "extra_device_name_l:" + extra_device_name_l +
                "extra_mac_address_l:" + extra_mac_address_l +
                "extra_device_name_r:" + extra_device_name_r +
                "extra_mac_address_r:" + extra_mac_address_r);
        itr_type = 99;
        p_id = Participant.getIDFromPreference(this);

        Intent intent = this.getIntent();
        itr_state = intent.getIntExtra("itr_state", -1);

        // create a method to get from database;

//        getSupportActionBar().setSubtitle(getString(R.string.dev_name_with_mac, extra_device_name_l, extra_mac_address_l) +
//                getString(R.string.dev_name_with_mac, extra_device_name_r, extra_mac_address_r));

        //application pass value

         //get MyApplication

        try{
            gForceProfile_l = app.getProfileLeft();
            gForceProfile_r = app.getProfileRight();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }

        textViewState_l = this.findViewById(R.id.text_device_l_state);
        textViewState_r = this.findViewById(R.id.text_device_r_state);
//        textViewQuaternion_l = this.findViewById(R.id.text_quaternion_l);
//        textViewQuaternion_r = this.findViewById(R.id.text_quaternion_r);
//        textFirmwareVersion_l = this.findViewById(R.id.text_firmware_version_l);
//        textFirmwareVersion_r = this.findViewById(R.id.text_firmware_version_r);

//        btn_getFirmwareVersion.setEnabled(false);
        btn_set.setEnabled(false);
//        btn_start.setEnabled(false);


        // setup a thread to update status
        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                updateState();
                handler.postDelayed(this, 1000);
            }
        };
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
//        gForceProfile_l.disconnect();
//        gForceProfile_r.disconnect();
    }
}
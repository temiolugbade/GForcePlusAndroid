package com.oymotion.newgforceprofiledemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.oymotion.gforceprofile.CommandResponseCallback;
import com.oymotion.gforceprofile.DataNotificationCallback;
import com.oymotion.gforceprofile.GForceProfile;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceActivity extends AppCompatActivity {
    @BindView(R.id.connect)
    Button btn_connect;
    @BindView(R.id.start)
    Button btn_start;
    @BindView(R.id.get_firmware_version)
    Button btn_getFirmwareVersion;
    @BindView(R.id.set)
    Button btn_set;
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";
    public static final String EXTRA_MAC_ADDRESS = "extra_mac_address";

    private GForceProfile.BluetoothDeviceStateEx state = GForceProfile.BluetoothDeviceStateEx.disconnected;
    private String macAddress;
    private TextView textViewState;
    private TextView textViewQuaternion;
    private TextView textFirmwareVersion;
    private String textErrorMsg = "";
    private Handler handler;
    private Runnable runnable;
    private boolean notifying = false;

    private GForceProfile gForceProfile;


    @OnClick(R.id.connect)
    public void onConnectClick() {
        Log.i("DeviceActivity", "[onConnectClick] state=" + state);

        if (state != GForceProfile.BluetoothDeviceStateEx.connected &&
                state != GForceProfile.BluetoothDeviceStateEx.connecting &&
                state != GForceProfile.BluetoothDeviceStateEx.ready) {

            GForceProfile.GF_RET_CODE ret_code = gForceProfile.connect(macAddress, false);

            if (ret_code != GForceProfile.GF_RET_CODE.GF_SUCCESS) {
                Log.e("DeviceActivity", "Connect failed, ret_code: " + ret_code);
                textViewState.setText("Connect failed, ret_code: " + ret_code);
                return;
            }

            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
        } else {
            boolean success = gForceProfile.disconnect();

            if (success) {
                btn_getFirmwareVersion.setEnabled(false);
                btn_set.setEnabled(false);
                btn_start.setEnabled(false);

                notifying = false;

                btn_start.setText("Start Data Notification");
                textViewQuaternion.setText("W: " + "\nX: " + "\nY: " + "\nZ: ");
                textFirmwareVersion.setText("FirmwareVersion: ");
            }
        }
    }


    private int response = -1;

    @OnClick(R.id.set)
    public void onSetClick() {
        if (state != GForceProfile.BluetoothDeviceStateEx.ready) return;

        int flags = GForceProfile.DataNotifFlags.DNF_EMG_RAW | GForceProfile.DataNotifFlags.DNF_QUATERNION | GForceProfile.DataNotifFlags.DNF_EULERANGLE;

        GForceProfile.GF_RET_CODE result;

        response = -1;

        result = gForceProfile.setDataNotifSwitch(flags, new CommandResponseCallback() {
            @Override
            public void onSetCommandResponse(int resp) {
                Log.i("DeviceActivity", "response of setDataNotifSwitch(): " + resp);
                response = resp;

                String msg;

                if (resp == GForceProfile.ResponseResult.RSP_CODE_SUCCESS) {
                    msg = "Device State: " + "Set Data Switch succeeded";
                } else {
                    msg = "Device State: " + "Set Data Switch failed, resp code: " + resp;
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        textViewState.setText(msg);
                    }
                });
            }
        }, 5000);

        Log.i("DeviceActivity", "setDataNotifSwitch() result:" + result);

        if (result != GForceProfile.GF_RET_CODE.GF_SUCCESS) {
            textViewState.setText("Device State: " + "setDataNotifSwitch() failed.");
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
            btn_start.setEnabled(true);
        } else {
            result = gForceProfile.setEmgRawDataConfig(500, 0xFF, 128, 8, new CommandResponseCallback() {
                @Override
                public void onSetCommandResponse(int resp) {
                    Log.i("DeviceActivity", "response of setEmgRawDataConfig(): " + resp);

                    String msg;

                    if (resp == GForceProfile.ResponseResult.RSP_CODE_SUCCESS) {
                        msg = "Device State: " + "Set EMG Config succeeded";
                    } else {
                        msg = "Device State: " + "Set EMG Config failed, resp code: " + resp;
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (resp == GForceProfile.ResponseResult.RSP_CODE_SUCCESS) {
                                btn_start.setEnabled(true);
                            }

                            textViewState.setText(msg);
                        }
                    });
                }
            }, 5000);

            if (result != GForceProfile.GF_RET_CODE.GF_SUCCESS) {
                textViewState.setText("Device State: " + "setEmgRawDataConfig() failed.");
            }
        }
    }

    @OnClick(R.id.start)
    public void onStartClick() {
        if (notifying) {
            btn_start.setText("Start Data Notification");

            gForceProfile.stopDataNotification();

            notifying = false;
        } else {
            if (state != GForceProfile.BluetoothDeviceStateEx.ready) return;

            gForceProfile.startDataNotification(new DataNotificationCallback() {
                @Override
                public void onData(byte[] data) {
                    Log.i("DeviceActivity", "data type: " + data[0] + ", len: " + data.length);

                    if (data[0] == GForceProfile.NotifDataType.NTF_QUAT_FLOAT_DATA && data.length == 17) {
                        Log.i("DeviceActivity", "Quat data: " + Arrays.toString(data));

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

                        runOnUiThread(new Runnable() {
                            public void run() {
                                textViewQuaternion.setText("W: " + w + "\nX: " + x + "\nY: " + y + "\nZ: " + z);
                            }
                        });
                    }
                }
            });

            btn_start.setText("Stop Data Notification");
            notifying = true;
        }
    }

    @OnClick(R.id.get_firmware_version)
    public void onGetFirmwareVersionClick() {
        GForceProfile.GF_RET_CODE result = gForceProfile.getControllerFirmwareVersion(new CommandResponseCallback() {
            @Override
            public void onGetControllerFirmwareVersion(int resp, String firmwareVersion) {
                Log.i("DeviceActivity", "\nfirmwareVersion: " + firmwareVersion);

                runOnUiThread(new Runnable() {
                    public void run() {
                        textFirmwareVersion.setText("FirmwareVersion: " + firmwareVersion);
                    }
                });
            }
        }, 5000);

        Log.i("DeviceActivity", "getControllerFirmwareVersion() result " + result);

        if (result != GForceProfile.GF_RET_CODE.GF_SUCCESS) {
            textFirmwareVersion.setText("FirmwareVersion: Error : " + result);
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

    void updateState() {
        GForceProfile.BluetoothDeviceStateEx newState = gForceProfile.getState();

        if (state != newState) {
            runOnUiThread(new Runnable() {
                public void run() {
                    textViewState.setText("Device State: " + newState.toString());
                }
            });

            state = newState;

            if (state == GForceProfile.BluetoothDeviceStateEx.disconnected) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        btn_connect.setText("Connect");
                    }
                });
            } else if (state == GForceProfile.BluetoothDeviceStateEx.connected) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        btn_connect.setText("Disconnect");
                    }
                });
            } else if (state == GForceProfile.BluetoothDeviceStateEx.connecting) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        btn_connect.setText("Disconnect");
                    }
                });
            } else if (state == GForceProfile.BluetoothDeviceStateEx.disconnecting) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        btn_connect.setText("Connect");
                    }
                });
            } else if (state == GForceProfile.BluetoothDeviceStateEx.ready) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        btn_connect.setText("Disconnect");

                        btn_getFirmwareVersion.setEnabled(true);
                        btn_set.setEnabled(true);
                        // btn_start.setEnabled(true);
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);
        macAddress = getIntent().getStringExtra(EXTRA_MAC_ADDRESS);
        getSupportActionBar().setSubtitle(getString(R.string.dev_name_with_mac, getIntent().getStringExtra(EXTRA_DEVICE_NAME), macAddress));

        gForceProfile = new GForceProfile(new GForceProfile.GForceErrorCallback() {
            @Override
            public void onGForceErrorCallback(String errorMsg) {
                Toast.makeText(DeviceActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        textViewState = this.findViewById(R.id.text_device_state);
        textViewQuaternion = this.findViewById(R.id.text_quaternion);
        textFirmwareVersion = this.findViewById(R.id.text_firmware_version);

        btn_getFirmwareVersion.setEnabled(false);
        btn_set.setEnabled(false);
        btn_start.setEnabled(false);

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
        gForceProfile.disconnect();
    }
}

package com.oymotion.newgforceprofiledemo;

import android.bluetooth.BluetoothDevice;

public class ScanResult {
    private final BluetoothDevice bluetoothDevice;
    private final int rssi;

    public ScanResult(BluetoothDevice bluetoothDevice, int rssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    @Override
    public String toString() {
        return "ScanResult{" +
                "bluetoothDevice=" + bluetoothDevice +
                ", rssi=" + rssi +
                '}';
    }
}

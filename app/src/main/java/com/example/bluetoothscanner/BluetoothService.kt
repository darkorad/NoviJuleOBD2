package com.example.bluetoothscanner

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BluetoothService : Service() {

    companion object {
        const val ACTION_DEVICE_FOUND = "com.example.bluetoothscanner.ACTION_DEVICE_FOUND"
        const val EXTRA_DEVICE = "com.example.bluetoothscanner.EXTRA_DEVICE"
        const val ACTION_GATT_CONNECTED = "com.example.bluetoothscanner.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED = "com.example.bluetoothscanner.ACTION_GATT_DISCONNECTED"
    }

    private val binder = LocalBinder()
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val bluetoothLeScanner by lazy { bluetoothAdapter.bluetoothLeScanner }
    private var scanning = false

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val intent = Intent(ACTION_DEVICE_FOUND)
            intent.putExtra(EXTRA_DEVICE, result.device)
            LocalBroadcastManager.getInstance(this@BluetoothService).sendBroadcast(intent)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("BluetoothService", "Scan failed with error code: $errorCode")
        }
    }


    inner class LocalBinder : Binder() {
        fun getService(): BluetoothService = this@BluetoothService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    fun startScan() {
        if (!scanning) {
            // Stops scanning after a pre-defined scan period.
            scanning = true
            bluetoothLeScanner.startScan(scanCallback)
            Log.d("BluetoothService", "Started scanning for devices")
        }
    }

    fun stopScan() {
        if (scanning) {
            scanning = false
            bluetoothLeScanner.stopScan(scanCallback)
            Log.d("BluetoothService", "Stopped scanning for devices")
        }
    }

    private var bluetoothGatt: BluetoothGatt? = null

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val intentAction: String
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    intentAction = ACTION_GATT_CONNECTED
                    Log.i("BluetoothService", "Connected to GATT server.")
                    broadcastUpdate(intentAction)
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    intentAction = ACTION_GATT_DISCONNECTED
                    Log.i("BluetoothService", "Disconnected from GATT server.")
                    broadcastUpdate(intentAction)
                }
            }
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun connect(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    fun pair(device: BluetoothDevice) {
        device.createBond()
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}

package com.example.bluetoothscanner

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var deviceListAdapter: DeviceListAdapter
    private lateinit var bluetoothService: BluetoothService
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as BluetoothService.LocalBinder
            bluetoothService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scanButton: Button = findViewById(R.id.scan_button)
        scanButton.setOnClickListener {
            if (checkPermissions()) {
                bluetoothService.startScan()
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.device_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        deviceListAdapter = DeviceListAdapter(
            mutableListOf(),
            onConnect = { device -> bluetoothService.connect(device) },
            onPair = { device -> bluetoothService.pair(device) },
            onDisconnect = { bluetoothService.disconnect() }
        )
        recyclerView.adapter = deviceListAdapter

        val intent = Intent(this, BluetoothService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BluetoothService.ACTION_GATT_DISCONNECTED)
        LocalBroadcastManager.getInstance(this).registerReceiver(gattUpdateReceiver, intentFilter)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            deviceFoundReceiver,
            IntentFilter(BluetoothService.ACTION_DEVICE_FOUND)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gattUpdateReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceFoundReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    1
                )
                return false
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
                return false
            }
        }
        return true
    }

    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothService.ACTION_GATT_CONNECTED -> {
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                }
                BluetoothService.ACTION_GATT_DISCONNECTED -> {
                    Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val deviceFoundReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            if (BluetoothService.ACTION_DEVICE_FOUND == action) {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothService.EXTRA_DEVICE)
                device?.let {
                    deviceListAdapter.addDevice(it)
                }
            }
        }
    }
}

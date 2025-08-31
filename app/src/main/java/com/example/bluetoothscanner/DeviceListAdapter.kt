package com.example.bluetoothscanner

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceListAdapter(
    private val devices: MutableList<BluetoothDevice>,
    private val onConnect: (BluetoothDevice) -> Unit,
    private val onPair: (BluetoothDevice) -> Unit,
    private val onDisconnect: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_device, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]
        holder.deviceName.text = device.name ?: "Unknown Device"
        holder.deviceAddress.text = device.address
        holder.connectButton.setOnClickListener { onConnect(device) }
        holder.pairButton.setOnClickListener { onPair(device) }
        holder.disconnectButton.setOnClickListener { onDisconnect(device) }
    }

    override fun getItemCount() = devices.size

    fun addDevice(device: BluetoothDevice) {
        if (!devices.contains(device)) {
            devices.add(device)
            notifyItemInserted(devices.size - 1)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceName: TextView = itemView.findViewById(R.id.device_name)
        val deviceAddress: TextView = itemView.findViewById(R.id.device_address)
        val connectButton: Button = itemView.findViewById(R.id.connect_button)
        val pairButton: Button = itemView.findViewById(R.id.pair_button)
        val disconnectButton: Button = itemView.findViewById(R.id.disconnect_button)
    }
}

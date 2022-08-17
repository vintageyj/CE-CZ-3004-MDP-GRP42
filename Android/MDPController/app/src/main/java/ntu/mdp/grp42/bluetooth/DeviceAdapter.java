package ntu.mdp.grp42.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

import ntu.mdp.grp42.R;

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    private Context context;
    private int resource;
    public DeviceAdapter(Context context, int resource, ArrayList<BluetoothDevice> deviceList) {
        super(context, resource, deviceList);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevice device = getItem(position);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            String deviceName = "Device Name: " + device.getName();
            String deviceAddress = "Device Address: " + device.getAddress(); // MAC Address

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent, false);

            TextView deviceNameTV = (TextView) convertView.findViewById(R.id.deviceNameTextView);
            TextView deviceAddressTV = (TextView) convertView.findViewById(R.id.deviceAddressTextView);

            deviceNameTV.setText(deviceName);
            deviceAddressTV.setText(deviceAddress);
        }
        return convertView;
    }
}

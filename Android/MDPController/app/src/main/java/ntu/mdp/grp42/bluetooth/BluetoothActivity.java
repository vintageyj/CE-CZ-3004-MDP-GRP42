package ntu.mdp.grp42.bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Set;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.TaskActivity;

public class BluetoothActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

//    BottomNavigationView bottomNavigationView;

    public static final int BLUETOOTH_REQUEST_CODE = 1;

    Button onButton, discoverableButton, scanButton;
    ListView pairListView, scanListView;
    TextView pairTextView, scanTextView;
    BluetoothAdapter bluetoothAdapter;

    Set<BluetoothDevice> pairedDevices, scannedDevices;
    ArrayAdapter pairAdapter, scanAdapter;
    ArrayList<BluetoothDevice> pairedList = new ArrayList<>();
    ArrayList<BluetoothDevice> scanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.navigation_bluetooth);
//        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        onButton = findViewById(R.id.onButton);
        discoverableButton = findViewById(R.id.discoverableButton);
        scanButton = findViewById(R.id.scanButton);
        pairListView = findViewById(R.id.pairedListView);
        scanListView = findViewById(R.id.scannedListView);
        pairTextView = findViewById(R.id.pairedTextView);
        scanTextView = findViewById(R.id.scannedTextView);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(BluetoothActivity.this, "This device doesn't support Bluetooth",
                    Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            onButton.setText("On Bluetooth");
        } else {
            onButton.setText("Off Bluetooth");
        }

        onButton.setOnClickListener(this);
        discoverableButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!pairedDevices.contains(device) && device.getName() != null) {
                    scanList.add(device);
                }
                scanAdapter = new DeviceAdapter(getApplicationContext(), R.layout.device_adapter_view_layout, scanList);
                scanListView.setAdapter(scanAdapter);
//                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    String deviceName = device.getName();
//                    String deviceHardwareAddress = device.getAddress(); // MAC address
//                    return;
//                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();;
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                startActivity(new Intent(getApplicationContext(), TaskActivity.class));
                overridePendingTransition(0, 0);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.onButton:
                if (!bluetoothAdapter.isEnabled()) {
                    Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        startActivityForResult(bluetoothIntent, BLUETOOTH_REQUEST_CODE);
                    } else {
                        bluetoothAdapter.disable();
                        onButton.setText("On Bluetooth");
                    }
                } else {
                    Toast.makeText(BluetoothActivity.this, "Turning off Bluetooth!", Toast.LENGTH_SHORT).show();
                    bluetoothAdapter.disable();
                    onButton.setText("On Bluetooth");
                }
                break;
            case R.id.discoverableButton:
                if (bluetoothAdapter.isEnabled()) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);
                } else {
                    Toast.makeText(BluetoothActivity.this, "Turn on Bluetooth first!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.scanButton:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // Query paired devices
                    pairedDevices = bluetoothAdapter.getBondedDevices();
                    pairedList.clear();
                    pairedList.addAll(pairedDevices);
                    pairAdapter = new DeviceAdapter(getApplicationContext(), R.layout.device_adapter_view_layout, pairedList);
                    pairListView.setAdapter(pairAdapter);

                    // Discover new devices
                    bluetoothAdapter.startDiscovery();
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(broadcastReceiver, filter);
                    return;
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BLUETOOTH_REQUEST_CODE:
                if (resultCode == RESULT_OK){
                    Toast.makeText(BluetoothActivity.this, "Bluetooth is ON", Toast.LENGTH_SHORT).show();
                    onButton.setText("Off Bluetooth");
                } else {
                    if (resultCode == RESULT_CANCELED) {
                        Toast.makeText(BluetoothActivity.this, "Bluetooth operation is cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
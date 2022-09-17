package ntu.mdp.grp42.bluetooth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import ntu.mdp.grp42.R;

public class BluetoothActivity extends AppCompatActivity
        implements BluetoothListener,
        View.OnClickListener,
        AdapterView.OnItemClickListener {

//    BottomNavigationView bottomNavigationView;

    public static final int BLUETOOTH_REQUEST_CODE = 1;

    Button onButton, discoverableButton, scanButton;
    ListView pairListView, scanListView;
    TextView pairTextView, scanTextView;
    BluetoothAdapter bluetoothAdapter;
    BluetoothService bluetoothService;


    private EditText messageBox;
    private Button sendButton;
    private static TextView messageList;



    Set<BluetoothDevice> pairedDevices, scannedDevices;
    ArrayAdapter pairAdapter, scanAdapter;
    ArrayList<BluetoothDevice> pairedList = new ArrayList<>();
    ArrayList<BluetoothDevice> scanList = new ArrayList<>();

    public static final Handler bluetoothMessageHandler = new Handler(Looper.myLooper(), message -> {
        if (message.what == Constants.MESSAGE_READ) {
            byte[] readBuf = (byte[]) message.obj;
            String strMessage = new String(readBuf, 0, message.arg1);
            messageList.setText(strMessage);

            try {
                JSONObject json = new JSONObject(strMessage);
//                rpiMessageHandler(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        onButton = findViewById(R.id.onButton);
        discoverableButton = findViewById(R.id.discoverableButton);
        scanButton = findViewById(R.id.scanButton);
        pairListView = findViewById(R.id.pairedListView);
        scanListView = findViewById(R.id.scannedListView);
        pairTextView = findViewById(R.id.pairedTextView);
        scanTextView = findViewById(R.id.scannedTextView);

        messageBox = findViewById(R.id.message_box);
        sendButton = findViewById(R.id.send_button);
        messageList = findViewById(R.id.communicate_messages);

        sendButton.setOnClickListener(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(BluetoothActivity.this, "This device doesn't support Bluetooth",
                    Toast.LENGTH_SHORT).show();
        }
        bluetoothService = new BluetoothService(bluetoothMessageHandler);
        bluetoothService.setOnBluetoothStatusChange(this);

        if (!bluetoothAdapter.isEnabled()) {
            onButton.setText("On Bluetooth");
        } else {
            onButton.setText("Off Bluetooth"); 
        }

        onButton.setOnClickListener(this);
        discoverableButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
        pairListView.setOnItemClickListener(this);
        scanListView.setOnItemClickListener(this);

        scanButton.performClick();
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
                if (!pairedDevices.contains(device) && !scanList.contains(device) && device.getName() != null) {
                    scanList.add(device);
                }

                // Save index and top position
                int index = scanListView.getFirstVisiblePosition();
                View view = scanListView.getChildAt(0);
                int top = (view == null) ? 0 : view.getTop();

                scanAdapter = new DeviceAdapter(getApplicationContext(), R.layout.device_adapter_view_layout, scanList);
                scanListView.setAdapter(scanAdapter);

                // Restore index and top position
                scanListView.setSelectionFromTop(index, top);


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
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void onBluetoothStatusChange(int status) {
        ArrayList<String> text = new ArrayList<>(Arrays.asList("Not Connected", "", "Connecting", "Connected"));
        runOnUiThread(() -> {
            setTitle(text.get(status));
        });
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
                if (bluetoothAdapter.isEnabled()) {
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
                } else {
                    Toast.makeText(BluetoothActivity.this, "Turn on Bluetooth first!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.send_button:
                String message = messageBox.getText().toString();
                if (!message.equals(""))
                    bluetoothService.write(message.getBytes(StandardCharsets.UTF_8));
                messageBox.setText("");
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(BluetoothActivity.this, "Turn on Bluetooth first!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            BluetoothDevice bluetoothDevice;
            switch (parent.getId()) {
                case R.id.pairedListView:
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothDevice = pairedList.get(position);
                    bluetoothDevice.createBond();
                    Log.d("BluetoothActivity", "Starting BT connection with " + bluetoothDevice.getName());
                    startBluetoothConnection(bluetoothDevice);
                    break;
                case R.id.scannedListView:
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothDevice = scanList.get(position);
                    bluetoothDevice.createBond();
                    Log.d("BluetoothActivity", "Starting BT connection with " + bluetoothDevice.getName());
                    startBluetoothConnection(bluetoothDevice);
                    break;
            }
            return;
        }
    }

    // This is responsible for setting the bluetooth button to display "On Bluetooth"
    // in the event that the user denies to turn on bluetooth during the permission prompt
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

    public void startBluetoothConnection(BluetoothDevice device) {
        Intent intent = new Intent();
        intent.putExtra("MAC", device.getAddress());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

//    private final Handler mHandler = new Handler() {
//        Toast toast;
//        String mConnectedDeviceName;
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case Constants.MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothService.STATE_CONNECTED:
//                            toast.cancel();
//                            toast.makeText(BluetoothActivity.this, "Bluetooth device connected", Toast.LENGTH_SHORT).show();
////                            mConversationArrayAdapter.clear();
//                            break;
//                        case BluetoothService.STATE_CONNECTING:
//                            toast.makeText(BluetoothActivity.this, "Connecting to bluetooth device", Toast.LENGTH_SHORT).show();
//                            break;
//                        case BluetoothService.STATE_LISTEN:
//                        case BluetoothService.STATE_NONE:
//                            break;
//                    }
//                    break;
//                case Constants.MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
////                    mConversationArrayAdapter.add("Me:  " + writeMessage);
//                    break;
//                case Constants.MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    // construct a string from the valid bytes in the buffer
//                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    messageList.setText(readMessage);
////                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
//                    break;
//                case Constants.MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
//                    Toast.makeText(BluetoothActivity.this, "Connected to "
//                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    break;
//                case Constants.MESSAGE_TOAST:
//                    Toast.makeText(BluetoothActivity.this, msg.getData().getString(Constants.TOAST),
//                            Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };
}
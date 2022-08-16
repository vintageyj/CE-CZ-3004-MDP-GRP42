package ntu.mdp.grp42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class BluetoothActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

//    BottomNavigationView bottomNavigationView;

    Button scanButton;
//    ListView scanListView;
//    ArrayList<String> stringArrayList = new ArrayList<String>();
//    ArrayAdapter<String> arrayAdapter;
//    BluetoothAdapter btAdapter;
//    BroadcastReceiver myReceiver = new BroadcastReceiver() {
//        @SuppressLint("MissingPermission")
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                stringArrayList.add(device.getName());
//                arrayAdapter.notifyDataSetChanged();
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.navigation_bluetooth);
//        bottomNavigationView.setOnNavigationItemSelectedListener(this);

//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (btAdapter == null) {
//            Toast.makeText(BluetoothActivity.this, "Bluetooth not supported!", Toast.LENGTH_SHORT).show();
//        } else {
//            if (!btAdapter.isEnabled()) {
//                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, TaskActivity.PERMISSIONS, 1);
//                    return;
//                }
//                startActivityForResult(intent, 1);
//            }
//        }

        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);
//        scanListView = (ListView) findViewById(R.id.scannedListView);
//
//        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(myReceiver, intentFilter);
//
//        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
//        scanListView.setAdapter(arrayAdapter);
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
//        switch (v.getId()) {
//            case R.id.scanButton:
//                Toast.makeText(getApplication().getBaseContext(), "Clicked!", Toast.LENGTH_SHORT).show();
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, TaskActivity.PERMISSIONS, 1);
//                    return;
//                }
//                boolean myswitch = btAdapter.startDiscovery();
//                if (!myswitch)
//                    Toast.makeText(getApplication().getBaseContext(), "Not Discovering!", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(getApplication().getBaseContext(), "Discovering!", Toast.LENGTH_SHORT).show();
//
//        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!TaskActivity.hasPermissions(this, TaskActivity.PERMISSIONS)) {
//            ActivityCompat.requestPermissions(this, TaskActivity.PERMISSIONS, 1);
//        }
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        btAdapter.startDiscovery();
//    }
}
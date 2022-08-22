package ntu.mdp.grp42;

import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DROP;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import ntu.mdp.grp42.arena.ArenaCell;
import ntu.mdp.grp42.bluetooth.BluetoothActivity;
import ntu.mdp.grp42.bluetooth.BluetoothListener;
import ntu.mdp.grp42.bluetooth.BluetoothService;
import ntu.mdp.grp42.bluetooth.RaspberryPiProtocol;
import ntu.mdp.grp42.fragment.*;

public class TaskActivity extends AppCompatActivity
        implements View.OnDragListener, BluetoothListener, View.OnClickListener, RaspberryPiProtocol {

    protected static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.VIBRATE
    };

//    BottomNavigationView bottomNavigationView;

    public static ActivityResultLauncher<Intent> activityResultLauncher;
    BluetoothAdapter bluetoothAdapter;
    public static BluetoothService bluetoothService;


    public static Vibrator vibrator;

    ArenaFragment arenaFragment;
    RightControlFragment rightControlFragment;
    control1Fragment control1Fragment;
    control2Fragment control2Fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        getSupportActionBar().hide();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        initViews();

        bluetoothService = new BluetoothService(BluetoothActivity.bluetoothMessageHandler);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService.setOnBluetoothStatusChange(this);
        arenaFragment.setBluetoothService(bluetoothService);
        control1Fragment.setBluetoothService(bluetoothService);
        control2Fragment.setBluetoothService(bluetoothService);

        getSelectedDevice();
    }

    @SuppressLint("MissingPermission")
    private void getSelectedDevice() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();
                Bundle bundle = intent.getExtras();
                String targetMACAddress = bundle.getString("MAC");
                BluetoothDevice targetDevice = bluetoothAdapter.getRemoteDevice(targetMACAddress);
                Toast.makeText(TaskActivity.this, "Connecting to " + targetDevice.getName(), Toast.LENGTH_SHORT).show();
                bluetoothService.connect(targetDevice);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        arenaFragment.setSpawnRG(rightControlFragment.getSpawnGroup());
    }

    protected static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }


    private void initViews() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        arenaFragment = (ArenaFragment) fragmentManager.findFragmentById(R.id.arenaFragment);
        rightControlFragment = (RightControlFragment) fragmentManager.findFragmentById(R.id.rightControlFragment);
        rightControlFragment.setArenaFragment(arenaFragment);

        control1Fragment = (control1Fragment) fragmentManager.findFragmentById(R.id.control1Fragment);
        control2Fragment = (control2Fragment) fragmentManager.findFragmentById(R.id.control2Fragment);

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.setOnDragListener(this);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case ACTION_DRAG_ENTERED:
            case ACTION_DRAG_EXITED:
                return true;
            case ACTION_DROP:
                ArenaCell arenaCell = (ArenaCell) event.getLocalState();
                arenaFragment.removeCell(arenaCell);
                return true;
        }
        return true;
    }

    public void onBluetoothStatusChange(int status) {
        ArrayList<String> connectionStatus = new ArrayList<>(Arrays.asList("Not Connected", "", "Connecting", "Connected"));
        runOnUiThread(() -> {
            rightControlFragment.getBluetoothBtn().setText(connectionStatus.get(status));
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetoothBtn:
                bluetoothService.start();
                activityResultLauncher.launch(new Intent(TaskActivity.this, BluetoothActivity.class));
                break;
        }
    }
}
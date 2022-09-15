package ntu.mdp.grp42;

import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DROP;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;

import ntu.mdp.grp42.arena.ArenaCell;
import ntu.mdp.grp42.bluetooth.BluetoothActivity;
import ntu.mdp.grp42.bluetooth.BluetoothListener;
import ntu.mdp.grp42.bluetooth.BluetoothService;
import ntu.mdp.grp42.bluetooth.Constants;
import ntu.mdp.grp42.bluetooth.RaspberryPiProtocol;
import ntu.mdp.grp42.fragment.ArenaFragment;
import ntu.mdp.grp42.fragment.BlankFragment;
import ntu.mdp.grp42.fragment.LeftFragment;
import ntu.mdp.grp42.fragment.RightControlFragment;
import ntu.mdp.grp42.fragment.StartTaskFragment;
import ntu.mdp.grp42.fragment.TimerFragment;
import ntu.mdp.grp42.fragment.control1Fragment;
import ntu.mdp.grp42.fragment.control2Fragment;

public class TaskActivity extends AppCompatActivity
        implements View.OnDragListener, BluetoothListener, View.OnClickListener, RaspberryPiProtocol, Constants{

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
    private BluetoothDevice targetDevice;
    private AlertDialog alertDialog;
    private Handler handler;

    public static Vibrator vibrator;

    ArenaFragment arenaFragment;
    LeftFragment leftStatusFragment;
    RightControlFragment rightControlFragment;
    control1Fragment control1Fragment;
    control2Fragment control2Fragment;

    private ViewPagerAdapter viewPagerAdapter, viewPagerAdapter2;
    private ViewPager viewPager, viewPager2;
    private TabLayout tabLayout, tabLayout2;
    private Button BTNTask1, BTNTask2, BTNreset, BTNstop;

    // D-Pad Controls
    ImageButton forwardButton, reverseButton, leftTurnButton, rightTurnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        viewPager = findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new StartTaskFragment(), "Start Tasks");
        viewPagerAdapter.add(new control1Fragment(), "Controllers");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager,true);

        viewPager2 = findViewById(R.id.viewpager2);
        viewPagerAdapter2 = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter2.add(new TimerFragment(), "Start Tasks");
        viewPagerAdapter2.add(new control2Fragment(), "Controllers");
        viewPager2.setAdapter(viewPagerAdapter2);

        tabLayout2 = findViewById(R.id.tab_layout2);
        tabLayout2.setupWithViewPager(viewPager2,true);



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                BTNTask1 = StartTaskFragment.getBTNtask1();
                BTNTask2 = StartTaskFragment.getBTNtask2();
                BTNstop = TimerFragment.getBTNstop();
                BTNreset = TimerFragment.getBTNreset();

                System.out.println("on page scrolled");
                View.OnClickListener task1Handle = new View.OnClickListener() {

                    public void onClick(View v) {
                        BTNTask2.setEnabled(false);
                        BTNreset.setEnabled(false);
                        TimerFragment.resetBTNeffect();
                        TimerFragment.taskEffect();
                        bluetoothService.write(START_TASK1.getBytes(StandardCharsets.UTF_8));
                    }};
                BTNTask1.setOnClickListener(task1Handle);

                View.OnClickListener task2Handle = new View.OnClickListener() {

                    public void onClick(View v) {
                        BTNTask1.setEnabled(false);
                        BTNreset.setEnabled(false);
                        TimerFragment.resetBTNeffect();
                        TimerFragment.taskEffect();
                        bluetoothService.write(START_TASK2.getBytes(StandardCharsets.UTF_8));
                    }};
                BTNTask2.setOnClickListener(task2Handle);

                View.OnClickListener stopHandle = new View.OnClickListener() {

                    public void onClick(View v) {
                        BTNreset.setEnabled(true);
                        TimerFragment.stopBTNeffect();
                        bluetoothService.write(STOP_TASK.getBytes(StandardCharsets.UTF_8));
                    }};
                BTNstop.setOnClickListener(stopHandle);

                View.OnClickListener resetHandle = new View.OnClickListener() {

                    public void onClick(View v) {
                        TimerFragment.resetBTNeffect();
                        BTNstop.setEnabled(false);
                        boolean stopped = TimerFragment.getStopStatus();
                        if (stopped) {
                            StartTaskFragment.resetTaskBTN();
                        } else {
                            TimerFragment.stopBTNeffect();
                            StartTaskFragment.resetTaskBTN();
                        }
                        BTNreset.setEnabled(false);

                    }};
                BTNreset.setOnClickListener(resetHandle);
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        // init for first fragment

                        break;
                    case 1:
                        System.out.println("in controllers view");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        tabLayout2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (viewPager2.getCurrentItem() == 0) {
                    TabLayout.Tab tab1 = tabLayout.getTabAt(1);
                    tab1.select();
                }
                if (viewPager2.getCurrentItem() == 1) {
                    TabLayout.Tab tab1 = tabLayout.getTabAt(0);
                    tab1.select();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });

        getSupportActionBar().hide();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        initViews();

        bluetoothService = new BluetoothService(bluetoothMessageHandler);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService.setOnBluetoothStatusChange(this);
        handler = new Handler();

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
                targetDevice = bluetoothAdapter.getRemoteDevice(targetMACAddress);
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
        leftStatusFragment = (LeftFragment) fragmentManager.findFragmentById(R.id.leftControlFragment);
        rightControlFragment = (RightControlFragment) fragmentManager.findFragmentById(R.id.rightControlFragment);
        control1Fragment = (control1Fragment) fragmentManager.findFragmentById(R.id.control1Fragment);
        control2Fragment = (control2Fragment) fragmentManager.findFragmentById(R.id.control2Fragment);

        rightControlFragment.setArenaFragment(arenaFragment);
        control1Fragment.setArenaFragment(arenaFragment);

        forwardButton = control1Fragment.getForwardButton();
        reverseButton = control1Fragment.getReverseButton();
        leftTurnButton = control2Fragment.getLeftTurnButton();
        rightTurnButton = control2Fragment.getRightTurnButton();

        forwardButton.setOnClickListener(this);
        reverseButton.setOnClickListener(this);
        leftTurnButton.setOnClickListener(this);
        rightTurnButton.setOnClickListener(this);

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
                arenaFragment.removeAdjacentCellHighlights(arenaFragment.getLastCell());
                return true;
        }
        return true;
    }

    public void onBluetoothStatusChange(int status) {
        ArrayList<String> connectionStatus = new ArrayList<>(Arrays.asList(NOT_CONNECTED, "",CONNECTING, CONNECTED));
        ArrayList<String> connectionStatusColor = new ArrayList<>(Arrays.asList("#B11226", "", "#FEC20C", "#00FF00"));
        runOnUiThread(() -> {
            rightControlFragment.getBluetoothBtn().setText(connectionStatus.get(status));
            rightControlFragment.getBluetoothBtn().setTextColor(Color.parseColor(connectionStatusColor.get(status)));
            if (bluetoothService.state == BluetoothService.STATE_NONE) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Reconnect to Bluetooth Device");
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    handler.postDelayed(() -> {
                        try {
                            bluetoothService.connect(targetDevice);
                        } catch (Exception e) {
                            Toast.makeText(TaskActivity.this, "Failed to reconnect due to " + e, Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                });
                builder.setNegativeButton("No", null);
                alertDialog = builder.show();
            } else {
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
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

    public final Handler bluetoothMessageHandler = new Handler(Looper.myLooper(), message -> {
        if (message.what == Constants.MESSAGE_READ) {
            byte[] readBuf = (byte[]) message.obj;
            String strMessage = new String(readBuf, 0, message.arg1);
            receiveMessage(strMessage);
            try {
                JSONObject json = new JSONObject(strMessage);
//                rpiMessageHandler(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    });

    public void writeMessage(String message){
        String[] strMessage = message.split(" ", 2);
        String command = strMessage[0];
        String data = strMessage[1];
//        switch(command) {
//            case SPAWN_ROBOT:
//                message = SPAWN_ROBOT + "," + data;
//                bluetoothService.write(message.getBytes(StandardCharsets.UTF_8));
//                break;
//            case ROTATE_ROBOT:
//                message = ROTATE_ROBOT + "," + data;
//                bluetoothService.write(message.getBytes(StandardCharsets.UTF_8));
//            case ADD_OBSTACLE:
//                break;
//            case REMOVE_OBSTACLE:
//                break;
//        }
    }

    private void receiveMessage(String message) {
        leftStatusFragment.setDebugWindow(message);
        try {
            String[] strMessage = message.split(" ");
            Gson gson = new Gson();
            switch (strMessage[0]) {
                case STATUS:
                    leftStatusFragment.setRobotStatus(strMessage[1]);
                    break;
                case UPDATE_OBSTACLE:
                    arenaFragment.updateCellImage(Integer.parseInt(strMessage[1]), Integer.parseInt(strMessage[2]));
                    break;
                case FORWARD:
                    arenaFragment.forwardRobot();
                    break;
                case REVERSE:
                    arenaFragment.reverseRobot();
                    break;
                case LEFT_TURN:
                    arenaFragment.rotateRobotLeft();
                    break;
                case RIGHT_TURN:
                    arenaFragment.rotateRobotRight();
                    break;
                case STOP_TASK:
                    BTNstop.performClick();
            }
        } catch (Exception e) {
            Log.e("receiveMessage", "Can't receive message" + e);
        }
    }
}
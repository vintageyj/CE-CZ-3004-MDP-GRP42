package ntu.mdp.grp42;

import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DROP;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import ntu.mdp.grp42.arena.ArenaCell;
import ntu.mdp.grp42.arena.Obstacle;
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
import ntu.mdp.grp42.fragment.VideoFragment;
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
    ArrayList<String> connectionStatus = new ArrayList<>(Arrays.asList(NOT_CONNECTED, "",CONNECTING, CONNECTED));
    ArrayList<String> connectionStatusColor = new ArrayList<>(Arrays.asList("#FF0000", "", "#FEC20C", "#00FF00"));
    private AlertDialog alertDialog;
    private Handler handler;

    private boolean rpi_connected = false;
    private boolean stm_connected = false;
    private boolean pc_connected = false;
    private boolean disconnected = false;
    private boolean reconnecting = false;
    private int reconnectAttempt = 0;

    Queue<String> instructions = new LinkedList<>();
    private boolean playingBack = false;

    public static Vibrator vibrator;

    static FragmentManager fragmentManager;
    ArenaFragment arenaFragment;
    LeftFragment leftStatusFragment;
    RightControlFragment rightControlFragment;
    control1Fragment control1Fragment;
    control2Fragment control2Fragment;
    VideoFragment videoFragment;

    private ViewPagerAdapter viewPagerAdapter, viewPagerAdapter2, viewPagerAdapter3, viewPagerAdapter4;
    private ViewPager viewPager, viewPager2, viewPager3, viewPager_video;
    private TabLayout tabLayout, tabLayout2, tabLayout_video;
    private static TabLayout tabLayout3;
    private Button btnTask1, btnTask2, btnReset, btnStop;
    private Button photoBtn;
    private boolean timer_ready;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // Hides the status and action bars
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();
//        showSplash();

        // Checks for app permissions and requests for missing ones
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        fragmentManager = getSupportFragmentManager();
        initViews();

        bluetoothService = new BluetoothService(bluetoothMessageHandler);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService.setOnBluetoothStatusChange(this);
        handler = new Handler();

        getSelectedDevice();
    }

    public void showSplash() {

        final Dialog dialog = new Dialog(TaskActivity.this, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setLayout(width, height);
        dialog.setContentView(R.layout.activity_main);
        dialog.setCancelable(true);
        dialog.show();

        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                {
                    dialog.dismiss();
                }
            }
        };
        handler.postDelayed(runnable, 4000);
    }

    public static void swapFragments(int index) {
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.centerLayout, fragment);
//        fragmentTransaction.commit();
        TabLayout.Tab tab = tabLayout3.getTabAt(index);
        tab.select();
    }

    // For removing the status bar
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//    }


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

//        arenaFragment = (ArenaFragment) fragmentManager.findFragmentById(R.id.arenaFragment);
        leftStatusFragment = (LeftFragment) fragmentManager.findFragmentById(R.id.leftControlFragment);
        rightControlFragment = (RightControlFragment) fragmentManager.findFragmentById(R.id.rightControlFragment);
        control1Fragment = (control1Fragment) fragmentManager.findFragmentById(R.id.control1Fragment);
        control2Fragment = (control2Fragment) fragmentManager.findFragmentById(R.id.control2Fragment);
        videoFragment = (VideoFragment) fragmentManager.findFragmentById(R.id.videoFragment);

        LinearLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.setOnDragListener(this);

        initTabs();
        arenaFragment = (ArenaFragment) viewPagerAdapter3.getItem(0);
        rightControlFragment.setArenaFragment(arenaFragment);
        rightControlFragment.setVideoFragment(videoFragment);
    }

    private void initTabs() {
        viewPager = findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager);
        viewPagerAdapter.add(new StartTaskFragment(), "Start Tasks");
        viewPagerAdapter.add(new control2Fragment(), "Controllers");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager,true);

        viewPager2 = findViewById(R.id.viewpager2);
        viewPagerAdapter2 = new ViewPagerAdapter(fragmentManager);
        viewPagerAdapter2.add(new TimerFragment(), "Start Tasks");
        viewPagerAdapter2.add(new control1Fragment(), "Controllers");
        viewPager2.setAdapter(viewPagerAdapter2);

        tabLayout2 = findViewById(R.id.tab_layout2);
        tabLayout2.setupWithViewPager(viewPager2,true);

        viewPager3 = findViewById(R.id.viewpager3);
        viewPagerAdapter3 = new ViewPagerAdapter(fragmentManager);
        viewPagerAdapter3.add(new ArenaFragment(), "Arena");
        viewPagerAdapter3.add(new VideoFragment(), "Video");
        viewPager3.setAdapter(viewPagerAdapter3);

        tabLayout3 = findViewById(R.id.tab_layout3);
        tabLayout3.setupWithViewPager(viewPager3, true);

        viewPager_video = findViewById(R.id.viewpager_video);
        viewPagerAdapter4 = new ViewPagerAdapter(fragmentManager);
        viewPagerAdapter4.add(new VideoFragment(), "Video");
        viewPagerAdapter4.add(new BlankFragment(), "Blank");
        viewPager_video.setAdapter(viewPagerAdapter4);

        tabLayout_video = findViewById(R.id.tab_layout_video);
        tabLayout_video.setupWithViewPager(viewPager_video, true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                btnTask1 = StartTaskFragment.getBTNtask1();
                btnTask2 = StartTaskFragment.getBTNtask2();
                btnStop = TimerFragment.getBTNstop();
                btnReset = TimerFragment.getBTNreset();
                photoBtn = StartTaskFragment.getPhotoBtn();

                photoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bluetoothService.write(TAKE_PHOTO.getBytes(StandardCharsets.UTF_8));
                    }
                });

                View.OnClickListener task1Handle = new View.OnClickListener() {

                    public void onClick(View v) {
                        btnTask2.setEnabled(false);
                        btnStop.setEnabled(true);
                        timer_ready = true;
                        bluetoothService.write(START_TASK1.getBytes(StandardCharsets.UTF_8));
                    }};
                btnTask1.setOnClickListener(task1Handle);

                View.OnClickListener task2Handle = new View.OnClickListener() {

                    public void onClick(View v) {
                        btnTask1.setEnabled(false);
                        btnStop.setEnabled(true);
                        timer_ready = true;
                        bluetoothService.write(START_TASK2.getBytes(StandardCharsets.UTF_8));
                    }};
                btnTask2.setOnClickListener(task2Handle);

                View.OnClickListener stopHandle = new View.OnClickListener() {

                    public void onClick(View v) {
                        //check if task buttons are pressed
                        if (timer_ready){
                            //start timer
                            TimerFragment.setStartTime();
                            btnReset.setEnabled(true);
                            timer_ready = false;
                        } else {
                            //stop timer
                            TimerFragment.stopBTNeffect();
                            bluetoothService.write(STOP_TASK.getBytes(StandardCharsets.UTF_8));
                            timer_ready = true;
                        }
                    }};
                btnStop.setOnClickListener(stopHandle);

                View.OnClickListener resetHandle = new View.OnClickListener() {

                    public void onClick(View v) {
                        TimerFragment.resetBTNeffect();
                        btnStop.setEnabled(false);
                        boolean stopped = TimerFragment.getStopStatus();
                        if (!stopped)
                            TimerFragment.stopBTNeffect();
                        StartTaskFragment.resetTaskBTN();
                        btnReset.setEnabled(false);

                    }};
                btnReset.setOnClickListener(resetHandle);
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        // init for first fragment

                        break;
                    case 1:
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

        tabLayout3.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (viewPager3.getCurrentItem() == 0) {
                    TabLayout.Tab tab1 = tabLayout_video.getTabAt(1);
                    tab1.select();
                }
                if (viewPager3.getCurrentItem() == 1) {
                    TabLayout.Tab tab1 = tabLayout_video.getTabAt(0);
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
        if (status == 0) {
            disconnected = true;
            if (reconnecting == false) {
                reconnecting = true;
                reconnectAttempt = 0;
            }
            leftStatusFragment.setRobotStatus("Offline");
            rpi_connected = false;
            stm_connected = false;
            pc_connected = false;
//            rightControlFragment.setArenaSettingsVisibility(View.INVISIBLE);
        } else if (status == 2) {
            if (disconnected) {
                updateBluetoothStatus(status);
            }
            leftStatusFragment.setRobotStatus("Connecting...");
        } else if (status == 3) {
            disconnected = false;
            reconnectAttempt = 0;
            reconnecting = false;
            query_connection();
        }
        runOnUiThread(() -> {
            if (bluetoothService.state == BluetoothService.STATE_NONE) {
                if (reconnectAttempt < RECONNECTION_LIMIT && reconnecting == true) {
                    handler.postDelayed(() -> {
                        try {
                            bluetoothService.connect(targetDevice);
//                            Toast.makeText(TaskActivity.this, "Reconnecting " + reconnectAttempt, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(TaskActivity.this, "Failed to reconnect due to " + e, Toast.LENGTH_SHORT).show();
                        } finally {
                            handler.postDelayed(() -> {
                                reconnectAttempt++;
                            }, 1000);
                        }
                    }, 1000);
                } else {
                    reconnecting = false;
                }

                // AlertDialog for manual input on reconnection
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Reconnect to Bluetooth Device");
//                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
//                    handler.postDelayed(() -> {
//                        try {
//                            bluetoothService.connect(targetDevice);
//                        } catch (Exception e) {
//                            Toast.makeText(TaskActivity.this, "Failed to reconnect due to " + e, Toast.LENGTH_SHORT).show();
//                        }
//                    }, 5000);
//                });
//                builder.setNegativeButton("No", null);
//                alertDialog = builder.show();

            } else {
                if (alertDialog != null)
                    alertDialog.dismiss();
            }
            if (reconnecting && (bluetoothService.state != 0 || bluetoothService.state != 3))
                return;
            else {
                updateBluetoothStatus(status);
            }
        });
    }

    private void query_connection() {
        if (!checkFullConnections()) {
            writeMessage(CONNECTION + " " + HOW);
            handler.postDelayed(() -> {
                if (!checkFullConnections())
                    query_connection();
            }, 3000);
        }
    }

    private void updateBluetoothStatus(int status) {
        switch (status) {
            case 0:
                leftStatusFragment.setRedAll();
                break;
            case 2:
                leftStatusFragment.setRpiColor(2);
                leftStatusFragment.setPcColor(0);
                leftStatusFragment.setStmColor(0);
                break;
            case 3:
                leftStatusFragment.setYellowAll();
                break;
        }


        if (status == 2) {
            rightControlFragment.spinner.setVisibility(View.VISIBLE);
        } else {
            rightControlFragment.spinner.setVisibility(View.INVISIBLE);
        }
        if (disconnected && status == 2)
            rightControlFragment.getBluetoothBtn().setText("Re" + connectionStatus.get(status));
        else
            rightControlFragment.getBluetoothBtn().setText(connectionStatus.get(status));
        rightControlFragment.getBluetoothBtn().setTextColor(Color.parseColor(connectionStatusColor.get(status)));
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
                Intent intent = new Intent(TaskActivity.this, BluetoothActivity.class);
//                Button button = rightControlFragment.getBluetoothBtn();
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TaskActivity.this, button, ViewCompat.getTransitionName(button));
                activityResultLauncher.launch(intent);

                break;
            default:
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
        bluetoothService.write(message.getBytes(StandardCharsets.UTF_8));
    }

    private void receiveMessage(String message) {
        leftStatusFragment.setDebugWindow(message);
        try {
            String[] strMessage = message.split(" ");
            Gson gson = new Gson();
            switch (strMessage[0]) {
                case ALGO_INSTRUCTION:
                    instructions.offer(strMessage[1]);
                    if (!playingBack)
                        pathPlayback(instructions);
                    break;

                case PREDICTED_PATH:
                    int[][] path = gson.fromJson(strMessage[1], int[][].class);
                    updatePredictedPath(path);
                    break;
                case CONNECTION:
                    if (strMessage[1].equals(RPI)) {
                        leftStatusFragment.setRpiColor(3);
                        rpi_connected = true;
                    }
                    else if (strMessage[1].equals(PC)) {
                        leftStatusFragment.setPcColor(3);
                        pc_connected = true;
                    }
                    else if (strMessage[1].equals(STM)) {
                        leftStatusFragment.setStmColor(3);
                        stm_connected = true;
                    }
                    checkFullConnections();
                    break;
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
                    btnStop.performClick();
            }
        } catch (Exception e) {
            Log.e("receiveMessage", "Can't receive message" + e);
        }
    }

    private void pathPlayback(Queue<String> instructions) {
        String instruction = instructions.remove();
        Handler handler = new Handler();
        playingBack = true;
        String command = instruction.substring(0,1);
        String distance;
        int delay = 300;
        int totalDelay = delay;
        switch (command) {
            case "f":
                distance = instruction.substring(1);
                for (int i = 0; i < (int) (Float.valueOf(distance) / 10); i++) {
                    handler.postDelayed(() -> arenaFragment.forwardRobot(), delay * i);
                    totalDelay += delay;
                }
                break;

            case "b":
                distance = instruction.substring(1);
                for (int i = 0; i < (int) (Float.valueOf(distance) / 10); i++) {
                    handler.postDelayed(() -> arenaFragment.reverseRobot(), delay * i);
                    totalDelay += delay;
                }
                break;

            case "q":
                handler.postDelayed(() -> arenaFragment.forwardRobot(), 0);
                handler.postDelayed(() -> arenaFragment.forwardRobot(), delay * 1);
                handler.postDelayed(() -> arenaFragment.rotateRobotLeft(), delay * 2);
                handler.postDelayed(() -> arenaFragment.forwardRobot(), delay * 3);
                handler.postDelayed(() -> arenaFragment.forwardRobot(), delay * 4);
                totalDelay += delay * 4;
                break;

            case "e":
                handler.postDelayed(() -> arenaFragment.forwardRobot(), 0);
                handler.postDelayed(() -> arenaFragment.forwardRobot(), delay * 1);
                handler.postDelayed(() -> arenaFragment.rotateRobotRight(), delay * 2);
                handler.postDelayed(() -> arenaFragment.forwardRobot(), delay * 3);
                handler.postDelayed(() -> arenaFragment.forwardRobot(), delay * 4);
                totalDelay += delay * 4;
                break;

            case "z":
                handler.postDelayed(() -> arenaFragment.reverseRobot(), 0);
                handler.postDelayed(() -> arenaFragment.reverseRobot(), delay * 1);
                handler.postDelayed(() -> arenaFragment.rotateRobotRight(), delay * 2);
                handler.postDelayed(() -> arenaFragment.reverseRobot(), delay * 3);
                handler.postDelayed(() -> arenaFragment.reverseRobot(), delay * 4);
                totalDelay += delay * 4;
                break;

            case "c":
                handler.postDelayed(() -> arenaFragment.reverseRobot(), 0);
                handler.postDelayed(() -> arenaFragment.reverseRobot(), delay * 1);
                handler.postDelayed(() -> arenaFragment.rotateRobotLeft(), delay * 2);
                handler.postDelayed(() -> arenaFragment.reverseRobot(), delay * 3);
                handler.postDelayed(() -> arenaFragment.reverseRobot(), delay * 4);
                totalDelay += delay * 4;
                break;
        }

        handler.postDelayed(() -> {
            if (!instructions.isEmpty()) {
                pathPlayback(instructions);
            } else {
                playingBack = false;
            }
        }, totalDelay);
    }

    private boolean checkFullConnections() {
        if (!rpi_connected || !stm_connected || !pc_connected) {
//            rightControlFragment.setArenaSettingsVisibility(View.INVISIBLE);
            return false;
        } else {
//            rightControlFragment.setArenaSettingsVisibility(View.VISIBLE);
            leftStatusFragment.setRobotStatus("Online! Robot Idling");
            Toast.makeText(this, "Full Connection Established with Robot!", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void updatePredictedPath(int[][] path) {
        arenaFragment.hidePredictedCells();
        arenaFragment.removePredictedCells();
        arenaFragment.predictedPath = path;
        if (arenaFragment.predictedPathShown) {
            arenaFragment.showPredictedCells();
        }
    }

    public void sendCommand(String message) {
        String[] messages = message.split(" ", 2);
        switch (messages[0]) {
            case FORWARD:
                writeMessage(STM + " " + FORWARD);
                break;
            case REVERSE:
                writeMessage(STM + " " + REVERSE);
                break;
            case LEFT_TURN:
                writeMessage(STM + " " + LEFT_TURN);
                break;
            case RIGHT_TURN:
                writeMessage(STM + " " + RIGHT_TURN);
                break;
        }
    }

    public void sendCommand(String message, Object object) {
        ArenaCell arenaCell;
        Obstacle obstacle;
        switch (message) {
            case SPAWN_ROBOT:
                arenaCell = (ArenaCell) object;
                String prependMsg = getObstacles();
                writeMessage(String.format("%s %s|%s|%d,%d,%s", PC, prependMsg, SPAWN_ROBOT, arenaCell.x, arenaCell.y, "N"));
                break;
//            case ADD_OBSTACLE:
//                obstacle = (Obstacle) object;
//                writeMessage(String.format("%s %s|%d,%d,%d,%d", PC, ADD_OBSTACLE, obstacle.obstacleID,obstacle.x, obstacle.y, obstacle.direction));
//                break;
            case REMOVE_OBSTACLE:
                arenaCell = (ArenaCell) object;
                writeMessage(String.format("%s %d %d", REMOVE_OBSTACLE, arenaCell.x, arenaCell.y));
                break;
        }
    }

    private String getObstacles() {
        String result = "POS";
        int totalObstacles = arenaFragment.arenaCoord.length * arenaFragment.arenaCoord[0].length;
        for (int obstacleID = 1; obstacleID <= totalObstacles; obstacleID++) {
            if (arenaFragment.obstacleList.containsKey(obstacleID)) {
                Obstacle obstacle = arenaFragment.obstacleList.get(obstacleID);
                result = result + String.format("|%d,%d,%d,%s", obstacle.obstacleID, obstacle.x, obstacle.y, arenaFragment.facings[obstacle.direction]);
            }
        }
        return result;
    }

    public void moveRobot(int direction) {
        switch (direction) {
            case 0:
                arenaFragment.forwardRobot();
                break;
            case 1:
                arenaFragment.rotateRobotRight();
                break;
            case 2:
                arenaFragment.reverseRobot();
                break;
            case 3:
                arenaFragment.rotateRobotLeft();
                break;
        }
    }
}
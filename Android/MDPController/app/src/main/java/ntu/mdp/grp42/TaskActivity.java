package ntu.mdp.grp42;

import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DROP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ntu.mdp.grp42.arena.ArenaCell;
import ntu.mdp.grp42.bluetooth.BluetoothActivity;
import ntu.mdp.grp42.fragment.ArenaFragment;
import ntu.mdp.grp42.fragment.RightControlFragment;

public class TaskActivity extends AppCompatActivity
    implements BottomNavigationView.OnNavigationItemSelectedListener,
        View.OnDragListener{

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

    public static Vibrator vibrator;

    ArenaFragment arenaFragment;
    RightControlFragment rightControlFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        getSupportActionBar().hide();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
        
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
//        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        initViews();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.navigation_bluetooth:
                startActivity(new Intent(getApplicationContext(), BluetoothActivity.class));
                overridePendingTransition(0,0);
                return true;
            default:
                return false;
        }
    }

    private void initViews() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        arenaFragment = (ArenaFragment) fragmentManager.findFragmentById(R.id.arenaFragment);
        rightControlFragment = (RightControlFragment) fragmentManager.findFragmentById(R.id.rightControlFragment);
        rightControlFragment.setArenaFragment(arenaFragment);

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
}
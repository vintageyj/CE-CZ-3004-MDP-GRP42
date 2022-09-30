package ntu.mdp.grp42.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.TaskActivity;
import ntu.mdp.grp42.bluetooth.BluetoothService;
import ntu.mdp.grp42.bluetooth.RaspberryPiProtocol;

public class control1Fragment extends Fragment implements View.OnClickListener, RaspberryPiProtocol, View.OnTouchListener {

    TaskActivity taskActivity;
    ArenaFragment arenaFragment;
    ImageButton forwardButton, reverseButton;

    public control1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        taskActivity = (TaskActivity) getActivity();

        forwardButton = view.findViewById(R.id.upBtn);
        reverseButton = view.findViewById(R.id.downBtn);

//        forwardButton.setOnClickListener(this);
//        reverseButton.setOnClickListener(this);
        forwardButton.setOnTouchListener(this);
        reverseButton.setOnTouchListener(this);

//        arenaFragment = (ArenaFragment) getFragmentManager().findFragmentById(R.id.arenaFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upBtn:
                taskActivity.moveRobot(0);
                taskActivity.sendCommand(FORWARD);
                break;
            case R.id.downBtn:
                taskActivity.moveRobot(2);
                taskActivity.sendCommand(REVERSE);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.upBtn:
                        taskActivity.moveRobot(0);
                        taskActivity.sendCommand(FORWARD);
                        break;
                    case R.id.downBtn:
                        taskActivity.moveRobot(2);
                        taskActivity.sendCommand(REVERSE);
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
//                    case R.id.upBtn:
//                        taskActivity.sendCommand(FORWARD);
//                        break;
//                    case R.id.downBtn:
//                        taskActivity.sendCommand(REVERSE);
//                        break;
                }
                break;
        }
        return false;
    }

//    public void setArenaFragment(ArenaFragment arenaFragment) {
//        this.arenaFragment = arenaFragment;
//    }
}
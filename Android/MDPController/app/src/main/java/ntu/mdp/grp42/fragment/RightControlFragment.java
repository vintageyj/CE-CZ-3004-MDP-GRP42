package ntu.mdp.grp42.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;

import ntu.mdp.grp42.TaskActivity;
import ntu.mdp.grp42.bluetooth.BluetoothActivity;
import ntu.mdp.grp42.R;

public class RightControlFragment extends Fragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Switch predictedPathSwitch;
    private Switch takenPathSwitch;

    RadioGroup spawnRG;
    Button bluetoothBtn, arenaButton, videoButton;
    private ArenaFragment arenaFragment;
    private VideoFragment videoFragment;
    public ProgressBar spinner;

    public RightControlFragment() {
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
        return inflater.inflate(R.layout.fragment_right_control, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        predictedPathSwitch = view.findViewById(R.id.predictedPathSwitch);
        predictedPathSwitch.setOnCheckedChangeListener(this);
        takenPathSwitch = view.findViewById(R.id.takenPathSwitch);
        takenPathSwitch.setOnCheckedChangeListener(this);
        spawnRG = view.findViewById(R.id.spawnRG);
        bluetoothBtn = view.findViewById(R.id.bluetoothBtn);
        bluetoothBtn.setOnClickListener(this);
        spinner = (ProgressBar) view.findViewById(R.id.progressBarBT);
        spinner.setVisibility(View.INVISIBLE);
        arenaButton = view.findViewById(R.id.arenaButton);
        videoButton = view.findViewById(R.id.videoButton);
        arenaButton.setOnClickListener(this);
        videoButton.setOnClickListener(this);
    }

    public RadioGroup getSpawnGroup() {
        return spawnRG;
    }
    public void setArenaFragment(ArenaFragment arenaFragment) {
        this.arenaFragment = arenaFragment;
    }

    public void setVideoFragment(VideoFragment videoFragment) {
        this.videoFragment = videoFragment;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bluetoothBtn:
                TaskActivity.bluetoothService.start();
                TaskActivity.activityResultLauncher.launch(new Intent(getActivity(), BluetoothActivity.class));
                break;
            case R.id.arenaButton:
                TaskActivity.swapFragments(0);

                break;
            case R.id.videoButton:
                TaskActivity.swapFragments(1);
                break;
        }
    }

    public Button getBluetoothBtn() {
        return bluetoothBtn;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == predictedPathSwitch) {
            if (predictedPathSwitch.isChecked()) {
                arenaFragment.showPredictedCells();
                arenaFragment.predictedPathShown = true;
            } else {
                arenaFragment.hidePredictedCells();
                arenaFragment.predictedPathShown = false;
            }
        } else if (compoundButton == takenPathSwitch) {
            if (takenPathSwitch.isChecked()) {
                arenaFragment.showTakenCells();
                arenaFragment.showTakenPath = true;
            } else {
                arenaFragment.hideTakenCells();
                arenaFragment.showTakenPath = false;
            }
        }
    }
}
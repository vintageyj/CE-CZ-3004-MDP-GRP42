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
import android.widget.Toast;

import ntu.mdp.grp42.TaskActivity;
import ntu.mdp.grp42.bluetooth.BluetoothActivity;
import ntu.mdp.grp42.R;

public class RightControlFragment extends Fragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Switch predictedPathSwitch;
    private Switch takenPathSwitch;

    RadioGroup spawnRG;
    Button bluetoothBtn;
    private ArenaFragment arenaFragment;
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
    }

    public RadioGroup getSpawnGroup() {
        return spawnRG;
    }
    public void setArenaFragment(ArenaFragment arenaFragment) {
        this.arenaFragment = arenaFragment;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bluetoothBtn:
                TaskActivity.bluetoothService.start();
                TaskActivity.activityResultLauncher.launch(new Intent(getActivity(), BluetoothActivity.class));
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
                arenaFragment.removePredictedCells();
                arenaFragment.predictedPathShown = false;
            }
        } else if (compoundButton == takenPathSwitch) {
            if (takenPathSwitch.isChecked()) {
                arenaFragment.showTakenCells();
            } else {
                arenaFragment.removeTakenCells();
            }
        }
    }
}
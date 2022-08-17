package ntu.mdp.grp42.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import ntu.mdp.grp42.bluetooth.BluetoothActivity;
import ntu.mdp.grp42.R;

public class RightControlFragment extends Fragment
    implements View.OnClickListener{
    RadioGroup spawnRG;
    Button bluetoothBtn;
    private ArenaFragment arenaFragment;

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
        spawnRG = view.findViewById(R.id.spawnRG);
        bluetoothBtn = view.findViewById(R.id.bluetoothBtn);
        bluetoothBtn.setOnClickListener(this);
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
                startActivity(new Intent(getActivity(), BluetoothActivity.class));
                break;
        }
    }
}
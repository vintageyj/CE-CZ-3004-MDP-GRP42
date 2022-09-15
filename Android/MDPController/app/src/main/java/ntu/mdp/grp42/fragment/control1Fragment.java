package ntu.mdp.grp42.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.TaskActivity;
import ntu.mdp.grp42.bluetooth.BluetoothService;
import ntu.mdp.grp42.bluetooth.RaspberryPiProtocol;

public class control1Fragment extends Fragment implements View.OnClickListener, RaspberryPiProtocol {

    TaskActivity taskActivity;
    ArenaFragment arenaFragment;
    BluetoothService bluetoothService;
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

        forwardButton.setOnClickListener(this);
        reverseButton.setOnClickListener(this);

        arenaFragment = (ArenaFragment) getFragmentManager().findFragmentById(R.id.arenaFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upBtn:
                arenaFragment.forwardRobot();
                try {
                    taskActivity.send
                    Toast.makeText(getContext(), "Pressed UP Btn", Toast.LENGTH_SHORT).show();
                } finally {
                    Toast.makeText(getContext(), "Bluetooth Service is null!", Toast.LENGTH_SHORT).show();
                }
                    break;
            case R.id.downBtn:
                arenaFragment.reverseRobot();
                if (bluetoothService != null)
                    bluetoothService.write(REVERSE.getBytes(StandardCharsets.UTF_8));
                break;
        }
    }

    public void setBluetoothService(BluetoothService bluetoothService) {
        Toast.makeText(getContext(), "Setting BTService", Toast.LENGTH_SHORT).show();
        this.bluetoothService = bluetoothService;
    }

    public void setArenaFragment(ArenaFragment arenaFragment) {
        this.arenaFragment = arenaFragment;
    }
}
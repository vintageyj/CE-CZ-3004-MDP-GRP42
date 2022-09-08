package ntu.mdp.grp42.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.nio.charset.StandardCharsets;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.bluetooth.BluetoothService;
import ntu.mdp.grp42.bluetooth.RaspberryPiProtocol;

public class control1Fragment extends Fragment implements View.OnClickListener, RaspberryPiProtocol {

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
                if (bluetoothService != null)
                    bluetoothService.write(FORWARD.getBytes(StandardCharsets.UTF_8));
                break;
            case R.id.downBtn:
                arenaFragment.reverseRobot();
                if (bluetoothService != null)
                    bluetoothService.write(REVERSE.getBytes(StandardCharsets.UTF_8));
                break;
        }
    }

    public void setBluetoothService(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    public void setArenaFragment(ArenaFragment arenaFragment) {
        this.arenaFragment = arenaFragment;
    }
}
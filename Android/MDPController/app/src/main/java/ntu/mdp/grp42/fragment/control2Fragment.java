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

public class control2Fragment extends Fragment implements RaspberryPiProtocol, View.OnClickListener {
    private BluetoothService bluetoothService;

    private ImageButton leftTurnButton, rightTurnButton;

    public control2Fragment() {
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
        return inflater.inflate(R.layout.fragment_control2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        leftTurnButton = view.findViewById(R.id.leftBtn);
        rightTurnButton = view.findViewById(R.id.rightBtn);

        leftTurnButton.setOnClickListener(this);
        rightTurnButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (bluetoothService != null)
            switch (v.getId()) {
                case R.id.leftBtn:
                    bluetoothService.write(LEFT_TURN.getBytes(StandardCharsets.UTF_8));
                    break;
                case R.id.rightBtn:
                    bluetoothService.write(RIGHT_TURN.getBytes(StandardCharsets.UTF_8));
                    break;
            }
    }

    public void setBluetoothService(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }
}
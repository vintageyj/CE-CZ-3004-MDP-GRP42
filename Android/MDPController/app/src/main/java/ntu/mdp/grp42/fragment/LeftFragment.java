package ntu.mdp.grp42.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.bluetooth.Constants;

public class LeftFragment extends Fragment implements Constants {
    private TextView robotStatus, robotDirection, robotCoordinates, debugWindow;
    private int x, y;

    public LeftFragment() {
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
        return inflater.inflate(R.layout.fragment_left, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        robotStatus = view.findViewById(R.id.robotStatus);
        robotDirection = view.findViewById(R.id.robotDirection);
        robotCoordinates = view.findViewById(R.id.robotCoordinates);
        debugWindow = view.findViewById(R.id.debugWindow);
    }

    public void setRobotStatus(String robotStatus) {
        this.robotStatus.setText(robotStatus);
    }

    public void setRobotDirection(int direction) {
        switch (direction) {
            case UP:
                robotDirection.setText("North");
                break;
            case RIGHT:
                robotDirection.setText("East");
                break;
            case DOWN:
                robotDirection.setText("South");
                break;
            case LEFT:
                robotDirection.setText("West");
                break;
            default:
                break;
        }
    }

    public void incrementCoordinates(boolean x, boolean y) {
        if (x)
            this.x++;
        if (y)
            this.y--;
        setRobotCoordinates();
    }

    public void decrementCoordinates(boolean x, boolean y) {
        if (x)
            this.x--;
        if (y)
            this.y++;
        setRobotCoordinates();
    }

    public void setRobotCoordinates() {
        robotCoordinates.setText(String.format("( %d , %d )", x, y));
    }

    public void setRobotCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
        setRobotCoordinates();
    }

    public void setDebugWindow(String message){
        debugWindow.setText(message);
    }
}
package ntu.mdp.grp42.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.arena.ArenaCell;
import ntu.mdp.grp42.bluetooth.Constants;

public class LeftFragment extends Fragment implements Constants {
    private TextView robotStatus, robotDirection, robotCoordinates, debugWindow;
    private int x, y;
    private TableLayout resultTable;
    private boolean resultTableDrawn = false;
    private int[] resultList = new int[8];

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
        resultTable = view.findViewById(R.id.resultTable);

        resultTable.getViewTreeObserver().addOnPreDrawListener( () -> {
            if (!resultTableDrawn) {
                initResultTable(0);
                resultTableDrawn = true;
            }
            return true;
        });
    }

    public void initResultTable(int obstacleNum) {
        resultTable.removeAllViews();
        int btnSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        Drawable cellBG = AppCompatResources.getDrawable(this.requireContext(), R.drawable.cell_background);

        TableRow row = new TableRow(this.getContext());
        row.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        for (int x = 0; x < obstacleNum; x++) {
            ArenaCell arenaCell = new ArenaCell(this.getContext(), x, 0);
            arenaCell.setId(View.generateViewId());
            arenaCell.setPadding(1,1,1,1);
            arenaCell.setBackground(cellBG);
            arenaCell.setLayoutParams(new TableRow.LayoutParams(btnSize, btnSize));
            arenaCell.setTextColor(Color.rgb(255, 255, 255));
            resultList[x] = arenaCell.getId();
            row.addView(arenaCell);
        }
        resultTable.addView(row);
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
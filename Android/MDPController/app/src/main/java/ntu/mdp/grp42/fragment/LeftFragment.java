package ntu.mdp.grp42.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.arena.ArenaCell;
import ntu.mdp.grp42.arena.Obstacle;
import ntu.mdp.grp42.bluetooth.Constants;

public class LeftFragment extends Fragment implements Constants {
    private ImageView rpi, pc, stm;
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
        rpi = view.findViewById(R.id.rpi);
        pc = view.findViewById(R.id.computer);
        stm = view.findViewById(R.id.stm);

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
        if (obstacleNum > 8 || obstacleNum < 0)
            return;
        resultTable.removeAllViews();
        int btnSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        Drawable cellBG = AppCompatResources.getDrawable(this.requireContext(), R.drawable.cell_background);

        TableRow row = new TableRow(this.getContext());
        row.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        for (int x = 0; x < obstacleNum; x++) {
            // The original arena obstacle cell list might not be sorted in ascending order,
            // after moving the arena cells around or adding or removing. So we have to sort the list first
            Collections.sort(ArenaFragment.obstacleCells, new ObstacleIdComparator());

            // Query for the xth obstacle cell
            ArenaCell obstacleCell = ArenaFragment.obstacleCells.get(x);

            // We can't directly assign the original obstacle cell into the new results table,
            // so we'll need to create a copy of it instead
            ArenaCell arenaCell = new ArenaCell(this.getContext(), x, 0);
            arenaCell.setId(x);
            arenaCell.setPadding(1,1,1,1);
            arenaCell.setBackground(obstacleCell.getBackground());
            arenaCell.setLayoutParams(new TableRow.LayoutParams(btnSize, btnSize));
            arenaCell.setTextColor(Color.rgb(255, 255, 255));
//            arenaCell.setTextSize(obstacleCell.getTextSize());
            arenaCell.setText(obstacleCell.getText());
            int obstacleID = Integer.parseInt(arenaCell.getText().toString());
            if (obstacleID >= 11 && obstacleID <= 40) {
                arenaCell.setBackground(ResourcesCompat.getDrawable(getResources(), arenaCell.getImageID(obstacleID), null));
                arenaCell.setText(" ");
            }
            resultList[x] = arenaCell.getId();
            row.addView(arenaCell);
        }
        resultTable.addView(row);
    }

    public void updateResultTable(int obstacleID, int imageID) {
        ArenaCell arenaCell = resultTable.findViewById(obstacleID - 1);
        arenaCell.setText(" ");
        arenaCell.setBackground(ResourcesCompat.getDrawable(getResources(), arenaCell.getImageID(imageID), null));
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

    public int[] getRobotCoordinates() {
        int[] results = new int[2];
        results[0] = x;
        results[1] = y;
        return results;
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

    public void setRpiColor(int color) {
        switch (color) {
            case 0:
                rpi.setImageDrawable(AppCompatResources.getDrawable(this.requireContext(), R.drawable.raspberry_pi_red));
                break;
            case 2:
                rpi.setImageDrawable(AppCompatResources.getDrawable(this.requireContext(), R.drawable.raspberry_pi_yellow));
                break;
            case 3:
                rpi.setImageDrawable(AppCompatResources.getDrawable(this.requireContext(), R.drawable.raspberry_pi_green));
                break;
        }
    }

    public void setPcColor(int color) {
        switch (color) {
            case 0:
                pc.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                break;
            case 2:
                pc.setColorFilter(ContextCompat.getColor(getContext(), R.color.honey_yellow));
                break;
            case 3:
                pc.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
                break;
        }
    }

    public void setStmColor(int color) {
        switch (color) {
            case 0:
                stm.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                break;
            case 2:
                stm.setColorFilter(ContextCompat.getColor(getContext(), R.color.honey_yellow));
                break;
            case 3:
                stm.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
                break;
        }
    }

    public void setRedAll() {
        setRpiColor(0);
        setPcColor(0);
        setStmColor(0);
    }

    public void setYellowAll() {
        setRpiColor(2);
        setPcColor(2);
        setStmColor(2);
    }
}

// Comparator Class for sorting of obstacle cells in the status window
class ObstacleIdComparator implements Comparator<ArenaCell> {
    // override the compare() method
    public int compare(ArenaCell arenaCell1, ArenaCell arenaCell2)
    {
        if (arenaCell1.obstacleID == arenaCell2.obstacleID)
            return 0;
        else if (arenaCell1.obstacleID > arenaCell2.obstacleID)
            return 1;
        else
            return -1;
    }
}
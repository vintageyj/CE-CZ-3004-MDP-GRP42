package ntu.mdp.grp42.fragment;

import static android.view.DragEvent.ACTION_DRAG_ENTERED;
import static android.view.DragEvent.ACTION_DRAG_EXITED;
import static android.view.DragEvent.ACTION_DRAG_LOCATION;
import static android.view.DragEvent.ACTION_DROP;
import static ntu.mdp.grp42.bluetooth.Constants.*;
import static ntu.mdp.grp42.bluetooth.RaspberryPiProtocol.ADD_OBSTACLE;
import static ntu.mdp.grp42.bluetooth.RaspberryPiProtocol.REMOVE_OBSTACLE;
import static ntu.mdp.grp42.bluetooth.RaspberryPiProtocol.ROTATE_ROBOT;
import static ntu.mdp.grp42.bluetooth.RaspberryPiProtocol.SPAWN_ROBOT;

import android.content.ClipData;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.TaskActivity;
import ntu.mdp.grp42.arena.ArenaCell;
import ntu.mdp.grp42.arena.ArenaDragShadowBuilder;
import ntu.mdp.grp42.arena.Obstacle;
import ntu.mdp.grp42.bluetooth.BluetoothService;

public class ArenaFragment extends Fragment implements View.OnClickListener {

    private String ARENA_FRAGMENT_TAG = "ARENA FRAGMENT";

    // Arena Configs
    public static int[][] arenaCoord = new int[20][20];
    int x, y, btnHeight, btnWidth;
    boolean arenaDrawn = false;
    HashMap<Integer, Obstacle> obstacleList = new HashMap<>();
    HashMap<Integer, Obstacle> dummyObstacleList = new HashMap<>();

    final String[] directions = {"Up", "Right", "Down", "Left"};

    TableLayout arenaTable;
    ArrayList<ArrayList<ArenaCell>> arenaCellList = new ArrayList<ArrayList<ArenaCell>>();
    ArenaCell lastCell;
    private ImageView robotIV, arenaAxis, spawnBox;
    static RadioGroup spawnRG;
    Drawable arenaCellBG;

    // Status window
    LeftFragment leftStatusFragment;

    private BluetoothService bluetoothService;


    public ArenaFragment() {
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
        return inflater.inflate(R.layout.fragment_arena, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        arenaTable = view.findViewById(R.id.arenaTable);
        robotIV = view.findViewById(R.id.robotIV);
        //arenaAxis = view.findViewById(R.id.arenaAxis);
        spawnBox = view.findViewById(R.id.robotSpawnIV);

        arenaTable.getViewTreeObserver().addOnPreDrawListener( () -> {
            if (!arenaDrawn) {
                initArena(arenaTable);
                arenaDrawn = true;
            }
            return true;
        });

        robotIV.setOnClickListener(this);

        leftStatusFragment = (LeftFragment) getFragmentManager().findFragmentById(R.id.leftControlFragment);
    }

    protected void initArena(TableLayout arenaTable) {
        // Set the arena cells' height and width
        btnHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        btnWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());

        // default background for cell
        arenaCellBG = AppCompatResources.getDrawable(this.requireContext(), R.drawable.cell_background);

        // 20x20 map
        for (y = 0; y < 20; y++) {
            TableRow row = new TableRow(this.getContext());
            row.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            ArrayList<ArenaCell> arenaCells = new ArrayList<ArenaCell>();

            for (x = 0; x < 20; x++) {
                ArenaCell arenaCell = new ArenaCell(this.getContext(), x, y);
                arenaCell.setId(View.generateViewId());
                arenaCell.setPadding(1, 1, 1, 1);
                arenaCell.setBackground(arenaCellBG);
                arenaCell.setLayoutParams(new TableRow.LayoutParams(btnWidth, btnHeight));
                arenaCell.setTextColor(Color.rgb(255, 255, 255));

                arenaCoord[x][y] = arenaCell.getId();

                arenaCell.setOnClickListener(new ArenaCellClickListener(x, y, arenaCell.getId()));
                arenaCell.setOnDragListener(new ArenaCellDragListener(x, y, arenaCell.getId()));
                row.addView(arenaCell);
                arenaCells.add(arenaCell);
            }
            arenaTable.addView(row);
            arenaCellList.add(arenaCells);
        }
    }

    public void setBluetoothService(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.robotIV:
                rotateRobotRight();
                sendBTCommand(ROTATE_ROBOT,String.valueOf(robotIV.getRotation()));
                break;
        }
    }

    public void rotateRobotRight() {
        if (robotIV != null) {
            robotIV.setRotation((robotIV.getRotation() + 90) % 360);
            leftStatusFragment.setRobotDirection((int) robotIV.getRotation() / 90);
        }
        else
            setRobotWarning();
    }

    public void rotateRobotLeft() {
        if (robotIV != null) {
            robotIV.setRotation((robotIV.getRotation() + 270) % 360);
            leftStatusFragment.setRobotDirection((int) robotIV.getRotation() / 90);
        }
        else
            setRobotWarning();
    }

    public void forwardRobot() {
        if (robotIV != null) {
            switch ((int) robotIV.getRotation() % 360) {
                case 0:
                    robotIV.setY(robotIV.getY() - dpToPixels(25));
                    leftStatusFragment.incrementCoordinates(false, true);
                    break;
                case 90:
                    robotIV.setX(robotIV.getX() + dpToPixels(25));
                    leftStatusFragment.incrementCoordinates(true, false);
                    break;
                case 180:
                    robotIV.setY(robotIV.getY() + dpToPixels(25));
                    leftStatusFragment.decrementCoordinates(false, true);
                    break;
                case 270:
                    robotIV.setX(robotIV.getX() - dpToPixels(25));
                    leftStatusFragment.decrementCoordinates(true, false);
                    break;

            }
        } else {
            setRobotWarning();
        }
    }

    public void reverseRobot() {
        if (robotIV != null) {
            switch ((int) robotIV.getRotation() % 360) {
                case 0:
                    robotIV.setY(robotIV.getY() + dpToPixels(25));
                    leftStatusFragment.decrementCoordinates(false, true);
                    break;
                case 90:
                    robotIV.setX(robotIV.getX() - dpToPixels(25));
                    leftStatusFragment.decrementCoordinates(true, false);
                    break;
                case 180:
                    robotIV.setY(robotIV.getY() - dpToPixels(25));
                    leftStatusFragment.incrementCoordinates(false, true);
                    break;
                case 270:
                    robotIV.setX(robotIV.getX() + dpToPixels(25));
                    leftStatusFragment.incrementCoordinates(true, false);
                    break;

            }
        } else {
            setRobotWarning();
        }
    }

    private void setRobotWarning() {
        Toast.makeText(getContext(), "Set the robot onto the arena first!", Toast.LENGTH_SHORT).show();
    }



    public ArenaCell getLastCell() {
        return lastCell;
    }

    private class ArenaCellClickListener implements View.OnClickListener {
        int x, y, id;

        public ArenaCellClickListener(int x, int y, int id){
            this.x = x;
            this.y = y;
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            ArenaCell arenaCell = v.findViewById(id);
            Log.d(ARENA_FRAGMENT_TAG, "Clicked outer " + x + " " + y + " " + id);

            String spawnType = ArenaFragment.getSpawnType();
            Log.d(ARENA_FRAGMENT_TAG, "Spawning " + spawnType);

            if (arenaCell.getText().equals(" ") && spawnType.equals(getResources().getString(R.string.dummy_cell))){
                removeCell(arenaCell);
                return;
            }

            if (!arenaCell.getText().equals("")){
                Toast.makeText(getContext(), "Obstacle " + arenaCell.obstacleID +
                        " at " + x + ", " + y, Toast.LENGTH_SHORT).show();
                return;
            }

            if (spawnType.equals(getResources().getString(R.string.robot_cell))) {
                spawnRobot(arenaCell);
                String data = arenaCell.x + "," + arenaCell.y + ",North";
                sendBTCommand(SPAWN_ROBOT, data);

            }
            else {
                int totalObstacle = ArenaFragment.arenaCoord.length * ArenaFragment.arenaCoord[0].length;
                Log.d(ARENA_FRAGMENT_TAG, "Max obstacle capacity: " + totalObstacle);
                for (int obstacleID = 1; obstacleID <= totalObstacle; obstacleID++) {
                    if (!obstacleList.containsKey(obstacleID)) {
                        Log.d(ARENA_FRAGMENT_TAG, "Clicked " + x + " " + y);
                        if (spawnType.equals(getResources().getString(R.string.obstacle_cell)))
                            queryObstacleDirection(obstacleID, id, x, y);
                        else if (spawnType.equals(getResources().getString(R.string.dummy_cell)))
                            addObstacle(obstacleID, id, -1);
                        break;
                    }
                }
            }
        }
    }

    private void sendBTCommand(String command, String data) {
        String message = command + " " + data;
        bluetoothService.write(message.getBytes(StandardCharsets.UTF_8));
//        switch(command) {
//            case SPAWN_ROBOT:
//                message = SPAWN_ROBOT + "," + data;
//                bluetoothService.write(message.getBytes(StandardCharsets.UTF_8));
//                break;
//            case ROTATE_ROBOT:
//                message = ROTATE_ROBOT + "," + data;
//                bluetoothService.write(message.getBytes(StandardCharsets.UTF_8));
//            case ADD_OBSTACLE:
//                break;
//            case REMOVE_OBSTACLE:
//                break;
//        }
    }

    private void spawnRobot(ArenaCell arenaCell) {
        int[] cellPos = new int[2];
        arenaCell.getLocationInWindow(cellPos);
        robotIV.setVisibility(View.VISIBLE);
        robotIV.setX(arenaCell.getX() - dpToPixels(25));
        robotIV.setY(cellPos[1] -
//                dpToPixels(24) -
                dpToPixels(50));
        robotIV.setRotation((0 * 90) % 360);

        spawnBox.setVisibility(View.VISIBLE);
        spawnBox.setX(arenaCell.getX() - dpToPixels(25));
        spawnBox.setY(cellPos[1] - dpToPixels(31) - dpToPixels(50));

        leftStatusFragment.setRobotCoordinates(arenaCell.x, arenaCell.y);
    }

    private int dpToPixels(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void queryObstacleDirection(int obstacleID, int cellID, int x, int y) {
        final String[] selectedDirection = {"Up"};

//        AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext())
//                .setTitle("Choose the direction the image is facing")
//                .setSingleChoiceItems(directions, 0, (dialog, which) -> selectedDirection[0] = directions[which])
//
//                // Specifying a listener allows you to take an action before dismissing the dialog.
//                // The dialog is automatically dismissed when a dialog button is clicked.
//                .setPositiveButton("Confirm", (dialog, which) -> {
//                    int direction = Arrays.asList(directions).indexOf(selectedDirection[0]);
//                    addObstacle(obstacleID, cellID, direction);
//                    dialog.dismiss();
//                })
//
//                // A null listener allows the button to dismiss the dialog and take no further action.
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
//                .setIcon(android.R.drawable.ic_dialog_alert);
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this.requireContext());
        builder.setTitle("Choose direction of image"+"("+x+","+y+")");
        builder.setSingleChoiceItems(directions, 0, (dialogInterface, i) -> selectedDirection[0] = directions[i]);

        // confirm to add obstacle
        builder.setPositiveButton("Confirm", (dialogInterface, i) -> {
            int dir = Arrays.asList(directions).indexOf(selectedDirection[0]);
            addObstacle(obstacleID, cellID, dir);
            dialogInterface.dismiss();
        });

        // exit process
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    private void addObstacle(int obstacleID, int cellID, int direction) {
        if (obstacleList.containsKey(obstacleID))
            return;

        int facingID;
        switch (direction) {
            case UP:
                facingID = R.drawable.north_facing_obstacle;
                break;
            case RIGHT:
                facingID = R.drawable.east_facing_obstacle;
                break;
            case DOWN:
                facingID = R.drawable.south_facing_obstacle;
                break;
            case LEFT:
                facingID = R.drawable.west_facing_obstacle;
                break;
            default:
                facingID = R.drawable.dummy_obstacle;
                break;
        }
        Log.d(ARENA_FRAGMENT_TAG, "Facing ID: " + facingID);


        ArenaCell arenaCell = arenaTable.findViewById(cellID);
        if (direction >= UP && direction <= LEFT)
            arenaCell.setText(String.valueOf(obstacleID));
        else
            arenaCell.setText(" ");
        arenaCell.obstacleID = obstacleID;

        // keeps track of obstacle in memory
        Obstacle obstacle = new Obstacle(obstacleID, cellID, arenaCell.x, arenaCell.y, direction);
        obstacleList.put(obstacleID, obstacle);
        dummyObstacleList.put(obstacleID, obstacle);
//
        // sends addition of obstacle over to robot
        sendBTCommand(ADD_OBSTACLE, obstacle.getObstacleData());
        Gson gson = new Gson();
        sendBTCommand(ADD_OBSTACLE, gson.toJson(obstacle));

        // draws direction of image onto cell
        Drawable cellFace = AppCompatResources.getDrawable(this.requireContext(), facingID);
        arenaCell.setBackground(cellFace);

        arenaCell.setOnLongClickListener(v -> {
            TaskActivity.vibrator.vibrate(100);
            try {
                // we can to keep track of:
                // original button id, obstacle id and direction of image
                JSONObject json = new JSONObject();
                json.put("obstacleID", obstacleID);
                json.put("direction", direction);

                // store data in ClipItem which stays until drag is stopped
                ClipData.Item item = new ClipData.Item(json.toString());
                ClipData dragData = new ClipData(
                        "dragObstacle",
                        new String[]{},
                        item
                );

                // shadow will just be the button itself
                View.DragShadowBuilder shadow = new ArenaDragShadowBuilder(arenaCell);
                v.startDrag(dragData, shadow, arenaCell, 0);

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return true;
        });

    }

    private class ArenaCellDragListener implements View.OnDragListener {
        int x, y, id;

        public ArenaCellDragListener(int x, int y, int id) {
            this.x = x;
            this.y = y;
            this.id = id;
        }

        @Override
        public boolean onDrag(View newCell, DragEvent event) {
            ArenaCell originalCell;
            ArenaCell newArenaCell = newCell.findViewById(newCell.getId());
            switch (event.getAction()) {
                case ACTION_DRAG_ENTERED:
                    // touch feedback for starting drag or entering a new cell
                    TaskActivity.vibrator.vibrate(100);

                    // removes all the highlights from last adjacent row and column
                    if (lastCell != null && lastCell != newArenaCell)
                        removeAdjacentCellHighlights(lastCell);
                    // highlights all the adjacent row and column
                    highlightAdjacentCells(newArenaCell);
                    lastCell = newArenaCell;
                case ACTION_DRAG_EXITED:
                    return true;
                case ACTION_DROP:
                    try {
                        // get drag data
                        ClipData dragData = event.getClipData();
                        JSONObject json = new JSONObject(dragData.getItemAt(0).getText().toString());

                        originalCell = (ArenaCell) event.getLocalState();

                        // stops if dropped cell is same as original cell
                        if (originalCell.getId() == newCell.getId()) {
                            // removes all the highlights
                            removeAdjacentCellHighlights(newArenaCell);
                            return true;
                        }

                        // stops if dropped cell is on a populated cell
                        newArenaCell = newCell.findViewById(newCell.getId());
                        if (newArenaCell.obstacleID != -1) {
                            Toast.makeText(getContext(), "Remove Obstacle " + newArenaCell.obstacleID +
                                    " first!", Toast.LENGTH_SHORT).show();

                            // removes all the highlights
                            removeAdjacentCellHighlights(newArenaCell);
                            return true;
                        }

                        // removes original cell
                        removeCell(originalCell);

                        // moves obstacle over to new cell
                        int obstacleID = json.getInt("obstacleID");
                        int selectedDirection = json.getInt("direction");
                        addObstacle(obstacleID, newCell.getId(), selectedDirection);

                        // removes all the highlights
                        removeAdjacentCellHighlights(newArenaCell);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    return true;
            }
            return true;
        }
    }

    public void removeCell(ArenaCell arenaCell) {
        int obstacleID = arenaCell.obstacleID;

        Obstacle obstacle = obstacleList.get(obstacleID);
        sendBTCommand(REMOVE_OBSTACLE, obstacle.getObstacleData());

        obstacleList.remove(obstacleID);
        arenaCell.setText("");
        arenaCell.obstacleID = -1;
        arenaCell.setBackground(arenaCellBG);
    }

    public void updateCellImage(int cellID, int imageID) {
        Log.d(ARENA_FRAGMENT_TAG, "Cell ID: " + cellID + " Image ID: " + imageID);
        ArenaCell arenaCell = arenaTable.findViewById(cellID);
        arenaCell.setText(" ");
        arenaCell.setBackground(ResourcesCompat.getDrawable(getResources(), arenaCell.getImageID(imageID), null));
    }

    public void highlightAdjacentCells(ArenaCell arenaCell) {
        int x = arenaCell.x;
        int y = arenaCell.y;
        ArenaCell cellX, cellY;
        for (int i = 0; i < 20; i++) {
            cellX = arenaCellList.get(i).get(x);
            cellY = arenaCellList.get(y).get(i);
            if (cellX.getText().equals("")) {
                cellX.setBackground(AppCompatResources.getDrawable(this.requireContext(), R.drawable.cell_highlighted_background));
            }

            if (cellY.getText().equals("")) {
                cellY.setBackground(AppCompatResources.getDrawable(this.requireContext(), R.drawable.cell_highlighted_background));
            }
        }
    }

    public void removeAdjacentCellHighlights(ArenaCell arenaCell) {
        int x = arenaCell.x;
        int y = arenaCell.y;
        ArenaCell cellX, cellY;
        for (int i = 0; i < 20; i++) {
            cellX = arenaCellList.get(i).get(x);
            cellY = arenaCellList.get(y).get(i);
            if (cellX.getText().equals("")) {
                cellX.setBackground(AppCompatResources.getDrawable(this.requireContext(), R.drawable.cell_background));
            }

            if (cellY.getText().equals("")) {
                cellY.setBackground(AppCompatResources.getDrawable(this.requireContext(), R.drawable.cell_background));
            }
        }
    }


    public static String getSpawnType(){
        RadioButton radioBtn = spawnRG.findViewById(spawnRG.getCheckedRadioButtonId());
        return radioBtn.getText().toString();
    }

    public void setSpawnRG(RadioGroup spawnRG) {
        this.spawnRG = spawnRG;
    }
}
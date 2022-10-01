package ntu.mdp.grp42.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.button.MaterialButtonToggleGroup;

import ntu.mdp.grp42.TaskActivity;
import ntu.mdp.grp42.bluetooth.BluetoothActivity;
import ntu.mdp.grp42.R;

public class RightControlFragment extends Fragment
        implements View.OnClickListener, MaterialButtonToggleGroup.OnButtonCheckedListener {

    Button bluetoothBtn, setArenaButton, resetArenaButton, videoButton;
    private ImageView videoTab;
    private ArenaFragment arenaFragment;
    private VideoFragment videoFragment;
    public ProgressBar spinner;

    private LinearLayout arenaSettingsLayout;
    private MaterialButtonToggleGroup pathToggleGroup, spawnToggleGroup;

    private boolean videoToggled = false;

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
        pathToggleGroup = view.findViewById(R.id.pathToggleBtnGrp);
        pathToggleGroup.addOnButtonCheckedListener(this);
        spawnToggleGroup = view.findViewById(R.id.spawnToggleBtnGrp);
        spawnToggleGroup.addOnButtonCheckedListener(this);

        bluetoothBtn = view.findViewById(R.id.bluetoothBtn);
        bluetoothBtn.setOnClickListener(this);
        spinner = (ProgressBar) view.findViewById(R.id.progressBarBT);
        spinner.setVisibility(View.INVISIBLE);
        setArenaButton = view.findViewById(R.id.setArenaButton);
        resetArenaButton = view.findViewById(R.id.resetArenaButton);
        videoButton = view.findViewById(R.id.videoButton);
        videoTab = view.findViewById(R.id.videoTab);
        setArenaButton.setOnClickListener(this);
        resetArenaButton.setOnClickListener(this);
        videoButton.setOnClickListener(this);

        arenaSettingsLayout = view.findViewById(R.id.arenaSettingsLayout);
//        arenaSettingsLayout.setVisibility(View.VISIBLE);
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
            case R.id.videoButton:
                if (!videoToggled) {
                    TaskActivity.swapFragments(1);
                    videoTab.setImageDrawable(getContext().getDrawable(R.drawable.ic_tab_unselected));
                    videoToggled = true;
                } else {
                    TaskActivity.swapFragments(0);
                    videoTab.setImageDrawable(getContext().getDrawable(R.drawable.ic_tab));
                    videoToggled = false;
                }
                break;
            case R.id.setArenaButton:
                arenaFragment.setCustomArena();
                break;

            case R.id.resetArenaButton:
                arenaFragment.resetArena();
                break;
        }
    }

    public Button getBluetoothBtn() {
        return bluetoothBtn;
    }

    @Override
    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        switch (group.getId()) {
            case R.id.pathToggleBtnGrp:
                switch (checkedId) {
                    case R.id.predictedPathBtn:
                        if (isChecked) {
                            arenaFragment.showPredictedCells();
                            arenaFragment.predictedPathShown = true;
                        } else {
                            arenaFragment.hidePredictedCells();
                            arenaFragment.predictedPathShown = false;
                        }
                        break;
                    case R.id.takenPathBtn:
                        if (isChecked) {
                            arenaFragment.showTakenCells();
                            arenaFragment.showTakenPath = true;
                        } else {
                            arenaFragment.hideTakenCells();
                            arenaFragment.showTakenPath = false;
                        }
                        break;
                }
                break;

            case R.id.spawnToggleBtnGrp:
                switch (checkedId) {
                    case R.id.spawnRobotBtn:
                        if (isChecked) {
                            arenaFragment.spawnType = getResources().getString(R.string.robot_cell);
                        } else {
                            arenaFragment.spawnType = "";
                        }
                        break;
                    case R.id.spawnObstacleBtn:
                        if (isChecked) {
                            arenaFragment.spawnType = getResources().getString(R.string.obstacle_cell);
                        } else {
                            if (arenaFragment.spawnType.equals(getResources().getString(R.string.obstacle_cell)))
                                arenaFragment.spawnType = "";
                        }
                        break;
                    case R.id.spawnDummyObstacleBtn:
                        if (isChecked) {
                            arenaFragment.spawnType = getResources().getString(R.string.dummy_cell);
                        } else {
                            if (arenaFragment.spawnType.equals(getResources().getString(R.string.dummy_cell)))
                                arenaFragment.spawnType = "";
                        }
                        break;
                }
                break;
        }
    }

//    public void setArenaSettingsVisibility(int visibility) {
//        if (visibility == View.VISIBLE) {
//            // Prepare the View for the animation
//            arenaSettingsLayout.setVisibility(View.VISIBLE);
//            arenaSettingsLayout.setAlpha(1.0f);
//
//            // Start the animation
//            arenaSettingsLayout.setY(-arenaSettingsLayout.getHeight());
//            arenaSettingsLayout.animate()
//                    .translationY(0)
//                    .alpha(1.0f)
//                    .setDuration(500)
//                    .setListener(null);
//        } else {
//            arenaSettingsLayout.animate()
//                    .translationY(0)
//                    .alpha(0.0f)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            arenaSettingsLayout.setVisibility(View.INVISIBLE);
//                        }
//                    });
//        }
//    }
}
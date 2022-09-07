package ntu.mdp.grp42.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;

import android.widget.Button;
import android.widget.TextView;


import ntu.mdp.grp42.R;

public class StartTaskFragment extends Fragment {

    private static Button BTNtask1, BTNtask2;
    public View v;

    public StartTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_start_task, container, false);

        BTNtask1 =  (Button)v.findViewById(R.id.task1);
        BTNtask2 =  (Button)v.findViewById(R.id.task2);

        return v;
    }
    public static Button getBTNtask1() {
        return BTNtask1;
    }

    public static Button getBTNtask2() {
        return BTNtask2;
    }

    public static void resetTaskBTN() {
        BTNtask1.setEnabled(true);
        BTNtask2.setEnabled(true);
    }

}
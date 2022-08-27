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

    // Is the stopwatch running?
    private boolean running;

    private boolean wasRunning;

    public View v;

    private TextView tempTextView; //Temporary TextView
    private Button tempBtn; //Temporary Button
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime;
    private final int REFRESH_RATE = 100;
    private String hours,minutes,seconds,milliseconds;
    private long secs,mins,hrs,msecs;
    private boolean stopped = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StartTaskFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_start_task, container, false);
        Button BTNstop =  (Button)v.findViewById(R.id.stop_buttonn);
        Button BTNreset =  (Button)v.findViewById(R.id.reset_buttonn);
        TextView timerview = (TextView)v.findViewById(R.id.timer);

        Button BTNtask1 =  (Button)v.findViewById(R.id.task1);
        Button BTNtask2 =  (Button)v.findViewById(R.id.task2);

        View.OnClickListener task1Handle = new View.OnClickListener() {

            public void onClick(View v) {
                task1effect();
                // TODO Auto-generated method stub
                if(stopped){
                    startTime = System.currentTimeMillis() - elapsedTime;
                }
                else{
                    startTime = System.currentTimeMillis();
                }
                mHandler.removeCallbacks(startTimer);
                mHandler.postDelayed(startTimer, 0);
            }
        };
        BTNtask1.setOnClickListener(task1Handle);

        View.OnClickListener task2Handle = new View.OnClickListener() {

            public void onClick(View v) {
                task2effect();
                // TODO Auto-generated method stub
                if(stopped){
                    startTime = System.currentTimeMillis() - elapsedTime;
                }
                else{
                    startTime = System.currentTimeMillis();
                }
                mHandler.removeCallbacks(startTimer);
                mHandler.postDelayed(startTimer, 0);
            }
        };
        BTNtask2.setOnClickListener(task2Handle);

        View.OnClickListener stopHandle = new View.OnClickListener() {

            public void onClick(View v) {
                stopBTNeffect();

                mHandler.removeCallbacks(startTimer);
                stopped = true;

            }
        };
        BTNstop.setOnClickListener(stopHandle);

        View.OnClickListener resetHandle = new View.OnClickListener() {

            public void onClick(View v) {
                resetBTNeffect();
                stopped = false;
                timerview.setText("00:00:00");
                //BTNstart.setText("START");
            }
        };
        BTNreset.setOnClickListener(resetHandle);

        return v;
    }

    private void task1effect() {
        ((Button)v.findViewById(R.id.task2)).setEnabled(false);
        ((Button)v.findViewById(R.id.stop_buttonn)).setEnabled(true);
    }

    private void task2effect() {
        ((Button)v.findViewById(R.id.task1)).setEnabled(false);
        ((Button)v.findViewById(R.id.stop_buttonn)).setEnabled(true);
    }

    private void stopBTNeffect() {

        ((Button)v.findViewById(R.id.reset_buttonn)).setEnabled(true);
    }

    private void resetBTNeffect(){
        ((Button)v.findViewById(R.id.task1)).setEnabled(true);
        ((Button)v.findViewById(R.id.task2)).setEnabled(true);
        ((Button)v.findViewById(R.id.stop_buttonn)).setEnabled(false);

    }


    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this,REFRESH_RATE);

        }
    };

    private void updateTimer (float time){
        View v = getView();
        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);

        /* Convert the seconds to String
         * and format to ensure it has
         * a leading zero when required
         */
        secs = secs % 60;
        seconds=String.valueOf(secs);
        if(secs == 0){
            seconds = "00";
        }
        if(secs <10 && secs > 0){
            seconds = "0"+seconds;
        }

        /* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes=String.valueOf(mins);
        if(mins == 0){
            minutes = "00";
        }
        if(mins <10 && mins > 0){
            minutes = "0"+minutes;
        }

        /* Convert the hours to String and format the String */

        hours=String.valueOf(hrs);
        if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }

        /* Although we are not using milliseconds on the timer in this example
         * I included the code in the event that you wanted to include it on your own
         */
        milliseconds = String.valueOf((long)time);
        if(milliseconds.length()==2){
            milliseconds = "0"+milliseconds;
        }
        if(milliseconds.length()<=1){
            milliseconds = "00";
        }
        milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-2);

        /* Setting the timer text to the elapsed time */
        ((TextView)v.findViewById(R.id.timer)).setText( minutes + ":" + seconds+"." + milliseconds);
        //((TextView)v.findViewById(R.id.timerMs)).setText("." + milliseconds);


    }//end Update Timer



}
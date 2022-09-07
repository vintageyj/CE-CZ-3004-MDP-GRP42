package ntu.mdp.grp42.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ntu.mdp.grp42.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimerFragment extends Fragment {

    private static Button BTNstop, BTNreset;
    private static View v;
    private static TextView timerview;
    private static Handler mHandler = new Handler();
    private static boolean stopped = false;
    private static long startTime;
    private static long elapsedTime;

    private static final int REFRESH_RATE = 100;
    private static String hours,minutes,seconds,milliseconds;
    private static long secs,mins,hrs,msecs;

    public TimerFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TimerFragment newInstance(String param1, String param2) {
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static Button getBTNstop() {
        return BTNstop;
    }

    public static Button getBTNreset(){
        return BTNreset;
    }

    public static boolean getStopStatus(){
        return stopped;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_timer, container, false);
        BTNstop =  (Button)v.findViewById(R.id.stop_button);
        BTNreset = (Button)v.findViewById(R.id.reset_button);


        View.OnClickListener resetHandle = new View.OnClickListener() {

            public void onClick(View v) {
                resetBTNeffect();
                stopped = false;
                resetTimer();
            }
        };
        BTNreset.setOnClickListener(resetHandle);
        return v;
    }

    public static void stopBTNeffect() {
        mHandler.removeCallbacks(startTimer);
        stopped = true;

    }

    public static void resetBTNeffect(){
        stopped = false;
        resetTimer();
    }

    public static void resetTimer(){

        ((TextView)v.findViewById(R.id.timer)).setText("00:00:00");
        secs = 0;
        mins = 0;
    }

    public static void taskEffect(){
        BTNstop.setEnabled(true);
        if(stopped){
            resetTimer();
            startTime = System.currentTimeMillis() - elapsedTime;
        }
        else{
            resetTimer();
            startTime = System.currentTimeMillis();
        }
        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
    }

    private static Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this,REFRESH_RATE);
        }
    };

    private static void updateTimer (float time){
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
    }//end Update Timer
}
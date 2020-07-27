package com.example.tamarind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class TimeUpActivity extends AppCompatActivity {

    TextView hr_display, h, min_display, min;
    TextView incrementBy5min;
    Button bottom_left, bottom_right;
    TextView topic_Selected_text;

    Boolean break_state;
    String topic_selected;
    long time_recorded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_up);

        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //Initialising
        hr_display = findViewById(R.id.hr_display);
        h = findViewById(R.id.h);
        min_display = findViewById(R.id.min_display);
        min = findViewById(R.id.m);
        incrementBy5min = findViewById(R.id.imcrementBy5min);
        bottom_left = findViewById(R.id.bottom_left);
        bottom_right = findViewById(R.id.bottom_right);
        topic_Selected_text = findViewById(R.id.topic_display);

        //retrieving data from intent
        Intent i = getIntent();

        topic_selected = i.getStringExtra("topicSelected");
        break_state = i.getBooleanExtra("isBreak", false);
        if(break_state) {
            time_recorded = MainActivity.breakSeconds;
            topic_Selected_text.setText("Break");
            bottom_right.setText("Reset");
        }else {
            time_recorded = MainActivity.seconds;
            topic_Selected_text.setText(topic_selected);
            bottom_right.setText("Save");
        }
        time_recorded = time_recorded + getIncrementedTimeByMin() * 60;
        int hrs = (int) ((time_recorded % 86400 ) / 3600);
        int mins = (int) (((time_recorded % 86400 ) % 3600 ) / 60);
        int sec = (int) (time_recorded % 60);

        hr_display.setText(String.valueOf(hrs));
        min_display.setText(String.valueOf(mins));

        if(hrs == 0) {
            hr_display.setVisibility(View.GONE);
            h.setVisibility(View.GONE);
        } else {
            hr_display.setVisibility(View.VISIBLE);
            h.setVisibility(View.VISIBLE);
        }

        if(break_state) {
            //time should be invisible
            hr_display.setVisibility(View.GONE);
            h.setVisibility(View.GONE);

            min_display.setVisibility(View.GONE);
            min.setVisibility(View.GONE);
        }

        bottom_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottom_right.getText().equals("Save")) {
                    saveTime();
                } else if(bottom_right.getText().equals("Reset")) {
                    resetTime();
                }

                MainActivity.topRight_option.setText("Set Timer");
                MainActivity.topLeft_option.setVisibility(View.INVISIBLE);
                MainActivity.incrementBy1min.setVisibility(View.INVISIBLE);

                MainActivity.incrementedTimeInMin = 0;
                storeincrementTedTimeByMin((long) 0);
                finish();
            }
        });

        bottom_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel
                MainActivity.topRight_option.setText("Set Timer");
                MainActivity.topLeft_option.setVisibility(View.INVISIBLE);
                MainActivity.incrementBy1min.setVisibility(View.INVISIBLE);

                MainActivity.incrementedTimeInMin = 0;
                storeincrementTedTimeByMin((long) 0);
                finish();
            }
        });
    }

    private void resetTime() {
        MainActivity.breakSeconds = TimeSetter.breakSeconds;
        MainActivity.timerTextSet(MainActivity.breakSeconds);

        MainActivity.topic_btn.setText("Break");

        MainActivity.isBreak = true;

        storeBreakState(true);
        storeMillisLeftInSharedPrefs(TimeSetter.breakSeconds);
        storeTimePassed(0);
    }

    private void saveTime() {
        if(!break_state){
            Log.i("timeRecorded", topic_Selected_text + ": " + String.valueOf(time_recorded));

            Toast.makeText(this, String.valueOf(time_recorded), Toast.LENGTH_LONG).show();
            MainActivity.totalSeconds_passed = 0;
            storeTimePassed(MainActivity.totalSeconds_passed);

            MainActivity.seconds = TimeSetter.seconds;
            MainActivity.breakSeconds = TimeSetter.seconds;

            MainActivity.timerTextSet(MainActivity.seconds);

        }else{
            MainActivity.seconds = TimeSetter.seconds;
            MainActivity.timerTextSet(MainActivity.seconds);
        }

        storeBreakState(break_state);
        storeMillisLeftInSharedPrefs(0);
        storeTimerState(0);
    }

    private void storeTimerState(int i) {
        SharedPreferences sharedPreferences = getSharedPreferences("timerState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("timerState", 0);
        editor.apply();
    }

    private void storeMillisLeftInSharedPrefs(int i) {
        SharedPreferences sharedPreferences = getSharedPreferences("millisLeft", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("millisLeft", MainActivity.seconds);
        editor.apply();
    }

    private void storeBreakState(Boolean break_state) {
        SharedPreferences sharedPreferences = getSharedPreferences("breakState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isBreak", break_state);
        editor.apply();
    }

    private void storeTimePassed(long totalSeconds_passed) {
        SharedPreferences sharedPreferences = getSharedPreferences("totalTimePassed", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("timePassed", totalSeconds_passed);
        editor.apply();
    }

    private long getTimepassed() {
        SharedPreferences sharedPreferences = getSharedPreferences("totalTimePassed", MODE_PRIVATE);

        long timePassed = sharedPreferences.getLong("timePassed", 0);
        return timePassed;
    }

    private Long getIncrementedTimeByMin() {
        SharedPreferences sharedPreferences = getSharedPreferences("incrementedTimeByMin", MODE_PRIVATE);
        Long time = sharedPreferences.getLong("incrementedTime", 0);

        return time;
    }

    private void storeincrementTedTimeByMin(Long incrementedTimeInMin) {
        SharedPreferences sharedPreferences = getSharedPreferences("incrementedTimeByMin", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("incrementedTime", incrementedTimeInMin);
        editor.apply();
    }

}
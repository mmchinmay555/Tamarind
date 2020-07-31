package com.example.tamarind;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class TimeUpActivity extends AppCompatActivity {

    TextView hr_display, h, min_display, min;
    Button bottom_left, bottom_right;
    TextView topic_Selected_text;

    Boolean break_state;
    String topic_selected;
    long time_recorded;
    static Boolean is_canceled, is_saved, is_reset, is_incrementedBy5min;

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
        bottom_left = findViewById(R.id.bottom_left);
        bottom_right = findViewById(R.id.bottom_right);
        topic_Selected_text = findViewById(R.id.topic_display);

        //retrieving data from intent
        Intent i = getIntent();

        topic_selected = getTopic_selected();
        break_state = PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).getBoolean("breakState",
                false);
        Log.i("getBreakStateTimeUp", String.valueOf(PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).getBoolean("breakState",
                false)));
        Log.i("break_stateTimeUp", String.valueOf(break_state));
        if(break_state) {
            time_recorded = getBreakSeconds();
            topic_Selected_text.setText("Break");
            bottom_right.setText("Reset");
        }else {
            time_recorded = getSeconds();
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
        }else {
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
                storeincrementTedTimeByMin((long) 0);
                storeTimerState(0);

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(1);

                win.closeAllPanels();
                startActivity(new Intent(TimeUpActivity.this, MainActivity.class));
                finish();
            }
        });

        bottom_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel
                is_canceled = true;
                store_is_canceled(is_canceled);
                store_is_reset(false);
                store_is_saved(false);

                storeTimePassed(0);
                storeBreakState(false);
                storeincrementTedTimeByMin((long) 0);
                storeTimerState(0);

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(1);

                win.closeAllPanels();

                startActivity(new Intent(TimeUpActivity.this, MainActivity.class));
                finish();
            }
        });

    }


    private void resetTime() {
        is_reset = true;
        store_is_reset(is_reset);
        store_is_saved(false);
        store_is_canceled(false);

        storeBreakState(true);
        storeMillisLeftInSharedPrefs(0);
    }

    private void saveTime() {
        is_saved = true;
        store_is_saved(is_saved);
        store_is_canceled(false);
        store_is_reset(false);

        Log.i("timeRecorded", topic_Selected_text + ": " + String.valueOf(time_recorded));
        Toast.makeText(this, String.valueOf(time_recorded), Toast.LENGTH_LONG).show();

        storeBreakState(break_state);
        storeMillisLeftInSharedPrefs(0);
        storeTimerState(0);
    }

    private void storeTimerState(long i) {
        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putLong("timerState",
               i).commit();
    }

    private void storeMillisLeftInSharedPrefs(long i) {
        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putLong("millisLeft",
                i).apply();
    }

    private void storeBreakState(Boolean break_state) {
        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putBoolean("breakState",
                break_state).commit();
    }

    private void storeTimePassed(long totalSeconds_passed) {
        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putLong("totalTimePassed",
                totalSeconds_passed).commit();
    }

    private Long getIncrementedTimeByMin() {
        Long time = PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).getLong("incrementedTimeByMin",
                0);
        return time;
    }

    private void storeincrementTedTimeByMin(Long incrementedTimeInMin) {
        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putLong("incrementedTimeByMin",
                incrementedTimeInMin).commit();
    }

    private int getBreakSeconds() {
        int time = PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).getInt("breakSeconds",
               4 * 60);
        return time;
    }

    private int getSeconds() {
       int timePassed = PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).getInt("seconds",
                50 * 60);
        return timePassed;
    }

    private void store_is_saved(Boolean is_saved) {
        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putBoolean("is_saved",
                is_saved).commit();
    }

    private void store_is_reset(Boolean is_reset) {
        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putBoolean("is_reset",
                is_reset).commit();
    }

    private void store_is_canceled(Boolean is_canceled) {
        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putBoolean("is_canceled",
                is_canceled).commit();
    }

    private String getTopic_selected() {
        return PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).getString("sharedPrefsTopicSelected", "null");
    }
}
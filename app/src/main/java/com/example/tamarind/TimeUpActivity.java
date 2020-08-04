package com.example.tamarind;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TimeUpActivity extends AppCompatActivity {

    TextView hr_display, h, min_display, min;
    Button bottom_left, bottom_right;
    TextView topic_Selected_text;

    Boolean break_state;
    String topic_selected;
    long time_recorded;
    static Boolean is_canceled, is_saved, is_reset;
    DateFormat dateFormat;
    Calendar first_data;
    ArrayList<topic_item> topic_items;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        dateFormat = new SimpleDateFormat("EEE , dd MMM");

        load_topic_items();
        first_data = Calendar.getInstance();

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
            @RequiresApi(api = Build.VERSION_CODES.N)
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveTime() {
        is_saved = true;
        store_is_saved(is_saved);
        store_is_canceled(false);
        store_is_reset(false);

        Log.i("timeRecorded", topic_Selected_text + ": " + String.valueOf(time_recorded));
       // topic_item item = new topic_item(topic_selected, time_recorded, date , Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        Toast.makeText(this, String.valueOf(time_recorded), Toast.LENGTH_LONG).show();

        store_time_recorded(topic_selected, time_recorded);

        storeBreakState(break_state);
        storeMillisLeftInSharedPrefs(0);
        storeTimerState(0);
        storeTimePassed(0);
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

    private void save_topic_items() {
        Gson gson = new Gson();
        String json = gson.toJson(topic_items);

        PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putString("topic_items",
                json).commit();
    }

    private void load_topic_items() {
        Gson gson = new Gson();
        String json =  PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).getString("topic_items",
                null);

        Type type = new TypeToken<ArrayList<topic_item>>(){}.getType();
        topic_items = gson.fromJson(json, type);

        if (topic_items == null) {
            topic_items = new ArrayList<topic_item>();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void store_time_recorded(String topicSelected, long totalSeconds_passed) {
        load_topic_items();
        //record time
        Calendar calendar = Calendar.getInstance();

        String date = dateFormat.format(calendar.getTime());
        topic_item topic_item = new topic_item(topicSelected, (totalSeconds_passed / 60), date , calendar.get(Calendar.DAY_OF_WEEK));

        Log.i("topicItemsSize", String.valueOf(topic_items.size()));
        if(topic_items.size() == 0) {
            first_data.setTime(calendar.getTime());
            first_data.add(Calendar.DAY_OF_MONTH, -1 * (first_data.get(Calendar.DAY_OF_WEEK) - 1));
            PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).edit().putLong("first_data_insterted_time",
                    first_data.getTimeInMillis()).commit();

            Log.i("firstTime", "added");
            Log.i("dateRecoded", String.valueOf(PreferenceManager.getDefaultSharedPreferences(TimeUpActivity.this).getLong("first_data_insterted_time",
                    0)));
        }

        Boolean found_item = false;
        for(int i = 0; i < topic_items.size(); i++) {
            if (topic_items.get(i).topic_name.equals(topic_item.topic_name) && topic_items.get(i).date_recorded.equals(topic_item.date_recorded)) {
                topic_items.get(i).time_recorded += totalSeconds_passed;
                found_item = true;

                Log.i("updatedItem", topic_items.get(i).topic_name + "\n" + topic_items.get(i).date_recorded + "\n" + topic_items.get(i).day_recorded + "\n" + topic_items.get(i).time_recorded);
            }
        }

        if(!found_item) {
            topic_items.add(topic_item);
            Log.i("recordedItem", topic_item.topic_name + "\n" + topic_item.date_recorded + "\n" + topic_item.day_recorded + "\n" + topic_item.time_recorded);
        }

        //store in preferences
        save_topic_items();
        load_topic_items();
        //update_currentView_list(Calendar.getInstance());
    }
}
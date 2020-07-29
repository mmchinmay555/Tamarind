package com.example.tamarind;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.media.TimedText;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.sql.Time;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    ImageView menu_btn, more_btn, back_btn;

    public long timerRunning;
    static public long totalSeconds_passed;

    static String topicSelected;

    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout, appbar;
    LinearLayout total_time, bottom_layout;

    static TextView topLeft_option, topRight_option;
    static TextView incrementBy1min;
    LinearLayout timer;

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;

    static TextView topic_btn;
    static TextView timerHr, timerMin, timerSec;
    static TextView colonM, colonS;

    static Boolean isBreak;

    CountDownTimer countDownTimer = null;
    static long seconds;
    static long breakSeconds;
    static ArrayList<topic_item> topics;
    static long incrementedTimeInMin;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View bottomSheet = findViewById(R.id.bottomSheet);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        menu_btn = findViewById(R.id.menu_button);
        more_btn = findViewById(R.id.more_btn);
        coordinatorLayout = findViewById(R.id.app_bar);
        appbar = findViewById(R.id.topbar);
        total_time = findViewById(R.id.time_display);
        back_btn = findViewById(R.id.back_button);
        bottom_layout = findViewById(R.id.bottom_layout);
        barChart = (BarChart) findViewById(R.id.barGraph);
        topLeft_option = findViewById(R.id.topLeft_option);
        topRight_option = findViewById(R.id.topRight_option);
        topic_btn = findViewById(R.id.topic_button);
        timer = findViewById(R.id.timer);
        timerHr = findViewById(R.id.timerHr);
        timerMin = findViewById(R.id.timerMin);
        timerSec = findViewById(R.id.timerSec);
        colonM = findViewById(R.id.timerColonM);
        colonS = findViewById(R.id.timerColonS);
        incrementBy1min = findViewById(R.id.incrementBy1min);

//        TimeSetter.seconds = 50 * 60;
//        TimeSetter.breakSeconds = 4 * 60;

        TimeSetter.seconds = getSeconds();
        TimeSetter.breakSeconds = getBreakSeconds();

        seconds = TimeSetter.seconds;
        breakSeconds = TimeSetter.breakSeconds;
        totalSeconds_passed = 0;

        Log.i("secondsLeft", String.valueOf(getMillisLeftInSharedPrefs()));
        Log.i("timerRunning", String.valueOf(getTimerState()));
        Log.i("BreakState", String.valueOf(getBreakState()));
        Log.i("timePassed", String.valueOf(getTimePassed()));
        Log.i("endTimeInMillis", String.valueOf(getEndTimeInMillis()));
        Log.i("incrementedTimeByMin", String.valueOf(getIncrementedTimeByMin()));

        isBreak = getBreakState();
        back_btn.setVisibility(View.GONE);
        incrementBy1min.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.GONE);

        topicSelected = "";
        incrementedTimeInMin = getIncrementedTimeByMin();

        bottomSheetBehavior.setDraggable(false);
        getTopicSelectedFromSharedPrefs();

        if(topicSelected.isEmpty()){
            topic_btn.setVisibility(View.VISIBLE);
            Log.i("topicSelected", "empty");
            topicSelected = "Break";
            topic_btn.setText(topicSelected);
            timerTextSet(4 * 60);
            isBreak = true;
            storeBreakState(true);

            if(getMillisLeftInSharedPrefs() > 0){
                timerTextSet(getMillisLeftInSharedPrefs());
                breakSeconds = getMillisLeftInSharedPrefs();
                timerTextSet(getMillisLeftInSharedPrefs());
            }
        }else{
            if(!isBreak) {
                topic_btn.setText(topicSelected);
            }else{
                topic_btn.setText("Break");
            }

            topic_btn.setVisibility(View.VISIBLE);
            Log.i("topicSelected 115", topicSelected);

            if(topicSelected.toString().equals("Break")) {
                timerTextSet(getMillisLeftInSharedPrefs());
                isBreak = true;
                breakSeconds = getMillisLeftInSharedPrefs();
            }
        }

        timerRunning = 0;
        if(getTimerState() == 1) {
            //running
            Calendar calendar = Calendar.getInstance();
            long currTimeInMillis = calendar.getTimeInMillis();

            long secondsLeft = (getEndTimeInMillis() - currTimeInMillis) / 1000;
            totalSeconds_passed = getTimePassed();

            Log.i("secondsLeft", String.valueOf(secondsLeft));

            if(secondsLeft < 1){
                //running....ended
                timerRunning = 0;
                startTimer(0);
                startTimer(0);
                Log.i("BreakState", String.valueOf(getBreakState()));
                incrementBy1min.setVisibility(View.INVISIBLE);
                storeTimerState(timerRunning);
                totalSeconds_passed = getTimePassed() + getMillisLeftInSharedPrefs();
                Log.i("totalSeconds_passsed", String.valueOf(totalSeconds_passed));
                if(!isBreak){
                    topRight_option.setText("Save");
                    topLeft_option.setText("Cancel");
                    topLeft_option.setVisibility(View.VISIBLE);
                    topRight_option.setVisibility(View.VISIBLE);
                }else{
                    topRight_option.setText("Reset");
                    topLeft_option.setText("Cancel");
                    topLeft_option.setVisibility(View.VISIBLE);
                    topRight_option.setVisibility(View.VISIBLE);
                }
            }else{
                startTimer((int) secondsLeft);
            }

        }else if(getTimerState() == 0 && getTimePassed() > 0){
            //paused
            long secondsLeft = getMillisLeftInSharedPrefs();
            Log.i("SecondsLeft", String.valueOf(secondsLeft));
            if(isBreak){
                breakSeconds = secondsLeft;
            }else{
                seconds = secondsLeft;
            }

            timerTextSet(secondsLeft);
            totalSeconds_passed = getTimePassed();

        }else if(getTimerState() == 0 && getTimePassed() == 0){
            //timer has not been initialized yet
            Log.i("Timer State", "not being initialized yet");
            if(getBreakState()) {
                timerTextSet(breakSeconds);
            }else{
                timerTextSet(seconds);
            }
        }

        if(get_is_saved()) {
            post_savedChanges();
        }else if(get_is_canceled()) {
            post_canceledChanges();
        }else if(get_is_reset()) {
            post_resetChanges();
        }


        bottom_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(bottomSheetBehavior.getState()).equals("4")){
                    expand_bottomSheet(bottomSheetBehavior, bottomSheet);
                    barChart.setVisibility(View.VISIBLE);
                    initialize_bar();
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(bottomSheetBehavior.getState()).equals("3")){
                    v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.on_click));
                    collapse_bottomSheet(bottomSheetBehavior, bottomSheet);
                    barChart.setVisibility(View.GONE);
                }
            }
        });

        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.on_click));
            }
        });

        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.on_click));
            }
        });

        topLeft_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_time();

                topLeft_option.setVisibility(View.INVISIBLE);
                topRight_option.setVisibility(View.VISIBLE);
                topRight_option.setText("Set Timer");
                incrementedTimeInMin = 0;
                storeincrementTedTimeByMin(incrementedTimeInMin);


                seconds = TimeSetter.seconds;
                timerTextSet(seconds);
                breakSeconds = TimeSetter.breakSeconds;
            }
        });

        topRight_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topRight_option.getText().toString().equals("Save")){
                    save_time();

                    incrementedTimeInMin = 0;
                    storeincrementTedTimeByMin(incrementedTimeInMin);
                    topLeft_option.setVisibility(View.INVISIBLE);
                    topRight_option.setVisibility(View.VISIBLE);
                    topRight_option.setText("Set Timer");

                }else if(topRight_option.getText().toString().equals("Set Timer")){
                    setTimer();
                }else if(topRight_option.getText().toString().equals("Reset")){
                    countDownTimer.cancel();
                    countDownTimer.onFinish();

                    breakSeconds = TimeSetter.breakSeconds;
                    timerTextSet(breakSeconds);

                    topic_btn.setText("Break");

                    topLeft_option.setVisibility(View.INVISIBLE);
                    topRight_option.setVisibility(View.VISIBLE);
                    topRight_option.setText("Set Timer");

                    isBreak = true;

                    storeBreakState(isBreak);
                    Log.i("StoreBreakState", "called 312");
                    storeMillisLeftInSharedPrefs(TimeSetter.breakSeconds);
                    storeTimePassed(0);
                }

            }
        });

        topic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerRunning == 0){
                    if(countDownTimer != null){
                        save_time();
                    }
                    topRight_option.setVisibility(View.VISIBLE);
                    topRight_option.setText("Set Timer");
                    topLeft_option.setVisibility(View.INVISIBLE);

                    v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.on_click));
                    startActivity(new Intent(MainActivity.this, TopicsList.class));

                    Log.i("topicSelectedTopicBtn", topicSelected);

                    saveTopicInSharedPrefs();
                    getTopicSelectedFromSharedPrefs();

                    Log.i("topicSelectedTopicBtn", topicSelected);


                }
            }
        });

        incrementBy1min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seconds = seconds + 60;
                countDownTimer.cancel();
                timerRunning = 0;
                startTimer((int) seconds);
                incrementedTimeInMin = incrementedTimeInMin + 1;
                storeincrementTedTimeByMin(incrementedTimeInMin);
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBreak){
                    startTimer((int) breakSeconds);
                    Log.i("BReak", "true");
                    Log.i("timeSetFor", String.valueOf(breakSeconds) + " " + String.valueOf(breakSeconds/60));
                }else{
                    startTimer((int) seconds);
                    Log.i("BReak", "false");
                    Log.i("timeSetFor", String.valueOf(seconds) + " " + String.valueOf(seconds/60));
                }
            }
        });

        //BarGraph
        setData();

        final ArrayList<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thur");
        days.add("Fri");
        days.add("Sat");
        days.add("Sun");
    }

    private void post_resetChanges() {
        Log.i("postChanges", "reset");
        Toast.makeText(MainActivity.this, "reset", Toast.LENGTH_LONG).show();
        MainActivity.topRight_option.setText("Set Timer");
        MainActivity.topLeft_option.setVisibility(View.INVISIBLE);
        MainActivity.incrementBy1min.setVisibility(View.INVISIBLE);
        MainActivity.incrementedTimeInMin = 0;

        MainActivity.breakSeconds = TimeSetter.breakSeconds;
        MainActivity.timerTextSet(getBreakSeconds());

        MainActivity.topic_btn.setText("Break");

        MainActivity.isBreak = true;
        timerRunning = 0;
        storeTimerState(0);
        topic_btn.setText("Break");

        store_is_reset(false);
    }

    private void post_canceledChanges() {
        Log.i("postChanges", "cancelled");
        Toast.makeText(MainActivity.this, "cancelled", Toast.LENGTH_LONG).show();
        MainActivity.topRight_option.setText("Set Timer");
        MainActivity.topLeft_option.setVisibility(View.INVISIBLE);
        MainActivity.incrementBy1min.setVisibility(View.INVISIBLE);

        MainActivity.incrementedTimeInMin = 0;
        timerRunning = 0;

        storeTimerState(0);
        timerTextSet(TimeSetter.seconds);
        storeTimerState(0);
        isBreak = false;
        storeBreakState(false);
        Log.i("StoreBreakState", "called 421");
        if(topicSelected.equals("Break")) {
            startActivity(new Intent(MainActivity.this, TopicsList.class));
        }
        topic_btn.setText(topicSelected);
        store_is_canceled(false);
    }

    private void post_savedChanges() {
        Log.i("postChanges", "saved");
        Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_LONG).show();
        MainActivity.topRight_option.setText("Set Timer");
        MainActivity.topLeft_option.setVisibility(View.INVISIBLE);
        MainActivity.incrementBy1min.setVisibility(View.INVISIBLE);
        MainActivity.incrementedTimeInMin = 0;

        if(getBreakState()){
            MainActivity.totalSeconds_passed = 0;
            storeTimePassed(MainActivity.totalSeconds_passed);

            MainActivity.seconds = TimeSetter.seconds;
            MainActivity.breakSeconds = TimeSetter.seconds;

            MainActivity.timerTextSet(TimeSetter.seconds);

        }else{
            MainActivity.seconds = TimeSetter.seconds;
            MainActivity.timerTextSet(TimeSetter.seconds);
        }
        topRight_option.setText("Set Timer");
        topLeft_option.setVisibility(View.INVISIBLE);
        timerRunning = 0;
        storeTimerState(0);
        store_is_saved(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startTimer(final int sec) {
        Log.i("BReak", String.valueOf(isBreak));
        if(!isBreak){
            //some topic is selected
            Log.i("topicSelectedNew", topicSelected);
            saveTopicInSharedPrefs();
        }

        if(topic_btn.getText().equals("null")){
            startActivity(new Intent(MainActivity.this, TopicsList.class));
        }else{
            if(timerRunning == 0) {
                incrementBy1min.setVisibility(View.VISIBLE);

                topRight_option.setVisibility(View.INVISIBLE);
                topLeft_option.setVisibility(View.INVISIBLE);

                countDownTimer = new CountDownTimer(sec * 1000, 1000) {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long s = millisUntilFinished / 1000 % 60;
                        long h = millisUntilFinished /1000 / 60;
                        long m = h % 60;
                        h = h / 60;

                        if(h == 0 && m == 0){
                            timerHr.setVisibility(View.GONE);
                            colonM.setVisibility(View.GONE);
                            timerMin.setVisibility(View.VISIBLE);
                            colonS.setVisibility(View.VISIBLE);
                            timerSec.setVisibility(View.VISIBLE);

                            timerMin.setText("0");

                        }else if(h == 0 && m != 0){
                            timerHr.setVisibility(View.GONE);
                            colonM.setVisibility(View.GONE);
                            timerMin.setVisibility(View.VISIBLE);
                            colonS.setVisibility(View.VISIBLE);
                            timerSec.setVisibility(View.VISIBLE);

                            timerMin.setText(String.valueOf(m));

                        }else if(h != 0 && m < 10){
                            timerHr.setVisibility(View.VISIBLE);
                            colonM.setVisibility(View.VISIBLE);
                            timerMin.setVisibility(View.VISIBLE);
                            colonS.setVisibility(View.VISIBLE);
                            timerSec.setVisibility(View.VISIBLE);

                            timerHr.setText(String.valueOf(h));
                            timerMin.setText("0" + String.valueOf(m));

                        }else if(h != 0 && m >= 10){
                            timerHr.setVisibility(View.VISIBLE);
                            colonM.setVisibility(View.VISIBLE);
                            timerMin.setVisibility(View.VISIBLE);
                            colonS.setVisibility(View.VISIBLE);
                            timerSec.setVisibility(View.VISIBLE);

                            timerHr.setText(String.valueOf(h));
                            timerMin.setText(String.valueOf(m));
                        }

                        if(s < 10){
                            timerSec.setText("0" + String.valueOf(s));
                        }else{
                            timerSec.setText(String.valueOf(s));
                        }

                        seconds = (int) (millisUntilFinished / 1000);
                        breakSeconds = (int) (millisUntilFinished/1000);
                        if(!isBreak){
                            totalSeconds_passed = totalSeconds_passed + 1;
                            storeTimePassed(totalSeconds_passed);
                            storeMillisLeftInSharedPrefs(seconds);
                        }else{
                            storeMillisLeftInSharedPrefs(breakSeconds);
                        }

                        storeEndTimeInMillis(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        if(is_app_onForeground()) {
                            Intent intent = new Intent(MainActivity.this, TimeUpActivity.class);
                            intent.putExtra("topicSelected", topicSelected);

                            startActivity(intent);
                        }
                        totalSeconds_passed = TimeSetter.seconds + getIncrementedTimeByMin() * 60;
                        seconds = TimeSetter.seconds;
                        timerTextSet(seconds);

                        timerRunning = 0;
                        storeTimerState(timerRunning);

                        if(!isBreak){
                            topRight_option.setVisibility(View.VISIBLE);
                            topLeft_option.setVisibility(View.VISIBLE);
                            topLeft_option.setText("Cancel");
                            topRight_option.setText("Save");
                        }else if(isBreak){
                            topRight_option.setVisibility(View.VISIBLE);
                            topLeft_option.setVisibility(View.INVISIBLE);
                            topRight_option.setText("Set Timer");

                            if(!topicSelected.equals("null")){
                                topic_btn.setText(topicSelected);
                            }else {
                                startActivity(new Intent(MainActivity.this, TopicsList.class));
                            }
                        }
                    }
                }.start();
                timerRunning = 1;
                storeTimerState(timerRunning);

                setAlarm(sec * 1000);

            }else if(timerRunning == 1){
                incrementBy1min.setVisibility(View.INVISIBLE);
                countDownTimer.cancel();
                cancelAlarm();
                timerRunning = 0;
                storeTimerState(timerRunning);

                if(!isBreak){
                    topRight_option.setText("Save");
                    topLeft_option.setText("Cancel");
                    topLeft_option.setVisibility(View.VISIBLE);
                    topRight_option.setVisibility(View.VISIBLE);
                }else{
                    topRight_option.setText("Reset");
                    topLeft_option.setText("Cancel");
                    topLeft_option.setVisibility(View.VISIBLE);
                    topRight_option.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private boolean is_app_onForeground() {
        ActivityManager am = (ActivityManager) MainActivity.this.getSystemService(ACTIVITY_SERVICE);
        // The first in the list of RunningTasks is always the foreground task.
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
        Log.i("foreground_taskName", foregroundTaskPackageName);

        if(foregroundTaskPackageName.equals("com.example.tamarind")) {
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAlarm(int i) {
        Calendar calendar = Calendar.getInstance();
        long currentTimeInMillis = calendar.getTimeInMillis();

        long setAlarmToTime = (currentTimeInMillis + i);

        Log.i("setAlarmToTime", String.valueOf(setAlarmToTime));
        Log.i("setAlarmAtTime", String.valueOf(currentTimeInMillis));

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(setAlarmToTime);

        startAlarm(c);
    }

    private void cancelAlarm() {
        Log.i("Note", "cancelAlarmCalled");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);

        if(isBreak) {
            intent.putExtra("isBreak",true);
            Log.i("BreakState 632", String.valueOf(isBreak));
        }else {
            Log.i("BreakState 634", String.valueOf(isBreak));
            intent.putExtra("isBreak",false);
        }

        intent.putExtra("timePassed", seconds + getIncrementedTimeByMin());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startAlarm(Calendar c) {
        Log.i("BreakState", String.valueOf(isBreak));
        Log.i("topicSelected", topicSelected);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);
        Log.i("startAlarmBreak", String.valueOf(getBreakState()));
        intent.putExtra("topicSelected", topicSelected);
        if(isBreak) {
            intent.putExtra("isBreak",true);
            Log.i("BreakState 632", String.valueOf(isBreak));
        }else {
            Log.i("BreakState 634", String.valueOf(isBreak));
            intent.putExtra("isBreak",false);
        }

        intent.putExtra("timePassed", seconds + getIncrementedTimeByMin());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private int getBreakSeconds() {
        int time = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getInt("breakSeconds",
                4 * 60);
        return time;
    }

    private int getSeconds() {
        int timePassed = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getInt("seconds",
                50 * 60);
        return timePassed;
    }

    private void storeTimePassed(long totalSeconds_passed) {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong("totalTimePassed",
                totalSeconds_passed).commit();
    }

    private long getTimePassed() {
        long timePassed = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong("totalTimePassed",
                0);
        return timePassed;
    }

    private void storeBreakState(Boolean break_state) {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("breakState",
                break_state).commit();
    }

    private Boolean getBreakState() {
        Boolean isBreak = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("breakState",
                false);

        return isBreak;
    }

    private Boolean get_is_saved() {

        Boolean is_saved = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("is_saved",
                false);
        Log.i("is_saved", String.valueOf(is_saved));
        Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_LONG).show();
        return is_saved;
    }

    private Boolean get_is_reset() {
        Boolean is_reset = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("is_reset",
                false);
        Log.i("is_reset", String.valueOf(is_reset));
        Toast.makeText(MainActivity.this, "is_reset", Toast.LENGTH_LONG).show();

        return is_reset;
    }

    private Boolean get_is_canceled() {
        Boolean is_canceled = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("is_canceled",
                false);
        Toast.makeText(MainActivity.this, "is_canceled", Toast.LENGTH_LONG).show();
        return is_canceled;
    }

    private void store_is_saved(Boolean is_saved) {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("is_saved",
                is_saved).commit();
    }

    private void store_is_reset(Boolean is_reset) {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("is_reset",
                is_reset).commit();
    }

    private void store_is_canceled(Boolean is_canceled) {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean("is_canceled",
                is_canceled).commit();
    }

    private void storeincrementTedTimeByMin(Long incrementedTimeInMin) {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong("incrementedTimeByMin",
                incrementedTimeInMin).commit();
    }

    private Long getIncrementedTimeByMin() {
        Long time = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong("incrementedTimeByMin",
                0);
        return time;
    }



    private void storeTimerState(long i) {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong("timerState",
                i).commit();
    }

    private long getTimerState() {
        long timerState = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong("timerState",
                0);
        return timerState;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void storeEndTimeInMillis(long millisLeft) {
        SharedPreferences sharedPreferences = getSharedPreferences("endTimeInMillis", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Calendar calendar = Calendar.getInstance();
        long currentTimeInMillis = calendar.getTimeInMillis();

        editor.putLong("endTimeInMIllis", currentTimeInMillis + millisLeft);
        editor.apply();
    }

    private long getEndTimeInMillis() {
        SharedPreferences sharedPreferences = getSharedPreferences("endTimeInMillis", MODE_PRIVATE);
        long endTimeInMilllis = sharedPreferences.getLong("endTimeInMIllis", 0);

        return endTimeInMilllis;
    }

    private void storeMillisLeftInSharedPrefs(long i) {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong("millisLeft",
                i).commit();
    }

    private long getMillisLeftInSharedPrefs(){
        long millisLeft = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong("millisLeft",
                0);

        return millisLeft;
    }

    private void saveTopicInSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefsTopicSelected", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("topicSelected", topicSelected);
        editor.apply();
        editor.commit();
    }

    private void getTopicSelectedFromSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefsTopicSelected", MODE_PRIVATE);
        topicSelected = sharedPreferences.getString("topicSelected", "");
    }

    public static void timerTextSet(long seconds) {
        int hrs = (int) ((seconds % 86400 ) / 3600);
        int mins = (int) (((seconds % 86400 ) % 3600 ) / 60);
        int sec = (int) (seconds % 60);

        if(hrs == 0 && mins == 0 && sec == 0){
            MainActivity.timerHr.setVisibility(View.GONE);
            MainActivity.colonM.setVisibility(View.GONE);
            MainActivity.timerMin.setVisibility(View.VISIBLE);
            MainActivity.colonS.setVisibility(View.VISIBLE);
            MainActivity.timerSec.setVisibility(View.VISIBLE);
            MainActivity.timerMin.setText("50");
        }else if(hrs == 0 && mins == 0 && sec != 0){
            MainActivity.timerHr.setVisibility(View.GONE);
            MainActivity.colonM.setVisibility(View.GONE);
            MainActivity.timerMin.setVisibility(View.VISIBLE);
            MainActivity.colonS.setVisibility(View.VISIBLE);
            MainActivity.timerSec.setVisibility(View.VISIBLE);
            MainActivity.timerMin.setText(String.valueOf(mins));
            MainActivity.timerSec.setText(String.valueOf(sec));
        }
        else if(hrs == 0 && mins != 0){
            MainActivity.timerHr.setVisibility(View.GONE);
            MainActivity.colonM.setVisibility(View.GONE);
            MainActivity.timerMin.setVisibility(View.VISIBLE);
            MainActivity.colonS.setVisibility(View.VISIBLE);
            MainActivity.timerSec.setVisibility(View.VISIBLE);
            MainActivity.timerMin.setText(String.valueOf(mins));

        }else if(hrs != 0 && mins < 10){
            MainActivity.timerHr.setVisibility(View.VISIBLE);
            MainActivity.colonM.setVisibility(View.VISIBLE);
            MainActivity.timerMin.setVisibility(View.VISIBLE);
            MainActivity.colonS.setVisibility(View.VISIBLE);
            MainActivity.timerSec.setVisibility(View.VISIBLE);
            MainActivity.timerHr.setText(String.valueOf(hrs));
            MainActivity.timerMin.setText("0" + String.valueOf(mins));
        }else if(hrs != 0 && mins >= 10){
            MainActivity.timerHr.setVisibility(View.VISIBLE);
            MainActivity.colonM.setVisibility(View.VISIBLE);
            MainActivity.timerMin.setVisibility(View.VISIBLE);
            MainActivity.colonS.setVisibility(View.VISIBLE);
            MainActivity.timerSec.setVisibility(View.VISIBLE);
            MainActivity.timerHr.setText(String.valueOf(hrs));
            MainActivity.timerMin.setText(String.valueOf(mins));
        }

        if(sec < 10){
            MainActivity.timerSec.setText("0" + String.valueOf(sec));
        }else{
            MainActivity.timerSec.setText(String.valueOf(sec));
        }
    }

    private void save_time() {
        if(!isBreak){
            Log.i("timeRecorded", topic_btn.getText() + ": " + totalSeconds_passed);
            countDownTimer.cancel();
            countDownTimer.onFinish();

            totalSeconds_passed = 0;
            storeTimePassed(totalSeconds_passed);

            seconds = TimeSetter.seconds;
            breakSeconds = TimeSetter.seconds;

        }else{
            seconds = TimeSetter.seconds;
            timerTextSet(seconds);
        }

        storeBreakState(isBreak);
        Log.i("StoreBreakState", "called 852");
        storeMillisLeftInSharedPrefs(0);
        storeTimerState(0);
    }

    private void setTimer() {
        startActivity(new Intent(MainActivity.this, TimeSetter.class));
    }

    private void cancel_time() {
        if(isBreak){
            isBreak = false;
            if(!topicSelected.equals("null") && !topicSelected.equals("Break")){
                topic_btn.setText(topicSelected);
            }else{
                startActivity(new Intent(MainActivity.this, TopicsList.class));
            }

            if(topicSelected.equals("Break")) {
                //there are no other topics initialised
                isBreak = true; //as default
            }
        }

        totalSeconds_passed = 0;
        storeTimePassed(totalSeconds_passed);

        storeBreakState(isBreak);
        Log.i("StoreBreakState", "called 881");


        if(isBreak) {
            storeMillisLeftInSharedPrefs(TimeSetter.breakSeconds);
        }else {
            storeMillisLeftInSharedPrefs(TimeSetter.seconds);
        }

        cancelAlarm();
        storeTimerState(0);
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    private void initialize_bar() {
        barChart.animateY(  800);
    }

    private void setData(){
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, 3.0f));
        barEntries.add(new BarEntry(1f, 2.0f));
        barEntries.add(new BarEntry(2f, 3.72f));
        barEntries.add(new BarEntry(3f, 4.12f));
        barEntries.add(new BarEntry(4f, 2.4f));
        barEntries.add(new BarEntry(5f, 5.32f));
        barEntries.add(new BarEntry(6f, 3.8f));
    }

    @SuppressLint("WrongConstant")
    private void collapse_bottomSheet(BottomSheetBehavior bottomSheetBehavior, View bottomSheet) {
        bottomSheetBehavior.setState(4);

        back_btn.setVisibility(View.GONE);
        bottomSheet.setBackground(getResources().getDrawable(R.drawable.curved_layout));
        coordinatorLayout.setVisibility(View.VISIBLE);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        getWindow().getDecorView().setSystemUiVisibility(0);
    }


    @SuppressLint("WrongConstant")
    private void expand_bottomSheet(BottomSheetBehavior bottomSheetBehavior, View bottomSheet) {
        bottomSheetBehavior.setState(3);

        back_btn.setVisibility(View.VISIBLE);
        bottomSheet.setBackground(getResources().getDrawable(R.drawable.rectangle_layout));
        coordinatorLayout.setVisibility(View.GONE);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //barChart
        barDataSet = new BarDataSet(barEntries, null);
        barData = new BarData(barDataSet);

        barChart.getLegend().setEnabled(false);

        barChart.setData(barData);
        barDataSet.setColor(getResources().getColor(R.color.low_primary));
        barChart.setDescription(null);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.animateX(800);
        barChart.animateY(800);
        barChart.setFitBars(true);

        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);

        barDataSet.setDrawValues(false);
        barDataSet.setHighLightColor(getResources().getColor(R.color.colorPrimary));
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getXAxis().setDrawLabels(true);

        barChart.getXAxis().setTextSize(12);

        barChart.getXAxis().setEnabled(true);
        barChart.getAxisRight().setEnabled(true);
        barChart.getAxisLeft().setEnabled(false);

        barChart.getAxisRight().setTextColor(getResources().getColor(R.color.unFocused_dark));
        barChart.getAxisRight().setTextSize(14);

        barChart.getAxisRight().setAxisMaximum(6);
        barChart.getAxisRight().setAxisMinimum(0);

        barChart.getAxisRight().setLabelCount(4, true);

        String[] xAxisLables = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }
}
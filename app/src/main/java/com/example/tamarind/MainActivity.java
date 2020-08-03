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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.cert.CollectionCertStoreParameters;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private BottomSheetBehavior bottomSheetBehavior;
    ImageView menu_btn, more_btn, back_btn;

    static String topicSelected;

    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout, appbar;
    LinearLayout total_time, bottom_layout;

    static TextView topLeft_option, topRight_option;
    static TextView incrementBy1min;
    LinearLayout timer;
    ProgressBar progressBar;


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
    static ArrayList<topic_item_forList> topics;
    static long incrementedTimeInMin;
    public long timerRunning;
    static public long totalSeconds_passed;
    public long timer_started_at;

    //tracking data
    ImageView select_previous_day, select_next_day;
    TextView current_day_display;
    ListView listView_topics_recorded;
    static ArrayList<topic_item> topic_items;
    static ArrayList<topic_item> current_View_list;
    DateFormat dateFormat;
    TextView day_reference;
    TextView tot_hr, tot_hr_text, tot_min, tot_min_text;
    static long topic_time_max_progress;

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
        progressBar = findViewById(R.id.progressBar);
        listView_topics_recorded = findViewById(R.id.listView_topics_recorded);
        select_next_day = findViewById(R.id.select_next_day);
        select_previous_day = findViewById(R.id.select_previous_day);
        current_day_display = findViewById(R.id.current_day);
        day_reference = findViewById(R.id.day_reference);
        tot_hr = findViewById(R.id.tot_hr);
        tot_hr_text = findViewById(R.id.tot_hr_txt);
        tot_min = findViewById(R.id.tot_min);
        tot_min_text = findViewById(R.id.tot_min_txt);

        bottomSheet.setNestedScrollingEnabled(true);

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

        //time recording
        load_topic_items();

        current_View_list = new ArrayList<topic_item>();
        dateFormat = new SimpleDateFormat("EEE , dd MMM");
        update_currentView_list(Calendar.getInstance());
        listView_topics_recorded.setVisibility(View.GONE);
        select_previous_day.setVisibility(View.GONE);
        select_next_day.setVisibility(View.GONE);
        current_day_display.setVisibility(View.GONE);

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
            //timer running
            Calendar calendar = Calendar.getInstance();
            long currTimeInMillis = calendar.getTimeInMillis();

            long secondsLeft = (getEndTimeInMillis() - currTimeInMillis) / 1000;
            totalSeconds_passed = getTimePassed();

            Log.i("secondsLeft", String.valueOf(secondsLeft));

            if(secondsLeft < 1){
                //timer was running, now ended
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
            //timer paused
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
        //deals with the actions performed when the app was in foreground previously
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
                progressBar.setProgress(0);
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
                    progressBar.setProgress(0);

                }else if(topRight_option.getText().toString().equals("Set Timer")){
                    setTimer();
                }else if(topRight_option.getText().toString().equals("Reset")){
                    resetTimer();
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

        //bottomSheet
        final Calendar calendar = Calendar.getInstance();
        final Calendar currentDate = Calendar.getInstance();
        current_day_display.setText(dateFormat.format(calendar.getTime()));

        select_next_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                long difference = calendar.getTimeInMillis() - currentDate.getTimeInMillis();
                int days_diff = (int) TimeUnit.MILLISECONDS.toDays(difference);
                Log.i("daysDifference", String.valueOf(days_diff));

                if(difference <= 0) {
                    current_day_display.setText(dateFormat.format(calendar.getTime()));
                    if(difference == 0){
                        day_reference.setText("Today");
                    }else if(days_diff == -1){
                        //yesterday
                        day_reference.setText("Yesterday");
                    }else{
                        day_reference.setText("");
                    }
                }else if(difference > 0) {
                    //today
                    day_reference.setText("Today");
                    calendar.setTime(currentDate.getTime());
                }
                update_currentView_list(calendar);
                initalizeAdapter();
            }
        });

        select_previous_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                current_day_display.setText(dateFormat.format(calendar.getTime()));

                long difference = calendar.getTimeInMillis() - currentDate.getTimeInMillis();
                int days_diff = (int) TimeUnit.MILLISECONDS.toDays(difference);

                Log.i("daysDifference", String.valueOf(days_diff));

                if(days_diff == -1){
                    //yesterday
                    day_reference.setText("Yesterday");
                }else {
                    day_reference.setText("");
                }

                update_currentView_list(calendar);
                initalizeAdapter();
            }
        });

        current_day_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_day_display.setText(dateFormat.format(currentDate.getTime()));
                calendar.setTime(currentDate.getTime());

                day_reference.setText("Today");
                update_currentView_list(calendar);
                initalizeAdapter();
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

    private void resetTimer() {
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

    private void post_resetChanges() {
        Log.i("postChanges", "reset");
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

        progressBar.setProgress(0);
        store_is_reset(false);
    }

    private void post_canceledChanges() {
        Log.i("postChanges", "cancelled");
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
        progressBar.setProgress(0);
        store_is_canceled(false);
    }

    private void post_savedChanges() {
        Log.i("postChanges", "saved");
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
        progressBar.setProgress(0);
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

                        updateTimerText(h, m, s);
                        updateProgressbar(millisUntilFinished);

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

    private void updateProgressbar(long millisLeft) {
        long totalmillis;

        if(isBreak) {
            totalmillis = (getBreakSeconds() * 1000) + (getIncrementedTimeByMin() * 60 * 1000);
        } else {
            totalmillis = (getSeconds() * 1000) +( getIncrementedTimeByMin() * 60 * 1000);
        }

        int progress_millis = (int) (((totalmillis - millisLeft) * 100) / totalmillis);
        //Log.i("progress_millis", String.valueOf(progress_millis));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(progress_millis, true);
        }
    }

    private void updateTimerText(long h, long m, long s) {
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
        intent.putExtra("topicSelected", topicSelected);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startAlarm(Calendar c) {
        Log.i("BreakState", String.valueOf(isBreak));
        Log.i("topicSelected", topicSelected);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReciever.class);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void save_time() {
        if(!isBreak){
            Log.i("timeRecorded", topic_btn.getText() + ": " + totalSeconds_passed);
            countDownTimer.cancel();
            store_time_recorded(topicSelected, totalSeconds_passed);
            totalSeconds_passed = 0;
            storeTimePassed(totalSeconds_passed);

            seconds = TimeSetter.seconds;
            breakSeconds = TimeSetter.seconds;
            timerTextSet(seconds);

        }else{
            seconds = TimeSetter.seconds;
            timerTextSet(seconds);
        }

        storeBreakState(isBreak);
        Log.i("StoreBreakState", "called 852");
        storeMillisLeftInSharedPrefs(0);
        storeTimerState(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void store_time_recorded(String topicSelected, long totalSeconds_passed) {
        load_topic_items();
        //record time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() - (TimeSetter.seconds * 1000) - ((getIncrementedTimeByMin() + 4) * 60 * 1000));
        String date = dateFormat.format(calendar.getTime() );
        topic_item topic_item = new topic_item(topicSelected, totalSeconds_passed, date , calendar.get(Calendar.DAY_OF_WEEK));

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
        update_currentView_list(Calendar.getInstance());
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
        listView_topics_recorded.setVisibility(View.GONE);
        select_next_day.setVisibility(View.GONE);
        select_previous_day.setVisibility(View.GONE);
        current_day_display.setVisibility(View.GONE);

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        getWindow().getDecorView().setSystemUiVisibility(0);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("WrongConstant")
    private void expand_bottomSheet(BottomSheetBehavior bottomSheetBehavior, View bottomSheet) {
        bottomSheetBehavior.setState(3);

        back_btn.setVisibility(View.VISIBLE);
        bottomSheet.setBackground(getResources().getDrawable(R.drawable.rectangle_layout));
        coordinatorLayout.setVisibility(View.GONE);
        listView_topics_recorded.setVisibility(View.VISIBLE);
        select_next_day.setVisibility(View.VISIBLE);
        select_previous_day.setVisibility(View.VISIBLE);
        current_day_display.setVisibility(View.VISIBLE);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        update_currentView_list(Calendar.getInstance());
        initalizeAdapter();

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

        String[] xAxisLables = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void update_currentView_list(Calendar calendarPassed) {
        current_View_list.clear();
        topic_time_max_progress = 0;
        long total_time_recorded = 0;
        for(int i = topic_items.size() - 1; i >= 0; i--){
            if(topic_items.get(i).date_recorded.equals(dateFormat.format(calendarPassed.getTime()))) {
                current_View_list.add(topic_items.get(i));
                total_time_recorded += topic_items.get(i).time_recorded;
                if(topic_time_max_progress < topic_items.get(i).time_recorded){
                    topic_time_max_progress = topic_items.get(i).time_recorded;
                }
            }
        }

        update_total_time_display(total_time_recorded);
        topic_time_max_progress += 2;
    }

    private void initalizeAdapter() {
        Collections.sort(current_View_list, new time_recordedSorter());
        topicItems__Adapter listView_topics_adapter = new topicItems__Adapter(this, current_View_list);
        listView_topics_recorded.setAdapter(listView_topics_adapter);
        listView_topics_recorded.setDivider(null);
    }

    private void save_topic_items() {
        SharedPreferences sharedPreferences = getSharedPreferences("topic_items", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(topic_items);

        editor.putString("topic_items", json);
        editor.apply();
    }

    private void load_topic_items() {
        SharedPreferences sharedPreferences = getSharedPreferences("topic_items", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("topic_items", null);

        Type type = new TypeToken<ArrayList<topic_item>>(){}.getType();
        topic_items = gson.fromJson(json, type);

        if (topic_items == null) {
            topic_items = new ArrayList<>();
        }
    }

    private void update_total_time_display(long minutes_recorded) {
        int hr = (int) minutes_recorded / 60;
        int minutes = (int) minutes_recorded % 60;

        if(hr == 0){
            tot_hr_text.setVisibility(View.GONE);
            tot_hr.setVisibility(View.GONE);
        }else{
            tot_hr.setText(String.valueOf(hr));
            tot_hr_text.setVisibility(View.VISIBLE);
            tot_hr.setVisibility(View.VISIBLE);
        }

        tot_min.setText(String.valueOf(minutes));
    }
}
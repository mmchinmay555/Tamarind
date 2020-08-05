package com.example.tamarind;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements bottom_sheet_more.BottomSheetListener {
    private BottomSheetBehavior bottomSheetBehavior;
    ImageView  more_btn, back_btn;

    static String topicSelected;

    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout, appbar;
    LinearLayout total_time, bottom_layout;

    static TextView topLeft_option, topRight_option;
    static TextView incrementBy1min;
    LinearLayout timer;
    ProgressBar progressBar;


//  BarChart SetUp
    ArrayList barEntries;
    CoordinatorLayout barChart_View;
    ProgressBar p_sun, p_mon, p_tue, p_wed, p_thu, p_fri, p_sat;

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
    static Calendar first_data;

    //tracking data
    ImageView select_previous_week, select_next_week;
    TextView current_day_display;
    ListView listView_topics_recorded;
    static ArrayList<topic_item> topic_items;
    static ArrayList<topic_item> current_View_list;
    static ArrayList<Integer> current_Week_list;
    DateFormat dateFormat;
    TextView day_reference;
    TextView tot_hr, tot_hr_text, tot_min, tot_min_text;
    static long topic_time_max_progress;

    Calendar calendar;
    Calendar currentDate;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        final View bottomSheet = findViewById(R.id.bottomSheet);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        more_btn = findViewById(R.id.more_btn);
        coordinatorLayout = findViewById(R.id.app_bar);
        appbar = findViewById(R.id.topbar);
        total_time = findViewById(R.id.time_display);
        back_btn = findViewById(R.id.back_button);
        bottom_layout = findViewById(R.id.bottom_layout);
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
        select_next_week = findViewById(R.id.select_next_week);
        select_previous_week = findViewById(R.id.select_previous_week);
        current_day_display = findViewById(R.id.current_day);
        day_reference = findViewById(R.id.day_reference);
        tot_hr = findViewById(R.id.tot_hr);
        tot_hr_text = findViewById(R.id.tot_hr_txt);
        tot_min = findViewById(R.id.tot_min);
        tot_min_text = findViewById(R.id.tot_min_txt);
        //BarChart related
        barChart_View = findViewById(R.id.barChart_View);
        barChart_View.setVisibility(View.GONE);
        p_sun = findViewById(R.id.Sun);
        p_mon = findViewById(R.id.Mon);
        p_tue = findViewById(R.id.Tue);
        p_wed = findViewById(R.id.Wed);
        p_thu = findViewById(R.id.Thu);
        p_fri = findViewById(R.id.Fri);
        p_sat = findViewById(R.id.Sat);

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

        topicSelected = "";
        incrementedTimeInMin = getIncrementedTimeByMin();

        bottomSheetBehavior.setDraggable(false);
        getTopicSelectedFromSharedPrefs();

        //time recording
        load_topic_items();
        first_data = Calendar.getInstance();

        current_View_list = new ArrayList<topic_item>();
        dateFormat = new SimpleDateFormat("EEE , dd MMM");
        update_currentView_list(Calendar.getInstance());
        listView_topics_recorded.setVisibility(View.GONE);
        select_previous_week.setVisibility(View.GONE);
        select_next_week.setVisibility(View.GONE);
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
                    //barChart.setVisibility(View.VISIBLE);
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
                    barChart_View.setVisibility(View.GONE);
                }
            }
        });

        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.on_click));
                //implement bottomSheet
                bottom_sheet_more bottom_sheet = new bottom_sheet_more();
                bottom_sheet.show(getSupportFragmentManager(), "bottomSheet_more");
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
        calendar = Calendar.getInstance();
        currentDate = Calendar.getInstance();
        current_day_display.setText(dateFormat.format(calendar.getTime()));

        select_next_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH, 7);

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
                    setData(calendar);
                }else if(difference > 0) {
                    //today
                    //day_reference.setText("Today");
                    calendar.add(Calendar.DAY_OF_MONTH, -7);

                }
                update_currentView_list(calendar);
                initalizeAdapter();

                Log.i("calendarTime", String.valueOf(calendar.getTime()));
            }
        });

        select_previous_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long cal_stack = calendar.getTimeInMillis();
                Log.i("calendarTime", String.valueOf(calendar.getTime()));
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                Log.i("calendarTime", String.valueOf(calendar.getTime()));
                if(calendar.getTimeInMillis() >= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong("first_data_insterted_time",
                        Calendar.getInstance().getTimeInMillis())) {
                    long difference = calendar.getTimeInMillis() - currentDate.getTimeInMillis();
                    int days_diff = (int) TimeUnit.MILLISECONDS.toDays(difference);
                    Log.i("prev", "if");

                    Log.i("daysDifference", String.valueOf(days_diff));

                    if(days_diff == -1){
                        //yesterday
                        day_reference.setText("Yesterday");
                    }else {
                        day_reference.setText("");
                    }
                    current_day_display.setText(dateFormat.format(calendar.getTime()));

                    update_currentView_list(calendar);
                    initalizeAdapter();

                    setData(calendar);
                }else{
                    calendar.setTimeInMillis(cal_stack);
                    Log.i("calendarTime", String.valueOf(calendar.getTime()));
                    Log.i("prev", "else");
                    current_day_display.setText(dateFormat.format(calendar.getTime()));
                    update_currentView_list(calendar);
                    initalizeAdapter();
                    Log.i("calendarTime", String.valueOf(calendar.getTime()));
                    //setData(calendar);

                }

                Log.i("cal_stack", String.valueOf(cal_stack));
            }
        });

        current_day_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_to_current_view();
            }
        });

        //BarGraph
        setData(Calendar.getInstance());

        final ArrayList<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thur");
        days.add("Fri");
        days.add("Sat");
        days.add("Sun");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void set_to_current_view() {
        current_day_display.setText(dateFormat.format(currentDate.getTime()));
        calendar.setTime(currentDate.getTime());

        day_reference.setText("Today");

        update_currentView_list(calendar);
        initalizeAdapter();

        set_bar_Color(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
        reset_other_barColor(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));

        setData(calendar);
    }

    private void resetTimer() {
        countDownTimer.cancel();

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void post_savedChanges() {
        Log.i("postChanges", "saved");
        MainActivity.topRight_option.setText("Set Timer");
        MainActivity.topLeft_option.setVisibility(View.INVISIBLE);
        MainActivity.incrementBy1min.setVisibility(View.INVISIBLE);
        MainActivity.incrementedTimeInMin = 0;
        storeTimePassed(0);

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
        //Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_LONG).show();
        return is_saved;
    }

    private Boolean get_is_reset() {
        Boolean is_reset = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("is_reset",
                false);
        Log.i("is_reset", String.valueOf(is_reset));
       // Toast.makeText(MainActivity.this, "is_reset", Toast.LENGTH_LONG).show();

        return is_reset;
    }

    private Boolean get_is_canceled() {
        Boolean is_canceled = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean("is_canceled",
                false);
        //poToast.makeText(MainActivity.this, "is_canceled", Toast.LENGTH_LONG).show();
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

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
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
        calendar.setTimeInMillis(calendar.getTimeInMillis() - getTimePassed());
        String date = dateFormat.format(calendar.getTime());
        topic_item topic_item = new topic_item(topicSelected, (totalSeconds_passed / 60), date , calendar.get(Calendar.DAY_OF_WEEK));

        Log.i("topicItemsSize", String.valueOf(topic_items.size()));
        if(topic_items.size() == 0) {
            first_data.setTime(calendar.getTime());
            first_data.add(Calendar.DAY_OF_MONTH, -1 * (first_data.get(Calendar.DAY_OF_WEEK) - 1));
            PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putLong("first_data_insterted_time",
                    first_data.getTimeInMillis()).commit();

            Log.i("firstTime", "added");
            Log.i("dateRecoded", String.valueOf(PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong("first_data_insterted_time",
                    0)));
        }

        Boolean found_item = false;
        for(int i = 0; i < topic_items.size(); i++) {
            if (topic_items.get(i).topic_name.equals(topic_item.topic_name) && topic_items.get(i).date_recorded.equals(topic_item.date_recorded)) {
                topic_items.get(i).time_recorded += totalSeconds_passed / 60;
                found_item = true;

                Log.i("updatedItem", topic_items.get(i).topic_name + "\n" + topic_items.get(i).date_recorded + "\n" + topic_items.get(i).day_recorded + "\n" + topic_items.get(i).time_recorded);
            }
        }

        if(!found_item) {
            if(topic_item.getTime_recorded() != 0) {
                topic_items.add(topic_item);
            }
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
       //animate barCharts
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setData(Calendar calendar){
        get_current_week_data(calendar);

        barEntries = new ArrayList<Long>();
        barEntries.clear();

        Log.i("set_crnt_wk", String.valueOf(current_Week_list));
        int max_total = 0;
        int c = 0;
        for(int i = 0; i < 7; i++){
            c = current_Week_list.get((6 - i));
            barEntries.add(c);
            Log.i("barEntr", String.valueOf(c) + " " + i);
            if(max_total < current_Week_list.get(i)) {
                max_total = current_Week_list.get(i);
            }
        }
        Log.i("max_total", String.valueOf(max_total));
        Log.i("barEntries", String.valueOf(barEntries));

        p_sun.setProgress(((Integer) barEntries.get(0) * 100) / (max_total + 10));
        p_mon.setProgress(((Integer) barEntries.get(1) * 100) / (max_total + 10));
        p_tue.setProgress(((Integer) barEntries.get(2) * 100) / (max_total + 10));
        p_wed.setProgress(((Integer) barEntries.get(3) * 100) / (max_total + 10));
        p_thu.setProgress(((Integer) barEntries.get(4) * 100) / (max_total + 10));
        p_fri.setProgress(((Integer) barEntries.get(5) * 100) / (max_total + 10));
        p_sat.setProgress(((Integer) barEntries.get(6) * 100) / (max_total + 10));

        int current_day_week_no = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        set_bar_Color(String.valueOf(current_day_week_no));

        //update_barLines
        TextView barLine_1 = findViewById(R.id.barLine_1);
        TextView barLine_2 = findViewById(R.id.barLine_2);
        TextView barLine_3 = findViewById(R.id.barLine_3);

        barLine_1.setText(getTime_formatted(max_total + 10));
        barLine_2.setText(getTime_formatted(((max_total + 10) * 2) / 3));
        barLine_3.setText(getTime_formatted((max_total + 10) / 3));
    }
    private String getTime_formatted(long minutes_recorded) {
        int hr = (int) minutes_recorded / 60;
        int minutes = (int) minutes_recorded % 60;

        String formatted_time = "";
        if(hr > 0) {
            formatted_time = String.valueOf(hr) + "h" + " " + String.valueOf(minutes) + "m";
        }else {
            formatted_time = String.valueOf(minutes) + "m";
        }

        return formatted_time;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void get_current_week_data(Calendar calendar) {
        int current_day_week_no = calendar.get(Calendar.DAY_OF_WEEK);
        long cal_stack = calendar.getTimeInMillis();
        Log.i("CurrentWeekNo", String.valueOf(current_day_week_no));
        //sunday = 1
        current_Week_list = new ArrayList<Integer>();
        current_Week_list.clear();
        // crnt_wk 1, day = day + 6
        // crnt wk 2, day = day + 5
        // crnt_wk 3, day = day + 4
        // crnt wk 4, day = day + 3
        // crnt_wk 5, day = day + 2
        // crnt wk 6, day = day + 1
        // crnt wk 7, day = day + 0

//        for(int i = 0; i <= 6; i++){
//            current_Week_list.add(0);
//        }

        int day_to_be_incremented = 7 - current_day_week_no;
        Log.i("before_Add", dateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_MONTH, day_to_be_incremented);
        Log.i("before_Add", dateFormat.format(calendar.getTime()));
        // finding the index of topic item, whose date = calendar selected
        // calculate the sum of all the values of that date and add to the current_Week_list array
        int days_total;
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        for(int j = 0; j < 7; j++){
            days_total = 0;
            calendar.add(Calendar.DAY_OF_MONTH, -1);

            for(int i = topic_items.size() - 1; i >= 0; i--){
                Log.i("date_recorded", String.valueOf(topic_items.get(i).date_recorded) + ", " + dateFormat.format(calendar.getTime()));
                if(topic_items.get(i).date_recorded.equals(dateFormat.format(calendar.getTime()))) {
                    days_total += topic_items.get(i).time_recorded;
                    Log.i("time_recorded", String.valueOf(topic_items.get(i).time_recorded));
                }
            }
            Log.i("date_now", dateFormat.format(calendar.getTime()) + ",   " + j + ", " + days_total);
            current_Week_list.add(j, days_total);
        }

        calendar.setTimeInMillis(cal_stack);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("WrongConstant")
    private void collapse_bottomSheet(BottomSheetBehavior bottomSheetBehavior, View bottomSheet) {
        bottomSheetBehavior.setState(4);

        back_btn.setVisibility(View.GONE);
        bottomSheet.setBackground(getResources().getDrawable(R.drawable.curved_layout));
        coordinatorLayout.setVisibility(View.VISIBLE);
        listView_topics_recorded.setVisibility(View.GONE);
        select_next_week.setVisibility(View.GONE);
        select_previous_week.setVisibility(View.GONE);
        current_day_display.setVisibility(View.GONE);
        barChart_View.setVisibility(View.GONE);

        set_to_current_view();

        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        getWindow().getDecorView().setSystemUiVisibility(0);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"WrongConstant", "ClickableViewAccessibility"})
    private void expand_bottomSheet(BottomSheetBehavior bottomSheetBehavior, View bottomSheet) {
        bottomSheetBehavior.setState(3);

        back_btn.setVisibility(View.VISIBLE);
        bottomSheet.setBackground(getResources().getDrawable(R.drawable.rectangle_layout));
        coordinatorLayout.setVisibility(View.GONE);
        listView_topics_recorded.setVisibility(View.VISIBLE);
        select_next_week.setVisibility(View.VISIBLE);
        select_previous_week.setVisibility(View.VISIBLE);
        current_day_display.setVisibility(View.VISIBLE);
        barChart_View.setVisibility(View.VISIBLE);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        update_currentView_list(Calendar.getInstance());
        initalizeAdapter();

        setData(Calendar.getInstance());
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
        Gson gson = new Gson();
        String json = gson.toJson(topic_items);

        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString("topic_items",
                json).commit();
    }

    private void load_topic_items() {
        Gson gson = new Gson();
        String json =  PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("topic_items",
                null);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void barClicked(View view) {
        set_bar_Color(view.getTag().toString());
        reset_other_barColor(view.getTag().toString());

        int crnt_wk_num = calendar.get(Calendar.DAY_OF_WEEK);
        int barSelected_wk =   Integer.parseInt(view.getTag().toString());

        reselect_week_no(barSelected_wk - crnt_wk_num);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void reselect_week_no(int i) {
        calendar.add(Calendar.DAY_OF_MONTH, i);

        long difference = calendar.getTimeInMillis() - currentDate.getTimeInMillis();
        int days_diff = (int) TimeUnit.MILLISECONDS.toDays(difference);
        Log.i("daysDifference", String.valueOf(days_diff));

        Log.i("firstTime", String.valueOf(PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong("first_data_insterted_time",
                0)));
        if(calendar.getTimeInMillis() >= PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getLong("first_data_insterted_time",
                0)) {
            if(difference <= 0) {
                current_day_display.setText(dateFormat.format(calendar.getTime()));
                if(days_diff == 0){
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
                set_bar_Color(String.valueOf(currentDate.get(Calendar.DAY_OF_WEEK)));
            }
            update_currentView_list(calendar);
            initalizeAdapter();
        } else{
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            set_bar_Color(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
            reset_other_barColor(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));

            Log.i("reachedEnd", String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void set_bar_Color(String tag) {
        if(tag.equals("1")) {
            p_sun.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }else if(tag.equals("2")) {
            p_mon.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }else if(tag.equals("3")) {
            p_tue.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }else if(tag.equals("4")) {
            p_wed.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }else if(tag.equals("5")) {
            p_thu.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }else if(tag.equals("6")) {
            p_fri.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }else if(tag.equals("7")) {
            p_sat.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void reset_other_barColor(String tag) {
        Log.i("view.getTag", tag);
        if(!tag.equals("1")) {
            p_sun.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.low_primary)));
        }
        if(!tag.equals("2")) {
            p_mon.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.low_primary)));
        }
        if(!tag.equals("3")) {
            p_tue.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.low_primary)));
        }
        if(!tag.equals("4")) {
            p_wed.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.low_primary)));
        }
        if(!tag.equals("5")) {
            p_thu.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.low_primary)));
        }
        if(!tag.equals("6")) {
            p_fri.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.low_primary)));
        }
        if(!tag.equals("7")) {
            p_sat.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.low_primary)));
        }
    }

    @Override
    public void onButtonClick(String text) {
        //bottomSheet
        if(text.equals("Suggestions")) {
            Toast.makeText(this, "Suggestions", Toast.LENGTH_SHORT).show();
            Intent mailIntent = new Intent(Intent.ACTION_VIEW);

            //initialse the values for the mail
            Uri data = Uri.parse("mailto:?subject=" + "Tamarind App suggestion"+ "&body=" + "Your Suggestions" + "&to=" + "mmchinmay555@gmail.com");

            mailIntent.setData(data);
            startActivity(Intent.createChooser(mailIntent, "Send mail..."));
        }
    }
}
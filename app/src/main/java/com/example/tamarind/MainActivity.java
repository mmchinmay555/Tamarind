package com.example.tamarind;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.media.TimedText;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    ImageView menu_btn, more_btn, back_btn;

    long timerRunning;
    long totalSeconds_passed;

    static String topicSelected;

    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout, appbar;
    LinearLayout total_time, bottom_layout;

    TextView topLeft_option, topRight_option;
    TextView incrementBy1min;
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


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        TimeSetter.seconds = 50 * 60;
        TimeSetter.breakSeconds = 4 * 60;

        seconds = TimeSetter.seconds;
        breakSeconds = TimeSetter.breakSeconds;
        totalSeconds_passed = 0;

        Log.i("secondsLeft", String.valueOf(getMillisLeftInSharedPrefs()));
        Log.i("timerRunning", String.valueOf(getTimerState()));
        Log.i("BreakState", String.valueOf(getBreakState()));
        Log.i("timePassed", String.valueOf(getTimePassed()));
        Log.i("endTimeInMillis", String.valueOf(getEndTimeInMillis()));

        isBreak = getBreakState();
        back_btn.setVisibility(View.GONE);
        incrementBy1min.setVisibility(View.INVISIBLE);
        barChart.setVisibility(View.GONE);

        topicSelected = "";

        bottomSheetBehavior.setDraggable(false);
        getTopicSelectedFromSharedPrefs();

        if(topicSelected.isEmpty()){
            topic_btn.setVisibility(View.INVISIBLE);
            Log.i("topicSelected", "null");
        }else{
            if(!isBreak) {
                topic_btn.setText(topicSelected);
            }else{
                topic_btn.setText("Break");
            }

            topic_btn.setVisibility(View.VISIBLE);
            Log.i("topicSelected 115", topicSelected);
        }

        timerRunning = 0;
        if(getTimerState() == 1) {
            Calendar calendar = Calendar.getInstance();
            long currTimeInMillis = calendar.getTimeInMillis();

            long secondsLeft = (getEndTimeInMillis() - currTimeInMillis) / 1000;
            startTimer((int) secondsLeft);
            totalSeconds_passed = getTimePassed();
        }else{
            long secondsLeft = getMillisLeftInSharedPrefs();
            Log.i("SecondsLeft", String.valueOf(secondsLeft));
            if(isBreak){
                breakSeconds = secondsLeft;
            }else{
                seconds = secondsLeft;
            }

            timerTextSet(secondsLeft);
            totalSeconds_passed = getTimePassed();
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

                seconds = TimeSetter.seconds;
                breakSeconds = TimeSetter.breakSeconds;
            }
        });

        topRight_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topRight_option.getText().toString().equals("Save")){
                    save_time();

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
                }

            }
        });

        topic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timerRunning == 1){

                }else if(timerRunning == 0){
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
                seconds = seconds + 61;
                countDownTimer.cancel();
                timerRunning = 0;
                startTimer((int) seconds);
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

                storeBreakState(isBreak);

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
                            totalSeconds_passed += 1;
                            storeTimePassed(totalSeconds_passed);
                            storeMillisLeftInSharedPrefs(seconds);
                        }else{
                            storeMillisLeftInSharedPrefs(breakSeconds);
                        }

                        storeEndTimeInMillis(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
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
                            //isBreak = false;
                            if(!topicSelected.equals("null")){
                                topic_btn.setText(topicSelected);
                                isBreak = false;
                            }else {
                                startActivity(new Intent(MainActivity.this, TopicsList.class));
                            }
                            Log.i("BReak", String.valueOf(isBreak));
                        }
                    }
                }.start();
                timerRunning = 1;

                storeTimerState(timerRunning);
            }else if(timerRunning == 1){
                incrementBy1min.setVisibility(View.INVISIBLE);
                countDownTimer.cancel();

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

    private void storeTimePassed(long totalSeconds_passed) {
        SharedPreferences sharedPreferences = getSharedPreferences("totalTimePassed", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("timePassed", totalSeconds_passed);
        editor.apply();
    }

    private long getTimePassed() {
        SharedPreferences sharedPreferences = getSharedPreferences("totalTimePassed", MODE_PRIVATE);

        long timePassed = sharedPreferences.getLong("timePassed", 0);
        return timePassed;
    }

    private void storeBreakState(Boolean isBreak) {
        SharedPreferences sharedPreferences = getSharedPreferences("breakState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isBreak", isBreak);
        editor.apply();
    }

    private Boolean getBreakState() {
        SharedPreferences sharedPreferences = getSharedPreferences("breakState", MODE_PRIVATE);
        Boolean isBreak = sharedPreferences.getBoolean("isBreak", false);

        return isBreak;
    }

    private void storeTimerState(long timerRunning) {
        SharedPreferences sharedPreferences = getSharedPreferences("timerState", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("timerState", timerRunning);
        editor.apply();
    }

    private long getTimerState() {
        SharedPreferences sharedPreferences = getSharedPreferences("timerState", MODE_PRIVATE);

        long timerState = sharedPreferences.getLong("timerState", 0);
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
    private void storeMillisLeftInSharedPrefs(long seconds) {
        SharedPreferences sharedPreferences = getSharedPreferences("millisLeft", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong("millisLeft", seconds);
        editor.apply();
    }

    private long getMillisLeftInSharedPrefs(){
        SharedPreferences sharedPreferences = getSharedPreferences("millisLeft", MODE_PRIVATE);
        long millisLeft = sharedPreferences.getLong("millisLeft", 0);

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

    private void timerTextSet(long seconds) {
        int hrs = (int) ((seconds % 86400 ) / 3600);
        int mins = (int) (((seconds % 86400 ) % 3600 ) / 60);
        int sec = (int) (seconds % 60);

        if(hrs == 0 && mins == 0){
            MainActivity.timerHr.setVisibility(View.GONE);
            MainActivity.colonM.setVisibility(View.GONE);
            MainActivity.timerMin.setVisibility(View.VISIBLE);
            MainActivity.colonS.setVisibility(View.VISIBLE);
            MainActivity.timerSec.setVisibility(View.VISIBLE);
            MainActivity.timerMin.setText("50");
        }else if(hrs == 0 && mins != 0){
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
        storeMillisLeftInSharedPrefs(0);
        storeTimerState(0);
    }

    private void setTimer() {
        startActivity(new Intent(MainActivity.this, TimeSetter.class));
    }

    private void cancel_time() {
        if(isBreak){
            isBreak = false;
            if(!topicSelected.equals("null")){
                topic_btn.setText(topicSelected);
            }else{
                startActivity(new Intent(MainActivity.this, TopicsList.class));
            }
        }
        countDownTimer.cancel();
        countDownTimer.onFinish();

        totalSeconds_passed = 0;
        storeTimePassed(totalSeconds_passed);

        storeBreakState(isBreak);
        storeMillisLeftInSharedPrefs(0);
        storeTimerState(0);
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
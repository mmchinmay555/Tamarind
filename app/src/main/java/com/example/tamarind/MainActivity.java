package com.example.tamarind;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout, appbar;
    LinearLayout total_time, bottom_layout;

    TextView topLeft_option, topRight_option;
    static TextView topic_btn;
    static TextView timer;

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimeSetter.seconds = 50 * 60; //50 mins

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

        back_btn.setVisibility(View.GONE);

        bottomSheetBehavior.setDraggable(false);
        barChart.setVisibility(View.GONE);

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
            }
        });

        topRight_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(topRight_option.getText().toString().equals("Save")){
                    save_time();
                }else if(topRight_option.getText().toString().equals("Set Timer")){
                    setTimer();
                }
            }
        });

        topic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.on_click));
                startActivity(new Intent(MainActivity.this, TopicsList.class));
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

    private void save_time() {
    }

    private void setTimer() {
        startActivity(new Intent(MainActivity.this, TimeSetter.class));
    }

    private void cancel_time() {
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
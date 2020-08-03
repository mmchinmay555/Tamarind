package com.example.tamarind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class TimeSetter extends AppCompatActivity {

    ImageView back_btn;
    TextView one, two, three, four, five, six, seven, eight, nine, zero;
    TextView hour_ones, minute_tenth, minute_ones;
    TextView setTime_btn;

    static int seconds;
    static int breakSeconds;
    int timeSet_status;

    FloatingActionButton clear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setter);

//        0 not set
//        1 min_ones set
//        2 min_tenth set
//        3 hr_ones_set
        timeSet_status = 0;

        back_btn = findViewById(R.id.back_btn_timeSetter);
        clear = findViewById(R.id.clear_btn);
        setTime_btn = findViewById(R.id.setTime_btn);

        //initialising numbers
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);
        zero = findViewById(R.id.zero);

        hour_ones = findViewById(R.id.hour_ones);
        minute_ones = findViewById(R.id.minute_ones);
        minute_tenth = findViewById(R.id.minute_tenth);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to MainActivity
                finish();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear time
                if(timeSet_status == 1){
                    minute_ones.setText("0");

                    timeSet_status = 0;
                }else if(timeSet_status == 2){
                    minute_ones.setText(minute_tenth.getText());
                    minute_tenth.setText("0");

                    timeSet_status = 1;
                }else if(timeSet_status == 3){
                    minute_ones.setText(minute_tenth.getText());
                    minute_tenth.setText(hour_ones.getText());
                    hour_ones.setText("0");

                    timeSet_status = 2;
                }
            }
        });

        setTime_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isBreak){
                    if(get_seconds() > 59){
                        seconds = get_seconds();
                        StoreSeconds(seconds);
                    }else{
                        finish();
                    }
                }else{
                    if(get_seconds() > 59){
                        breakSeconds = get_seconds();
                        StoreBreakSeconds(breakSeconds);
                    }else{
                        finish();
                    }
                }

                if(get_seconds() == 0){
                    finish();
                }

                if(get_seconds() < 10800){
                    //only time less than 3hr is valid

                    int hrs = (get_seconds() % 86400 ) / 3600;
                    int mins = ((get_seconds() % 86400 ) % 3600 ) / 60;

                    if(hrs == 0 && mins == 0){
                        MainActivity.timerHr.setVisibility(View.GONE);
                        MainActivity.colonM.setVisibility(View.GONE);
                        MainActivity.timerMin.setVisibility(View.VISIBLE);
                        MainActivity.colonS.setVisibility(View.VISIBLE);
                        MainActivity.timerSec.setVisibility(View.VISIBLE);
                    }else if(hrs == 0 && mins != 0){
                        MainActivity.timerHr.setVisibility(View.GONE);
                        MainActivity.colonM.setVisibility(View.GONE);
                        MainActivity.timerMin.setVisibility(View.VISIBLE);
                        MainActivity.colonS.setVisibility(View.VISIBLE);
                        MainActivity.timerSec.setVisibility(View.VISIBLE);
                        MainActivity.timerMin.setText(String.valueOf(mins));
                        MainActivity.timerSec.setText("00");

                    }else if(hrs != 0 && mins < 10){
                        MainActivity.timerHr.setVisibility(View.VISIBLE);
                        MainActivity.colonM.setVisibility(View.VISIBLE);
                        MainActivity.timerMin.setVisibility(View.VISIBLE);
                        MainActivity.colonS.setVisibility(View.VISIBLE);
                        MainActivity.timerSec.setVisibility(View.VISIBLE);
                        MainActivity.timerHr.setText(String.valueOf(hrs));
                        MainActivity.timerMin.setText("0" + String.valueOf(mins));
                        MainActivity.timerSec.setText("00");
                    }else if(hrs != 0 && mins >= 10){
                        MainActivity.timerHr.setVisibility(View.VISIBLE);
                        MainActivity.colonM.setVisibility(View.VISIBLE);
                        MainActivity.timerMin.setVisibility(View.VISIBLE);
                        MainActivity.colonS.setVisibility(View.VISIBLE);
                        MainActivity.timerSec.setVisibility(View.VISIBLE);
                        MainActivity.timerHr.setText(String.valueOf(hrs));
                        MainActivity.timerMin.setText(String.valueOf(mins));
                        MainActivity.timerSec.setText("00");
                    }

                    if(MainActivity.isBreak){
                        MainActivity.breakSeconds = breakSeconds;
                    }else{
                        MainActivity.seconds = seconds;
                    }
                    finish();
                }else{
                    show_snackbar("It is not safe over 3 hrs", zero);
                }
            }
        });
    }

    private void show_snackbar(String s, View v) {
        Snackbar.make(v, s, Snackbar.LENGTH_SHORT)
                .setTextColor(getResources().getColor(R.color.white))
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .show();
    }
    private void StoreBreakSeconds(int seconds) {
        PreferenceManager.getDefaultSharedPreferences(TimeSetter.this).edit().putInt("breakSeconds",
                seconds).commit();
    }

    private void StoreSeconds(int seconds) {
        PreferenceManager.getDefaultSharedPreferences(TimeSetter.this).edit().putInt("seconds",
                seconds).commit();
    }

    private int get_seconds() {
        int min_ones = Integer.parseInt(minute_ones.getText().toString()) * 60;
        int min_tenth = Integer.parseInt(minute_tenth.getText().toString()) * 10 * 60;
        int hr_ones = Integer.parseInt(hour_ones.getText().toString()) * 60 * 60;

        int sec = min_ones + min_tenth + hr_ones;
        return sec;
    }

    public void On_num_Clicked(View view) {
        if(timeSet_status == 0){
            minute_ones.setText(view.getTag().toString());

            timeSet_status = 1;
        }else if(timeSet_status == 1){
            minute_tenth.setText(minute_ones.getText());
            minute_ones.setText(view.getTag().toString());

            timeSet_status = 2;
        }else if(timeSet_status == 2){
            hour_ones.setText(minute_tenth.getText());
            minute_tenth.setText(minute_ones.getText());
            minute_ones.setText(view.getTag().toString());

            timeSet_status = 3;
        }
    }
}
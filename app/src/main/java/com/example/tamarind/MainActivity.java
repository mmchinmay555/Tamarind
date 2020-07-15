package com.example.tamarind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior bottomSheetBehavior;
    ImageView menu_btn, more_btn, back_btn;

    View maximize;
    androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout, appbar;
    LinearLayout total_time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View bottomSheet = findViewById(R.id.bottomSheet);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        menu_btn = findViewById(R.id.menu_button);
        more_btn = findViewById(R.id.more_btn);
        coordinatorLayout = findViewById(R.id.app_bar);
        appbar = findViewById(R.id.topbar);
        total_time = findViewById(R.id.time_display);
        back_btn = findViewById(R.id.back_button);
        maximize = findViewById(R.id.maximize);

        back_btn.setVisibility(View.GONE);

        Log.i("Status Bar", String.valueOf(getWindow().getDecorView().getSystemUiVisibility()));

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {
                Log.i("State", String.valueOf(bottomSheetBehavior.getState()));
                if(String.valueOf(bottomSheetBehavior.getState()).equals("3")){
                    //total_time.setPadding(0, 20, 0, 0);
                    back_btn.setVisibility(View.VISIBLE);
                    maximize.setVisibility(View.GONE);
                    bottomSheetBehavior.setDraggable(false);

                    bottomSheet.setBackground(getResources().getDrawable(R.drawable.rectangle_layout));
                    coordinatorLayout.setVisibility(View.GONE);

                    getWindow().setStatusBarColor(getResources().getColor(R.color.white));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                    back_btn.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onClick(View v) {
                            bottomSheetBehavior.setState(4);

                            bottomSheet.setBackground(getResources().getDrawable(R.drawable.curved_layout));

                            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                            getWindow().getDecorView().setSystemUiVisibility(0);

                            coordinatorLayout.setVisibility(View.VISIBLE);
                            back_btn.setVisibility(View.GONE);
                            maximize.setVisibility(View.VISIBLE);

                        }
                    });

                }else if(String.valueOf(bottomSheetBehavior.getState()).equals("2")){
                    bottomSheet.setBackground(getResources().getDrawable(R.drawable.rectangle_layout));
                    bottomSheetBehavior.setDraggable(false);

                    coordinatorLayout.setVisibility(View.GONE);
                    back_btn.setVisibility(View.VISIBLE);
                    maximize.setVisibility(View.GONE);
                }else if(String.valueOf(bottomSheetBehavior.getState()).equals("4")){
                    bottomSheetBehavior.setDraggable(true);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                if(bottomSheetBehavior.isDraggable()){
//                    total_time.setPadding(0, 40, 0, 0);
//                    bottomSheet.setBackground(getResources().getDrawable(R.drawable.rectangle_layout));
//                    coordinatorLayout.setVisibility(View.GONE);
//                }else{
//                    total_time.setPadding(0, 0, 0, 0);
//                    bottomSheet.setBackground(getResources().getDrawable(R.drawable.curved_layout));
//                    coordinatorLayout.setVisibility(View.VISIBLE);
//
//                }
            }
        });



    }

    private void change_background(int state, View bottomSheet) {


        if(state == 1){

        }
    }
}
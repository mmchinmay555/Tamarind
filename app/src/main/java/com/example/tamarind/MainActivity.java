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

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {
                Log.i("State", String.valueOf(bottomSheetBehavior.getState()));
                if(String.valueOf(bottomSheetBehavior.getState()).equals("1")){

                    bottomSheet.setBackground(getResources().getDrawable(R.drawable.rectangle_layout));
                    total_time.setPadding(0, 0, 0, 0);
                    coordinatorLayout.setVisibility(View.GONE);

                }else if(String.valueOf(bottomSheetBehavior.getState()).equals("4")){

                    bottomSheet.setBackground(getResources().getDrawable(R.drawable.curved_layout));
                    appbar.setVisibility(View.GONE);
                    total_time.setPadding(0, 0, 0, 0);
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }else if(String.valueOf(bottomSheetBehavior.getState()).equals("3")){
                    total_time.setPadding(0, 40, 0, 0);
                    appbar.setVisibility(View.VISIBLE);

                    back_btn.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onClick(View v) {
                            bottomSheetBehavior.setState(4);
                            bottomSheet.setBackground(getResources().getDrawable(R.drawable.curved_layout));
                            appbar.setVisibility(View.GONE);
                            total_time.setPadding(0, 0, 0, 0);
                            coordinatorLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });



    }
}
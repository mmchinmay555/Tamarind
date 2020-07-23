package com.example.tamarind;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.sql.Time;
import java.util.ArrayList;

import static com.example.tamarind.MainActivity.seconds;
import static com.example.tamarind.MainActivity.topicSelected;
import static com.example.tamarind.MainActivity.topics;

public class taskItemAdapter extends ArrayAdapter<topic_item> {
    public taskItemAdapter(@NonNull Context context, ArrayList<topic_item> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final topic_item item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.topic_view, parent, false);
        }

        final TextView topicName = convertView.findViewById(R.id.topicName);
        topicName.setText(item.topicName);

        topicName.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                TopicsList.selectedTopic = item.topicName;
                MainActivity.isBreak = false;
                seconds = TimeSetter.seconds;

                setTimeText((int) seconds);
                MainActivity.topicSelected = item.topicName;
                Snackbar.make(v, MainActivity.topicSelected, Snackbar.LENGTH_SHORT)
                        .setTextColor(getContext().getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .show();
            }
        });

        return convertView;
    }

    private void setTimeText(int s) {
        int hrs = (int) ((s % 86400 ) / 3600);
        int mins = (int) (((s % 86400 ) % 3600 ) / 60);

        if(hrs == 0 && mins == 0){
            MainActivity.timerHr.setVisibility(View.GONE);
            MainActivity.colonM.setVisibility(View.GONE);
            MainActivity.timerMin.setVisibility(View.VISIBLE);
            MainActivity.colonS.setVisibility(View.VISIBLE);
            MainActivity.timerSec.setVisibility(View.VISIBLE);
            MainActivity.timerMin.setText("50");
            MainActivity.timerSec.setText("00");
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
    }
}


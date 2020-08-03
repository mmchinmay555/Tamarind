package com.example.tamarind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class topicItems__Adapter extends ArrayAdapter<topic_item> {
    public topicItems__Adapter(@NonNull Context context, ArrayList<topic_item> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final topic_item item = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tracked_topic_time, parent, false);
        }
        final TextView topicName = convertView.findViewById(R.id.tracked_topicName);
        final ProgressBar tracked_progress = convertView.findViewById(R.id.progressBar_topicView);
        final TextView tracked_topicTime = convertView.findViewById(R.id.tracked_topicTime);

        topicName.setText(item.topic_name);
        tracked_topicTime.setText(item.time_recorded + "m");
        tracked_progress.setProgress(getProgress_value(item.time_recorded));

        return convertView;
    }

    private int getProgress_value(long time_recorded) {
        return (int) ((time_recorded * 100)/MainActivity.topic_time_max_progress);
    }
}

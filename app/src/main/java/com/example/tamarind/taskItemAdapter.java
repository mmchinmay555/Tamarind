package com.example.tamarind;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;

public class taskItemAdapter extends ArrayAdapter<topic_item> {
    public taskItemAdapter(@NonNull Context context, ArrayList<topic_item> resource) {
        super(context, 0, resource);
    }

    int previousSelected = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
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

                Snackbar.make(v, item.topicName, Snackbar.LENGTH_SHORT)
                        .setTextColor(getContext().getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .show();
            }
        });



        return convertView;
    }
}


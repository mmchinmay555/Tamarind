package com.example.tamarind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class TopicsList extends AppCompatActivity {
    public static String selectedTopic;
    ImageView back_btn;
    FloatingActionButton addText_btn;
    EditText newTopic_addText;
    ListView listView_topics;

    static ArrayList<topic_item> topics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics_list);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        back_btn = findViewById(R.id.back_btn_topicList);
        addText_btn = findViewById(R.id.addText_btn);
        newTopic_addText = findViewById(R.id.newTopic_addText);
        listView_topics = (ListView) findViewById(R.id.listView_topics);

        loadTopics();
        initalizeAdapter();

        selectedTopic = MainActivity.topic_btn.getText().toString();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.topic_btn.setText(selectedTopic);
                finish();
            }
        });

        addText_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add text to the topic list
                if(!newTopic_addText.getText().toString().isEmpty()){
                    addTopic(newTopic_addText.getText().toString());
                    loadTopics();
                    initalizeAdapter();

                    Log.i("newTopic", newTopic_addText.getText().toString());
                    newTopic_addText.setText("");
                }
            }
        });
    }

    private void addTopic(String s) {
        topic_item item = new topic_item(s);
        topics.add(item);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(topics);

        editor.putString("topicsList", json);
        editor.apply();

        listView_topics.smoothScrollByOffset(topics.size());
    }

    private void loadTopics() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("topicsList", null);

        Type type = new TypeToken<ArrayList<topic_item>>(){}.getType();
        topics = gson.fromJson(json, type);

        if (topics == null) {
            topics = new ArrayList<>();
        }
    }

    private void initalizeAdapter() {
        taskItemAdapter adapter = new taskItemAdapter(this, topics);
        listView_topics.setAdapter(adapter);
        listView_topics.setDivider(null);
    }

}
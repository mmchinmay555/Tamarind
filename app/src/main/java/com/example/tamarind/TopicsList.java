package com.example.tamarind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.tamarind.MainActivity.breakSeconds;
import static com.example.tamarind.MainActivity.isBreak;
import static com.example.tamarind.MainActivity.seconds;
import static com.example.tamarind.MainActivity.topics;

public class TopicsList extends AppCompatActivity {
    public static String selectedTopic;
    ImageView back_btn;
    FloatingActionButton addText_btn;
    EditText newTopic_addText;
    ListView listView_topics;
    TextView breakTime;

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
        breakTime = findViewById(R.id.breakTime);

        loadTopics();
        initalizeAdapter();

        selectedTopic = MainActivity.topic_btn.getText().toString();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((topics.size() > 0 && !selectedTopic.equals("null")) || isBreak) {
                    if(isBreak){
                        MainActivity.topic_btn.setText("Break");
                        MainActivity.topic_btn.setVisibility(View.VISIBLE);
                        MainActivity.timerTextSet(breakSeconds);
                    }else{
                        MainActivity.topic_btn.setText(selectedTopic);
                        MainActivity.topicSelected = selectedTopic;
                        MainActivity.topic_btn.setVisibility(View.VISIBLE);
                        MainActivity.timerTextSet(seconds);
                    }
                    storeBreakState(isBreak);
                    saveTopicInSharedPrefs(selectedTopic);
                    finish();
                }else{
                    Snackbar.make(newTopic_addText, "Topic can't be empty", Snackbar.LENGTH_SHORT)
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .show();
                }
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

        breakTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isBreak = true;
                MainActivity.breakSeconds = TimeSetter.breakSeconds;

                setTimeText((int) breakSeconds);

                Snackbar.make(newTopic_addText, "Break", Snackbar.LENGTH_SHORT)
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .show();
            }
        });
    }

    private void addTopic(String s) {
        topic_item_forList item = new topic_item_forList(s);
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

        Type type = new TypeToken<ArrayList<topic_item_forList>>(){}.getType();
        topics = gson.fromJson(json, type);

        if (topics == null) {
            topics = new ArrayList<>();
        }
    }

    private void initalizeAdapter() {
        topicItem_forList_Adapter adapter = new topicItem_forList_Adapter(this, topics);
        listView_topics.setAdapter(adapter);
        listView_topics.setDivider(null);
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
    private void storeBreakState(Boolean break_state) {
        PreferenceManager.getDefaultSharedPreferences(TopicsList.this).edit().putBoolean("breakState",
                break_state).commit();
    }

    private void saveTopicInSharedPrefs(String sharedPrefsTopicSelected) {
        PreferenceManager.getDefaultSharedPreferences(TopicsList.this).edit().putString("sharedPrefsTopicSelected",
                sharedPrefsTopicSelected).commit();
    }
}
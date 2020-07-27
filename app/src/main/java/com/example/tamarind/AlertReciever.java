package com.example.tamarind;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import static com.example.tamarind.MainActivity.isBreak;
import static com.example.tamarind.MainActivity.topicSelected;

public class AlertReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TIME UP", "!");
        Log.i("topicSelected", intent.getStringExtra("topicSelected"));
        Log.i("isBreak", String.valueOf(intent.getBooleanExtra("isBreak", false)));
        Log.i("timePassed", String.valueOf(MainActivity.totalSeconds_passed));

        Intent in = new Intent();
        in.setClass(context, TimeUpActivity.class); //Test is a dummy class name where to redirect
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        in.putExtra("topicSelected", intent.getStringExtra("topicSelected"));
        in.putExtra("isBreak", intent.getBooleanExtra("isBreak", false));
        in.putExtra("time_recorded", MainActivity.totalSeconds_passed);

        context.startActivity(in);

        Log.i("Note", "Alert Recieved");
        Toast.makeText(context, "Recieved!!", Toast.LENGTH_LONG).show();
    }
}

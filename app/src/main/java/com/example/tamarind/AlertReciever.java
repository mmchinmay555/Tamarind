package com.example.tamarind;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlertReciever extends BroadcastReceiver {
    @SuppressLint("StaticFieldLeak")
    private static NotificationManagerCompat notificationManager;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);
        Intent fullScreenIntent = new Intent(context, TimeUpActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, appNotification.CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_arrow_down)
                        .setContentTitle("Time Up!")
                        .setContentText("Tamarind")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)

                        .setFullScreenIntent(fullScreenPendingIntent, true);

        Notification incomingCallNotification = notificationBuilder.build();
        incomingCallNotification.flags = Notification.FLAG_INSISTENT;
        context.startForegroundService(fullScreenIntent);

        notificationManager.notify(1, incomingCallNotification);
    }
}

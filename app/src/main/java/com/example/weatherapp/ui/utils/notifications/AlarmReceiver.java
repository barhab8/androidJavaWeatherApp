package com.example.weatherapp.ui.utils.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm triggered! Showing notification...");

        // Show the notification
        String message = intent.getStringExtra("notification_message");
        if (message == null) {
            message = "Don't forget to check the weather today!";
        }
        NotificationHelper.showWeatherNotification(context, message);

        // Reschedule for the next day
        NotificationScheduler.rescheduleNextDay(context);
    }
}

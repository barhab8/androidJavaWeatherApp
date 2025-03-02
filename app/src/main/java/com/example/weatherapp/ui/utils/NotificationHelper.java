package com.example.weatherapp.ui.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.activitys.MainScreenActivity;

public class NotificationHelper {

    private static final String TAG = "notification";
    private static final String CHANNEL_ID = "WEATHER_CHANNEL";

    public static void showWeatherNotification(Context context) {
        Log.d(TAG, "Attempting to show notification...");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android 8+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Weather Alerts", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for checking the weather");
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created.");
        }

        // Intent to open MainScreenActivity when notification is clicked
        Intent intent = new Intent(context, MainScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_weather) // Ensure you have an icon in the drawable folder
                .setContentTitle("Check the Weather!")
                .setContentText("Don't forget to check the weather today.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        notificationManager.notify(1, builder.build());

        Log.d(TAG, "Notification should be displayed now.");
    }
}

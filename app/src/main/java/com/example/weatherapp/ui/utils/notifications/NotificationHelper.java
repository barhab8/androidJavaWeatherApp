package com.example.weatherapp.ui.utils.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.activitys.MainScreenActivity;
import com.example.weatherapp.ui.activitys.SplashScreenActivity;

public class NotificationHelper {
    private static final String TAG = "notification";
    private static final String CHANNEL_ID = "WEATHER_CHANNEL";

    public static void showWeatherNotification(Context context, String message) {
        Log.d(TAG, "Attempting to show notification...");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Weather Alerts", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for checking the weather");
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created.");
        }

        // Intent to open MainScreenActivity when clicked
        Intent intent = new Intent(context, SplashScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_weather)
                .setContentTitle("Weather Update 🌤")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Unique notification ID
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());

        Log.d(TAG, "Notification displayed: " + message);
    }
}

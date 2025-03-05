package com.example.weatherapp.ui.activitys;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.weatherapp.R;
import com.example.weatherapp.ui.utils.notifications.NotificationScheduler;

public class NotificationSettingsActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button btnSetNotification, btnCancelNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notification Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        timePicker = findViewById(R.id.timePicker);
        btnSetNotification = findViewById(R.id.btnSetNotification);
        btnCancelNotification = findViewById(R.id.btnCancelNotification);

        btnSetNotification.setOnClickListener(v -> setDailyNotification());
        btnCancelNotification.setOnClickListener(v -> cancelNotification());
    }

    private void setDailyNotification() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        NotificationScheduler.setDailyNotification(this, hour, minute);
        Toast.makeText(this, "Daily Notification Set!", Toast.LENGTH_SHORT).show();
    }

    private void cancelNotification() {
        NotificationScheduler.cancelNotification(this);
        Toast.makeText(this, "Notification Canceled!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

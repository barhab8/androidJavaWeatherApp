package com.example.weatherapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.weatherapp.R;
import com.example.weatherapp.ui.utils.notifications.NotificationScheduler;
import com.google.android.material.button.MaterialButton;

public class NotificationSettingsFragment extends Fragment {

    private TimePicker timePicker;
    private MaterialButton btnSetNotification, btnCancelNotification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_settings, container, false);

        timePicker = view.findViewById(R.id.timePicker);
        btnSetNotification = view.findViewById(R.id.btnSetNotification);
        btnCancelNotification = view.findViewById(R.id.btnCancelNotification);

        btnSetNotification.setOnClickListener(v -> setDailyNotification());
        btnCancelNotification.setOnClickListener(v -> cancelNotification());

        return view;
    }

    private void setDailyNotification() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        NotificationScheduler.setDailyNotification(requireContext(), hour, minute);
        Toast.makeText(requireContext(), "Daily Notification Set!", Toast.LENGTH_SHORT).show();
    }

    private void cancelNotification() {
        NotificationScheduler.cancelNotification(requireContext());
        Toast.makeText(requireContext(), "Notification Canceled!", Toast.LENGTH_SHORT).show();
    }
}

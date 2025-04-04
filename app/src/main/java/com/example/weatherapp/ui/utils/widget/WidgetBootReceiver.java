package com.example.weatherapp.ui.utils.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WidgetBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            WidgetUpdateService.scheduleUpdate(context);
        }
    }
}

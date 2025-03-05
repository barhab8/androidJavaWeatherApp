package com.example.weatherapp.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.weatherapp.ui.fragments.NotificationSettingsFragment;
import com.example.weatherapp.ui.fragments.SystemSettingsFragment;
import com.example.weatherapp.ui.fragments.UserDetailsFragment;

public class SettingsPagerAdapter extends FragmentStateAdapter {

    public SettingsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserDetailsFragment();
            case 1:
                return new NotificationSettingsFragment();
            case 2:
                return new SystemSettingsFragment();
            default:
                return new UserDetailsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

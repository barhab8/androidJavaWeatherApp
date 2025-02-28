package com.example.weatherapp.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<String> cities;
    private Map<String, String> cityTemperatures = new HashMap<>();
    private final OnCityClickListener onCityClickListener;
    private String unit;
    private String unitSymbol;

    public interface OnCityClickListener {
        void onCityClick(String city);
    }

    public FavoritesAdapter(Context context, OnCityClickListener listener) {
        this.onCityClickListener = listener;

        SharedPreferences prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE);
        unit = prefs.getString("unit", "metric");

        // Determine the appropriate unit symbol
        switch (unit) {
            case "imperial":
                unitSymbol = "°F";
                break;
            case "standard":
                unitSymbol = "K";
                break;
            default:
                unitSymbol = "°C";
        }
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    public void updateCityTemperature(String city, String temperature) {
        cityTemperatures.put(city, temperature + unitSymbol);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = cities.get(position);
        holder.tvCityName.setText(city);
        holder.tvTemperature.setText(cityTemperatures.getOrDefault(city, "Loading..."));

        holder.itemView.setOnClickListener(v -> onCityClickListener.onCityClick(city));
    }

    @Override
    public int getItemCount() {
        return cities == null ? 0 : cities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCityName, tvTemperature;

        ViewHolder(View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
        }
    }
}

package com.example.weatherapp.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.weather.model.ForecastResponse;
import com.example.weatherapp.ui.utils.ForecastProcessor;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<ForecastResponse.ForecastItem> forecastList = new ArrayList<>();
    private String unit; // Store the unit

    private static final String PREFS_NAME = "weather_prefs";
    private static final String UNIT_KEY = "unit";

    public ForecastAdapter(Context context) {
        // Load unit from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        unit = prefs.getString(UNIT_KEY, "metric");
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forcast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        // Get the forecast item at the current position
        ForecastResponse.ForecastItem item = forecastList.get(position);

        // Initialize ForecastProcessor and format the date
        ForecastProcessor forecastProcessor = new ForecastProcessor();
        String formattedDate = forecastProcessor.formatDate(item.getDateTime());

        // Determine the temperature unit symbol
        String tempUnitSymbol;
        switch (unit) {
            case "imperial":
                tempUnitSymbol = "°F";
                break;
            case "standard":
                tempUnitSymbol = " K";
                break;
            default:
                tempUnitSymbol = "°C";
                break;
        }

        // Update UI with formatted data
        holder.tvDate.setText(formattedDate); // Use the formatted date
        holder.tvTemp.setText(String.format("%s%s", item.getMain().getTemp(), tempUnitSymbol)); // Display temperature
        holder.tvDescription.setText(item.getWeather().get(0).getDescription()); // Weather description

        // Load the weather icon using Picasso
        String iconUrl = "https://openweathermap.org/img/wn/" + item.getWeather().get(0).getIcon() + "@2x.png";
        Picasso.get().load(iconUrl).into(holder.ivIcon);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public void setForecastList(List<ForecastResponse.ForecastItem> forecastList) {
        this.forecastList = forecastList;
        notifyDataSetChanged();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTemp, tvDescription;
        ImageView ivIcon;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }
    }
}

package com.example.weatherapp.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.weatherapp.R;
import com.example.weatherapp.data.AI.GptApiService;
import com.example.weatherapp.data.AI.GptRequest;
import com.example.weatherapp.data.AI.GptResponse;
import com.example.weatherapp.data.AI.PromptBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherTipDialog extends Dialog {

    private final String city;
    private final String temp;
    private final String windSpeed;
    private final String humidity;
    private final String visibility;
    private final String weatherDescription;
    private final String aqi;

    private TextView tipTextView;
    private final GptApiService gptApi = GptApiService.create();

    public WeatherTipDialog(@NonNull Context context,
                            String city,
                            String temp,
                            String windSpeed,
                            String humidity,
                            String visibility,
                            String weatherDescription,
                            String aqi) {
        super(context);
        this.city = city;
        this.temp = temp;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.visibility = visibility;
        this.weatherDescription = weatherDescription;
        this.aqi = aqi;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_weather_tip, null);
        setContentView(view);

        tipTextView = view.findViewById(R.id.tipTextView);

        generateWeatherTip();
    }

    private void generateWeatherTip() {
        String prompt = PromptBuilder.buildPrompt(
                city, temp, windSpeed, humidity, visibility, weatherDescription,
                aqi
        );

        GptRequest request = new GptRequest("gpt-4o-mini", prompt);

        gptApi.getChatCompletion(request).enqueue(new Callback<GptResponse>() {
            @Override
            public void onResponse(Call<GptResponse> call, Response<GptResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String tip = response.body().getFirstMessageContent();
                    tipTextView.setText(tip);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Toast.makeText(getContext(), "Error: " + errorBody, Toast.LENGTH_LONG).show();
                        Log.e("API Error", "Error: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tipTextView.setText("Failed to get tip.");
                }
            }


            @Override
            public void onFailure(Call<GptResponse> call, Throwable t) {
                tipTextView.setText("Error: " + t.getMessage());
                Toast.makeText(getContext(), "Failed to fetch tip", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

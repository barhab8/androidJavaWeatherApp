// GeminiApiService.java
package com.example.weatherapp.data.AI;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {

    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    Call<GeminiResponse> getChatCompletion(
            @Query("key") String apiKey,
            @Body GeminiRequest request
    );

    static GeminiApiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GeminiApiService.class);
    }
}

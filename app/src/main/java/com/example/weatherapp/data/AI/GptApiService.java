package com.example.weatherapp.data.AI;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GptApiService {

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("v1/chat/completions")
    Call<GptResponse> getChatCompletion(@Body GptRequest request);

    static GptApiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openrouter.ai/api/")  // Updated base URL for OpenRouter
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GptApiService.class);
    }
}

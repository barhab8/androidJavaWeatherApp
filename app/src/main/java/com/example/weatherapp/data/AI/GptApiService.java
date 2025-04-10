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

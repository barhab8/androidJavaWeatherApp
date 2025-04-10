package com.example.weatherapp.data.AI;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AIApiService {

    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer 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"
    })
    @POST("v1/chat/completions")
    Call<GptResponse> getChatCompletion(@Body AIRequest request);

    static AIApiService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openrouter.ai/api/")  // Updated base URL for OpenRouter
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AIApiService.class);
    }
}

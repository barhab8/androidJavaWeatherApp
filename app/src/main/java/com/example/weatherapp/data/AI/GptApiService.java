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
                .baseUrl("https://openrouter.ai/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GptApiService.class);
    }
}

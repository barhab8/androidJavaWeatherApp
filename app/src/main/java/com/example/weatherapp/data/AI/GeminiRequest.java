package com.example.weatherapp.data.AI;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

import static com.example.weatherapp.data.AI.AiContentModels.*;

public class GeminiRequest {
    @SerializedName("contents")
    private List<Content> contents;

    public GeminiRequest(String userMessage) {
        this.contents = Collections.singletonList(new Content(
                Collections.singletonList(new Part(userMessage))
        ));
    }
}
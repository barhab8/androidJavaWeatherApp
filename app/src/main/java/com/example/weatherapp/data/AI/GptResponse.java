package com.example.weatherapp.data.AI;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AIResponse {
    @SerializedName("id")
    public String id;

    @SerializedName("object")
    public String object;

    @SerializedName("created")
    public long created;

    @SerializedName("model")
    public String model;

    @SerializedName("choices")
    public List<Choice> choices;

    public String getFirstMessageContent() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).message.content;
        }
        return "No tip available.";
    }

    public static class Choice {
        @SerializedName("index")
        public int index;

        @SerializedName("message")
        public Message message;

        @SerializedName("finish_reason")
        public String finishReason;
    }

    public static class Message {
        @SerializedName("role")
        public String role;

        @SerializedName("content")
        public String content;
    }
}

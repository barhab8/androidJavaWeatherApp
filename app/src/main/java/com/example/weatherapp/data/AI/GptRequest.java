package com.example.weatherapp.data.AI;

import java.util.Collections;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class AIRequest {

    @SerializedName("model")
    private String model;

    @SerializedName("messages")
    private List<Message> messages;

    public AIRequest(String model, String userMessage) {
        this.model = model;
        this.messages = Collections.singletonList(new Message("user", userMessage));
    }

    public static class Message {
        @SerializedName("role")
        String role;

        @SerializedName("content")
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

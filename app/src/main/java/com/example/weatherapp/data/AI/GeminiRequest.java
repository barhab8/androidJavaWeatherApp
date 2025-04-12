// GeminiRequest.java
package com.example.weatherapp.data.AI;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public class GeminiRequest {

    @SerializedName("contents")
    private List<Content> contents;

    public GeminiRequest(String userMessage) {
        this.contents = Collections.singletonList(new Content(
                Collections.singletonList(new Part(userMessage))
        ));
    }

    public static class Content {
        @SerializedName("parts")
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        private String text;

        public Part(String text) {
            this.text = text;
        }
    }
}

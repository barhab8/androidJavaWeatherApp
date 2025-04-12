// GeminiResponse.java
package com.example.weatherapp.data.AI;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeminiResponse {

    @SerializedName("candidates")
    public List<Candidate> candidates;

    public String getFirstMessageContent() {
        if (candidates != null && !candidates.isEmpty()) {
            return candidates.get(0).content.parts.get(0).text;
        }
        return "No tip available.";
    }

    public static class Candidate {
        @SerializedName("content")
        public Content content;

        @SerializedName("finishReason")
        public String finishReason;
    }

    public static class Content {
        @SerializedName("parts")
        public List<Part> parts;
    }

    public static class Part {
        @SerializedName("text")
        public String text;
    }
}

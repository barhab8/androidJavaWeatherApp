package com.example.weatherapp.data.AI;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AiContentModels {

    public static class Content {
        @SerializedName("parts")
        public List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }
    }

    public static class Part {
        @SerializedName("text")
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}

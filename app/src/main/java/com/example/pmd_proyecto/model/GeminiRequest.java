package com.example.pmd_proyecto.model;

import java.util.ArrayList;
import java.util.List;

public class GeminiRequest {
    public List<Content> contents = new ArrayList<>();
    public GenerationConfig generationConfig = new GenerationConfig();

    public static class Content {
        public List<Part> parts = new ArrayList<>();
    }

    public static class Part {
        public String text;
    }

    public static class GenerationConfig {
        public double temperature;
        public int maxOutputTokens;
        public String responseMimeType;
    }
}

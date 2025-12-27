package com.example.pmd_proyecto.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EnunciadoProblema {
    private String questionId;
    private String title;
    private String url;
    private String difficulty;
    private String content;
    private String categoryTitle;
    private List<String> hints;
    private List<TopicTag> topicTags;
    private Solution solution;

    public String getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getContent() {
        return content;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public List<String> getHints() {
        return hints;
    }

    public List<TopicTag> getTopicTags() {
        return topicTags;
    }

    public Solution getSolution() {
        return solution;
    }

    public static class TopicTag {
        private String name;
        public String getName() { return name; }
    }
    public static class Solution {
        private String content;
        public String getContent() { return content; }
    }
}

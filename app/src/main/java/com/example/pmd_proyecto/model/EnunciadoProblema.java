package com.example.pmd_proyecto.model;
import java.util.List;

public class EnunciadoProblema {
    public String questionId;
    public String title;
    public String url;
    public String difficulty;
    public String content;
    public String categoryTitle;
    public List<String> hints;
    public List<TopicTag> topicTags;
    public Solution solution;
    public static class TopicTag {
        public String name;
    }
    public static class Solution {
        public String content;
    }
}

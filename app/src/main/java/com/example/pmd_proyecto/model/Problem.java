package com.example.pmd_proyecto.model;

import java.util.Arrays;

public class Problem {
    private Integer contestId;
    private String problemsetName;
    private String index;
    private String name;
    private String type;
    private Integer rating;
    private String[] tags;

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getProblemsetName() {
        return problemsetName;
    }

    public void setProblemsetName(String problemsetName) {
        this.problemsetName = problemsetName;
    }

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "contestId=" + contestId +
                ", problemsetName='" + problemsetName + '\'' +
                ", index='" + index + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", rating=" + rating +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}

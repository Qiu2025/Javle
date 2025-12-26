package com.example.pmd_proyecto.model;

public class Problem {
    private Integer contestId;
    private String problemsetName;
    private String index;
    private String name;
    private String type;
    private Integer rating;
    private String[] tags;

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }
}

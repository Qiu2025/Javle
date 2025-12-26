package com.example.pmd_proyecto.model;

import java.util.List;

public class ProblemsetResponse {
    public String status;
    public Result result;

    public static class Result {
        public List<Problem> problems;
    }
}

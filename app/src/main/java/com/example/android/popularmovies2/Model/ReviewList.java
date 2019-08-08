package com.example.android.popularmovies2.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ReviewList {

    @SerializedName("results")
    private List<Review> results;

    public void setResults(List<Review> results) {
        this.results = results;
    }
    public List<Review> getResults() {
        return results;
    }
}
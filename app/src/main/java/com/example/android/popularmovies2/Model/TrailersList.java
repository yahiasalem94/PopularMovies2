package com.example.android.popularmovies2.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TrailersList {

    @SerializedName("results")
    private List<Trailer> results;

    public void setResults(List<Trailer> results) {
        this.results = results;
    }
    public List<Trailer> getResults() {
        return results;
    }
}
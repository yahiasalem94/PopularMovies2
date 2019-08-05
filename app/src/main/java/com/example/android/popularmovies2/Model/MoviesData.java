package com.example.android.popularmovies2.Model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MoviesData implements Serializable {

    private String title;
    private String releaseDate;
    private String voteAverage;
    private String overview;
    private String posterPath;

    public MoviesData() {
        /* Empty Constructor */
    }
    public MoviesData(String title, String releaseDate, String voteAverage, String overview, String posterPath) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}

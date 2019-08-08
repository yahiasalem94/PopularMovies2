package com.example.android.popularmovies2.Utils;

import com.example.android.popularmovies2.Model.MovieData;
import com.example.android.popularmovies2.Model.MoviesList;
import com.example.android.popularmovies2.Model.ReviewList;
import com.example.android.popularmovies2.Model.Trailer;
import com.example.android.popularmovies2.Model.TrailersList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("movie/top_rated")
    Call<MoviesList> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MoviesList> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailersList> getTrailer(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewList> getReviews(@Path("id") int id, @Query("api_key") String apiKey);
}
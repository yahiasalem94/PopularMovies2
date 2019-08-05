package com.example.android.popularmovies2.Utils;

import com.example.android.popularmovies2.Model.MoviesList;

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

//    @GET("movie/{id}")
//    Call<List<MoviesData>> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);
}
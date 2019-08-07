package com.example.android.popularmovies2.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies2.Model.MovieData;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM moviesData")
    LiveData<List<MovieData>> loadAllMovies();

    @Query("SELECT * FROM moviesData WHERE id = :id")
    MovieData loadMovie(String id);

    @Insert
    void insertMovie(MovieData movie);

    @Delete
    void deleteMovie(MovieData movie);

}

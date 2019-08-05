package com.example.android.popularmovies2.Utils;

import android.content.Context;

import com.example.android.popularmovies2.Model.MoviesData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public final class OpenMoviesJsonUtils {

    public static ArrayList<MoviesData> fetchMoviesDetailsFromJson(Context context, String forecastJsonStr)
            throws JSONException {

        ArrayList<MoviesData> itemList = new ArrayList<>();

        final String RESULT = "results";
        final String TITLE = "title";
        final String RELEASE_DATE = "release_date";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String POSTER_PATH = "poster_path";
        final String STATUS_CODE = "status_code";


        JSONObject movieJson = new JSONObject(forecastJsonStr);

        /* Is there an error? */
        if (movieJson.has(STATUS_CODE)) {
            int errorCode = movieJson.getInt(STATUS_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray moviesArray = movieJson.getJSONArray(RESULT);


        for (int i = 0; i < moviesArray.length(); i++) {

            MoviesData moviesData = new MoviesData();
            /* Get the JSON object representing the day */
            JSONObject movie = moviesArray.getJSONObject(i);

            moviesData.setTitle(movie.getString(TITLE));
            moviesData.setReleaseDate(movie.getString(RELEASE_DATE));
            moviesData.setVoteAverage(movie.getString(VOTE_AVERAGE));
            moviesData.setOverview(movie.getString(OVERVIEW));
            moviesData.setPosterPath(movie.getString(POSTER_PATH));

            itemList.add(moviesData);
        }

        return itemList;
    }
}
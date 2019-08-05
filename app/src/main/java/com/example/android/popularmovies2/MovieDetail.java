package com.example.android.popularmovies2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.Model.MoviesData;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    private ImageView posterImage;
    private TextView title;
    private TextView releaseDate;
    private TextView rating;
    private TextView overview;

    private static final String totalVotes = "/10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        posterImage = findViewById(R.id.posterImageview);
        title = findViewById(R.id.titleTextview);
        releaseDate = findViewById(R.id.releaseDateTextview);
        rating = findViewById(R.id.ratingTextview);
        overview = findViewById(R.id.overview);

        Intent intent = getIntent();
        MoviesData movie = (MoviesData)intent.getSerializableExtra(Constants.INTENT_TAG);

        if (movie != null) {

            Picasso.get().load(Constants.IMAGE_URL+movie.getPosterPath()).into(posterImage);
            title.setText(movie.getTitle());
            releaseDate.setText(movie.getReleaseDate());
            String ratingValue = movie.getVoteAverage()+totalVotes;
            rating.setText(ratingValue);
            overview.setText(movie.getOverview());

            Log.d("MovieDetail", movie.getPosterPath());
            Log.d("MovieDetail", movie.getTitle());
            Log.d("MovieDetail", movie.getReleaseDate());
            Log.d("MovieDetail", movie.getVoteAverage());
            Log.d("MovieDetail", movie.getOverview());
        }
    }

}

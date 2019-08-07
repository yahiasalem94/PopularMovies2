package com.example.android.popularmovies2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.Database.AppDatabase;
import com.example.android.popularmovies2.Model.MovieData;
import com.example.android.popularmovies2.Model.MoviesList;
import com.example.android.popularmovies2.Model.Trailer;
import com.example.android.popularmovies2.Model.TrailersList;
import com.example.android.popularmovies2.Utils.ApiInterface;
import com.example.android.popularmovies2.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetail extends AppCompatActivity {

    private ImageView posterImage;
    private TextView title;
    private TextView releaseDate;
    private TextView rating;
    private TextView overview;
    private Button addToFavorite;

    private Button button;
    private String key;

    private MovieData movie;

    private static final String TAG = MovieDetail.class.getSimpleName();

    private static final String totalVotes = "/10";

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);


        posterImage = findViewById(R.id.posterImageview);
        title = findViewById(R.id.titleTextview);
        releaseDate = findViewById(R.id.releaseDateTextview);
        rating = findViewById(R.id.ratingTextview);
        overview = findViewById(R.id.overview);
        addToFavorite = findViewById(R.id.addToFavorite);

        button = findViewById(R.id.play);

        mDb = AppDatabase.getInstance(getApplicationContext());

        addToFavorite.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

               addToFavorite();

            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+key)));
                Log.i("Video", "Video Playing....");

            }
        });

        Intent intent = getIntent();
        movie = (MovieData)intent.getSerializableExtra(Constants.INTENT_TAG);

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
            Log.d("MovieDetail", movie.getId()+"");
        }

        loadTrailers();
    }

    private void addToFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().insertMovie(movie);
            }
        });
    }

    private void loadTrailers() {

        ApiInterface apiService = NetworkUtils.getRetrofitInstance().create(ApiInterface.class);

        Call<TrailersList> call = apiService.getTrailer(movie.getId(), Constants.API_KEY);

        call.enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(Call<TrailersList> call, Response<TrailersList> response) {

                ArrayList<Trailer> trailers = (ArrayList) response.body().getResults();
                key = trailers.get(0).getKey();
                Log.d(TAG, "Number of movies received: " + trailers.get(0).getKey());
            }

            @Override
            public void onFailure(Call<TrailersList> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }
}

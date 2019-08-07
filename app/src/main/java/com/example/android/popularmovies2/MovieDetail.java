package com.example.android.popularmovies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MovieDetail extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler {

    private ImageView posterImage;
    private TextView title;
    private TextView releaseDate;
    private TextView rating;
    private TextView overview;
    private Button addToFavorite;
    private RecyclerView mRecyclerView;

    private Button button;
    private String key;

    private MovieData movie;
    private TrailersAdapter trailersAdapter;
    private ArrayList<Trailer> trailersList;

    private boolean isFavorite = false;

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
        mRecyclerView = findViewById(R.id.recyclerViewTrailers);

        mDb = AppDatabase.getInstance(getApplicationContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView


        trailersAdapter = new TrailersAdapter(this, this);

        addToFavorite.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
               addToFavorite();
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

        checkIfFavorite();
        loadTrailers();
        mRecyclerView.setAdapter(trailersAdapter);

    }

    private void checkIfFavorite() {
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        LiveData<MovieData> favoriteMovie = mDb.movieDao().loadMovie(movie.getId());

        favoriteMovie.observe(this, new Observer<MovieData>() {
            @Override
            public void onChanged(@Nullable MovieData favorite) {
                Log.d(TAG, "Receiving database update from LiveData");
                   if (favorite != null) {
                       isFavorite = true;
                   } else {
                       isFavorite = false;
                   }
            }
        });
    }

    private void addToFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                if (isFavorite) {
                    mDb.movieDao().deleteMovie(movie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Movie removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mDb.movieDao().insertMovie(movie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Movie added to favorites", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    private void loadTrailers() {

        ApiInterface apiService = NetworkUtils.getRetrofitInstance().create(ApiInterface.class);

        Call<TrailersList> call = apiService.getTrailer(movie.getId(), Constants.API_KEY);

        call.enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(Call<TrailersList> call, Response<TrailersList> response) {

                trailersList = (ArrayList) response.body().getResults();
                trailersAdapter.setTrailers(trailersList);
                key = trailersList.get(0).getKey();
                Log.d(TAG, "Number of movies received: " + trailersList.get(0).getKey());
            }

            @Override
            public void onFailure(Call<TrailersList> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onClick(int position) {

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.BASE_YOUTUBE_URL+trailersList.get(position).getKey())));
        Log.i("Video", "Video Playing....");
    }
}

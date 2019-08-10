package com.example.android.popularmovies2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.popularmovies2.Database.AppDatabase;
import com.example.android.popularmovies2.Model.MovieData;
import com.example.android.popularmovies2.Model.Review;
import com.example.android.popularmovies2.Model.ReviewList;
import com.example.android.popularmovies2.Model.Trailer;
import com.example.android.popularmovies2.Model.TrailersList;
import com.example.android.popularmovies2.Utils.ApiInterface;
import com.example.android.popularmovies2.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetail extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler {

    private ImageView posterImage;
    private TextView title;
    private TextView releaseDate;
    private TextView rating;
    private TextView overview;
    private ToggleButton addToFavorite;
    private RecyclerView mRecyclerView;
    private RecyclerView mReviewsRecyclerView;

    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;

    private MovieData movie;


    private ArrayList<Trailer> trailersList;
    private ArrayList<Review> reviewsList;

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
        mReviewsRecyclerView = findViewById(R.id.recyclerViewReviews);

        mDb = AppDatabase.getInstance(getApplicationContext());


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        LinearLayoutManager linearLayoutManagerReviews = new LinearLayoutManager(getApplicationContext());
        mReviewsRecyclerView.setLayoutManager(linearLayoutManagerReviews);


        trailersAdapter = new TrailersAdapter(this, this);
        reviewsAdapter = new ReviewsAdapter( this);

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

            Log.d(TAG, movie.getTitle());
        }

        checkIfFavorite();
        loadReviews();
        loadTrailers();

        mRecyclerView.setAdapter(trailersAdapter);
        mReviewsRecyclerView.setAdapter(reviewsAdapter);
    }

    private void checkIfFavorite() {
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                MovieData favoriteMovie = mDb.movieDao().loadMovie(movie.getId());
                if ( favoriteMovie != null ) {
                    isFavorite = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addToFavorite.setChecked(isFavorite);
                        }
                    });
                } else {
                    isFavorite = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addToFavorite.setChecked(isFavorite);
                        }
                    });
                }
            }
        });
    }

    private void addToFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                if (isFavorite) {
                    isFavorite = false;
                    mDb.movieDao().deleteMovie(movie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Movie removed from favorites", Toast.LENGTH_SHORT).show();
                            addToFavorite.setChecked(isFavorite);
                        }
                    });
                } else {
                    isFavorite = true;
                    mDb.movieDao().insertMovie(movie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Movie added to favorites", Toast.LENGTH_SHORT).show();
                            addToFavorite.setChecked(isFavorite);
                        }
                    });
                }

            }
        });
    }

    private void loadReviews() {

        ApiInterface apiService = NetworkUtils.getRetrofitInstance().create(ApiInterface.class);

        Log.d(TAG, "key is" + " " + movie.getId());
        Call<ReviewList> call = apiService.getReviews(movie.getId(), Constants.API_KEY);

        call.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {

                reviewsList = (ArrayList) response.body().getResults();
                Log.d(TAG, reviewsList.get(0).getAuthor() + " " + reviewsList.get(0).getComment());
                reviewsAdapter.setReviews(reviewsList);
            }

            @Override
            public void onFailure(Call<ReviewList> call, Throwable t) {
                Log.e(TAG, t.toString());
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

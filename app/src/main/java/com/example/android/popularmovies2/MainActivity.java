package com.example.android.popularmovies2;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.popularmovies2.Database.AppDatabase;
import com.example.android.popularmovies2.Model.MovieData;
import com.example.android.popularmovies2.Model.MoviesList;
import com.example.android.popularmovies2.Utils.ApiInterface;
import com.example.android.popularmovies2.Utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MoviesAdapter moviesAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private  Spinner spinner;

    private boolean isConnected = true;

    private boolean isFavorites = false;


    private ApiInterface apiService;

    private ArrayList<MovieData> movies;

    private AppDatabase mDb;

    private static final String CATEGORY_ID = "category";
    private static final int DEFAULT_CATEGORY = 0;

    private int category = 0;

    private static Bundle mBundleRecyclerViewState;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private  Parcelable listState;
    private  GridLayoutManager gridLayoutManager;

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");
        if (savedInstanceState != null && savedInstanceState.containsKey(CATEGORY_ID)) {

            category = savedInstanceState.getInt(CATEGORY_ID, DEFAULT_CATEGORY);
            Log.d(TAG, "savedInstanceState not null" + " " + category+"");
        }
        // get the reference of RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),calculateNoOfColumns(this));
        mRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

        //  call the constructor of MoviesAdapter to send the reference and data to Adapter
        moviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(moviesAdapter); // set the Adapter to RecyclerView

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        apiService = NetworkUtils.getRetrofitInstance().create(ApiInterface.class);

        mDb = AppDatabase.getInstance(getApplicationContext());

        loadData();


    }


    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    private void loadData() {
        showDataView();
        mLoadingIndicator.setVisibility(View.VISIBLE);

        if ( category == 0 ) {
            Call<MoviesList> call = apiService.getTopRatedMovies(Constants.API_KEY);

            call.enqueue(new Callback<MoviesList>() {
                @Override
                public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {

                    movies = (ArrayList) response.body().getResults();
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    moviesAdapter.setMoviesData(movies);
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
                }

                @Override
                public void onFailure(Call<MoviesList> call, Throwable t) {

                    mLoadingIndicator.setVisibility(View.INVISIBLE);

                    if ( t instanceof IOException) {
                        isConnected = false;
                    }

                    showErrorMessage(isConnected);

                }
            });
        } else if ( category == 1 ) {
            Call<MoviesList> call = apiService.getPopularMovies(Constants.API_KEY);
            call.enqueue(new Callback<MoviesList>() {
                @Override
                public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {

                    movies = (ArrayList) response.body().getResults();

                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    moviesAdapter.setMoviesData(movies);
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
                }

                @Override
                public void onFailure(Call<MoviesList> call, Throwable t) {

                    mLoadingIndicator.setVisibility(View.INVISIBLE);

                    if ( t instanceof IOException) {
                        isConnected = false;
                    }

                    showErrorMessage(isConnected);

                }
            });
        } else if (category == 2) {
            retrieveTasks();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CATEGORY_ID, category);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause");
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Log.d(TAG, "onResume");
        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    private void retrieveTasks() {
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        LiveData<List<MovieData>> favorites = mDb.movieDao().loadAllMovies();

        favorites.observe(this, new Observer<List<MovieData>>() {
            @Override
            public void onChanged(@Nullable List<MovieData> favorites) {
                Log.d(TAG, "Receiving database update from LiveData");
                if (isFavorites) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    movies = (ArrayList) favorites;
                    moviesAdapter.setMoviesData(movies);
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
                }
            }
        });
    }

    private void showDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int position) {

        Intent intent = new Intent(this, MovieDetail.class);
        intent.putExtra(Constants.INTENT_TAG, movies.get(position));
        Log.d(TAG, movies.get(position).getTitle());
        startActivity(intent);
    }


    private void showErrorMessage(boolean isConnected) {

        if (isConnected) {
            /* First, hide the currently visible data */
            mRecyclerView.setVisibility(View.INVISIBLE);
            /* Then, show the error */
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        } else {
            /* First, hide the currently visible data */
            mRecyclerView.setVisibility(View.INVISIBLE);
            /* Then, show the error */
            mErrorMessageDisplay.setText(getString(R.string.no_connection));
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sortBy, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(category);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isFavorites = (i == 2) ? true : false;
                category = i;
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

}
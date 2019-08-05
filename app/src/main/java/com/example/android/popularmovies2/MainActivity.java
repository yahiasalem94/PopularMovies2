package com.example.android.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;


import com.example.android.popularmovies2.Model.MoviesData;
import com.example.android.popularmovies2.Utils.NetworkUtils;
import com.example.android.popularmovies2.Utils.OpenMoviesJsonUtils;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private MoviesAdapter moviesAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private ArrayList<MoviesData> itemList;
    private boolean isConnected = true;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the reference of RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),calculateNoOfColumns(this));
        mRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView

        //  call the constructor of MoviesAdapter to send the reference and data to Adapter
        moviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(moviesAdapter); // set the Adapter to RecyclerView

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        loadData(0);
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

    private void loadData(int i) {
        showDataView();
        new FetchMoviesTask().execute(i);

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
        intent.putExtra(Constants.INTENT_TAG, itemList.get(position));
        startActivity(intent);
    }


    private void showErrorMessage() {

        if (isConnected) {
            /* First, hide the currently visible data */
            mRecyclerView.setVisibility(View.INVISIBLE);
            /* Then, show the error */
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        } else {
            /* First, hide the currently visible data */
            mRecyclerView.setVisibility(View.INVISIBLE);
            /* Then, show the error */
            mErrorMessageDisplay.setText("No Internet Connection");
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
    }

    public class FetchMoviesTask extends AsyncTask<Integer, Void, ArrayList<MoviesData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<MoviesData> doInBackground(Integer... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            if ( NetworkUtils.networkStatus(MainActivity.this) ) {

                int urlNumber = params[0];
                URL requestUrl = NetworkUtils.buildUrl(urlNumber);

                try {
                    String jsonResponse = NetworkUtils
                            .getResponseFromHttpUrl(requestUrl);

                    itemList = OpenMoviesJsonUtils
                            .fetchMoviesDetailsFromJson(MainActivity.this, jsonResponse);

                    return itemList;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                isConnected = false;
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MoviesData> moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (moviesData != null) {
                showDataView();
                moviesAdapter.setMoviesData(moviesData);
            } else if (isConnected == false ){
                showErrorMessage();
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sortBy, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                   loadData(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

}
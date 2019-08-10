package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popularmovies2.Model.MovieData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapterViewHolder> {

    private ArrayList<MovieData> mMoviesData;
    private final MoviesAdapterOnClickHandler mClickHandler;
    private Context context;


    public interface MoviesAdapterOnClickHandler {
        void onClick(int position);
    }


    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler, Context context) {
        mClickHandler = clickHandler;
        this.context = context;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.movie_list_row, viewGroup, false);
        return new MoviesAdapterViewHolder(view, mClickHandler);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder adapterViewHolder, int position) {
        Picasso.get().load(Constants.IMAGE_URL+mMoviesData.get(position).getPosterPath()).into(adapterViewHolder.mMoviePosterImageView);
        adapterViewHolder.mMoviePosterImageView.setAdjustViewBounds(true);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.size();
    }

    public void setMoviesData(ArrayList<MovieData> movieData) {
        mMoviesData = movieData;
        notifyDataSetChanged();
    }
}
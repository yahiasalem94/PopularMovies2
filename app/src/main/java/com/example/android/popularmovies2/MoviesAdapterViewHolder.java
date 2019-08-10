package com.example.android.popularmovies2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final ImageView mMoviePosterImageView;
    private final MoviesAdapter.MoviesAdapterOnClickHandler mClickHandler;

    public MoviesAdapterViewHolder(View view, MoviesAdapter.MoviesAdapterOnClickHandler clickHandler) {
        super(view);
        mMoviePosterImageView = view.findViewById(R.id.moviePoster);
        this.mClickHandler = clickHandler;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mClickHandler.onClick(getAdapterPosition());
    }
}
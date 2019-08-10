package com.example.android.popularmovies2;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import static android.support.constraint.Constraints.TAG;

public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView trailerName;
    private final TrailersAdapter.TrailersAdapterOnClickHandler mClickHandler;

    public TrailersAdapterViewHolder(View view, TrailersAdapter.TrailersAdapterOnClickHandler mClickHandler) {
        super(view);
        trailerName = view.findViewById(R.id.trailerName);
        view.setOnClickListener(this);
        this.mClickHandler = mClickHandler;
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, getAdapterPosition()+"");
        mClickHandler.onClick(getAdapterPosition());
    }
}
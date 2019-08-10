package com.example.android.popularmovies2;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
    public final TextView author;
    public final TextView comment;

    public ReviewsAdapterViewHolder(View view) {
        super(view);
        author = view.findViewById(R.id.author);
        comment = view.findViewById(R.id.comment);
    }

}
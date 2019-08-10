package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies2.Model.Review;

import java.util.ArrayList;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapterViewHolder> {

    private ArrayList<Review> mReviewData;
    private Context context;

    private static final String TAG = TrailersAdapter.class.getSimpleName();



    public ReviewsAdapter( Context context) {
        this.context = context;
    }


    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.review_list_row, viewGroup, false);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder adapterViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder"+position+"");
        adapterViewHolder.author.setText(mReviewData.get(position).getAuthor());
        adapterViewHolder.comment.setText(mReviewData.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        if (null == mReviewData) return 0;
        return mReviewData.size();
    }

    public void setReviews(ArrayList<Review> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }
}
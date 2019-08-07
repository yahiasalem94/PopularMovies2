package com.example.android.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies2.Model.MovieData;
import com.example.android.popularmovies2.Model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private ArrayList<Trailer> mTrailerData;
    private final TrailersAdapterOnClickHandler mClickHandler;
    private Context context;

    private static final String TAG = TrailersAdapter.class.getSimpleName();


    public interface TrailersAdapterOnClickHandler {
        void onClick(int position);
    }


    public TrailersAdapter(TrailersAdapterOnClickHandler clickHandler, Context context) {
        Log.d(TAG, clickHandler.toString());
        mClickHandler = clickHandler;
        this.context = context;
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView trailerName;

        public TrailersAdapterViewHolder(View view) {
            super(view);
            trailerName = view.findViewById(R.id.trailerName);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e(TAG, getAdapterPosition()+"");
            mClickHandler.onClick(getAdapterPosition());
        }
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trailer_list_row, viewGroup, false);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder adapterViewHolder, int position) {
        Log.d(TAG, "onBindViewHolder"+position+"");
        adapterViewHolder.trailerName.setText(context.getString(R.string.trailer)+" "+(position+1)+"");
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerData) return 0;
        return mTrailerData.size();
    }

    public void setTrailers(ArrayList<Trailer> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }
}
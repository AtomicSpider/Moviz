package com.satandigital.moviz.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.satandigital.moviz.R;
import com.satandigital.moviz.activities.DetailsActivity;
import com.satandigital.moviz.activities.MainActivity;
import com.satandigital.moviz.callbacks.MovieDetailCallback;
import com.satandigital.moviz.callbacks.MovizCallback;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.models.MovieObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/14/2016.
 */
public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder> {

    private final String TAG = MoviesRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private MovizCallback mCallback;
    private MovieDetailCallback movieDetailCallback;
    private LayoutInflater mLayoutInflater;
    private boolean paging = true;

    //Data
    private ArrayList<MovieObject> movieObjects;
    private String TMDB_BASE_POSTER_PATH;
    private int listPosition = 0;

    public MoviesRecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.movieObjects = new ArrayList<>();

        TMDB_BASE_POSTER_PATH = mContext.getResources().getString(R.string.TMDB_BASE_POSTER_PATH);
    }

    public void setCallback(MovizCallback callback) {
        this.mCallback = callback;
    }

    public void setMovieDetailCallback(MovieDetailCallback callback) {
        this.movieDetailCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "" + position);
        listPosition = position;

        Picasso.with(mContext).load(TMDB_BASE_POSTER_PATH + movieObjects.get(position).getPoster_path()).placeholder(R.drawable.placeholder_poster).error(R.drawable.placeholder_error_poster).into(holder.movie_poster_iv);
        holder.movie_title_tv.setText(movieObjects.get(position).getOriginal_title());

        holder.movie_poster_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Launching details for: " + movieObjects.get(position).getOriginal_title());
                if (MainActivity.twoPane) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", movieObjects.get(position));
                    movieDetailCallback.CallbackRequest(AppCodes.CALLBACK_MOVIE_BUNDLE, bundle);
                } else {
                    Intent mIntent = new Intent(mContext, DetailsActivity.class);
                    mIntent.putExtra("data", movieObjects.get(position));
                    mContext.startActivity(mIntent);
                }
            }
        });

        if (position == movieObjects.size() - 1 && paging) {
            Log.i(TAG, "Reached final movie");
            mCallback.CallbackRequest(AppCodes.CALLBACK_FETCH_MOVIES_WITH_PAGE, "");
        }
    }

    @Override
    public int getItemCount() {
        return movieObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_poster_iv)
        ImageView movie_poster_iv;
        @BindView(R.id.movie_title_tv)
        TextView movie_title_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void clearAllAndPopulate(ArrayList<MovieObject> movies) {
        movieObjects.clear();
        movieObjects.addAll(movies);
        notifyDataSetChanged();
    }

    public void addItemsAndPopulate(ArrayList<MovieObject> movies) {
        movieObjects.addAll(movies);
        notifyDataSetChanged();
    }

    public ArrayList<MovieObject> getMovieObjects() {
        return movieObjects;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setPaging(boolean b) {
        paging = b;
    }
}
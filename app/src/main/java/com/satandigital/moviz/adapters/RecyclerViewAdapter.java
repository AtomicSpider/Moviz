package com.satandigital.moviz.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.satandigital.moviz.BuildConfig;
import com.satandigital.moviz.R;
import com.satandigital.moviz.activities.DetailsActivity;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.models.MovieObject;
import com.satandigital.moviz.models.TMDBPageObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/14/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final String TAG = RecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private AdapterCallback mCallback;
    private LayoutInflater mLayoutInflater;

    //Data
    private ArrayList<MovieObject> movieObjects;
    private String TMDB_BASE_POSTER_PATH;

    public RecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.movieObjects = new ArrayList<>();

        TMDB_BASE_POSTER_PATH = mContext.getResources().getString(R.string.TMDB_BASE_POSTER_PATH);
    }

    public interface AdapterCallback {
        void AdapterCallbackRequest(String requestCode, int nextPage);
    }

    public void setCallback(AdapterCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "" + position);

        Picasso.with(mContext).load(TMDB_BASE_POSTER_PATH + movieObjects.get(position).getPoster_path()).placeholder(R.drawable.placeholder_poster).error(R.drawable.placeholder_error_poster).into(holder.movie_poster_iv);
        holder.movie_title_tv.setText(movieObjects.get(position).getOriginal_title());

        holder.movie_poster_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Launching details for: " + movieObjects.get(position).getOriginal_title());
                Intent mIntent = new Intent(mContext, DetailsActivity.class);
                mIntent.putExtra("data", movieObjects.get(position));
                mContext.startActivity(mIntent);
            }
        });

        //ToDo Pagination
        /*if (position == movieObjects.size() - 1) {
            Log.i(TAG, "Reached final card");
            if (!isProcessOngoing) {
                Log.i(TAG, "Requesting more Data");
                new FetchMoviesTask(mContext).execute(sortType, Integer.toString(page + 1));
            }
        }*/
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
}
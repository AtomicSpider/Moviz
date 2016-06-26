package com.satandigital.moviz.models;

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
    private boolean isProcessOngoing = false;
    private String sortType;
    private int page = 0;
    private String TMDB_BASE_POSTER_PATH;
    private String TMDB_MOVIES_BASE_URL;

    public RecyclerViewAdapter(Context context, String sortType) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.movieObjects = new ArrayList<>();
        this.sortType = sortType;

        TMDB_BASE_POSTER_PATH = mContext.getResources().getString(R.string.TMDB_BASE_POSTER_PATH);
        TMDB_MOVIES_BASE_URL = mContext.getResources().getString(R.string.TMDB_MOVIES_BASE_URL);

        new FetchMoviesTask(mContext).execute(sortType, null);
    }

    public interface AdapterCallback {
        void AdapterCallbackRequest(String requestCode);
    }

    public void setCallback(AdapterCallback callback) {
        this.mCallback = callback;
    }

    public void toggleSort() {
        if (isProcessOngoing)
            Toast.makeText(mContext, "Search Ongoing...", Toast.LENGTH_SHORT).show();
        else {
            if (sortType.equals(AppCodes.PREF_SORT_POPULAR)) {
                sortType = AppCodes.PREF_SORT_TOP_RATED;
                Log.i(TAG, "Sort by Popularity");
            } else if (sortType.equals(AppCodes.PREF_SORT_TOP_RATED)) {
                sortType = AppCodes.PREF_SORT_POPULAR;
                Log.i(TAG, "Sort by Ratings");
            }
            new FetchMoviesTask(mContext).execute(sortType, null);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_grid, parent, false);
        ViewHolder mViewHolder = new ViewHolder(view);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

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

        if (position == movieObjects.size() - 1) {
            Log.i(TAG, "Reached final card");
            if (!isProcessOngoing) {
                Log.i(TAG, "Requesting more Data");
                new FetchMoviesTask(mContext).execute(sortType, Integer.toString(page + 1));
            }
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

    private class FetchMoviesTask extends AsyncTask<String, Void, TMDBPageObject> {

        private final String FetchMoviesTaskTAG = FetchMoviesTask.class.getSimpleName();
        private Context mContext;

        public FetchMoviesTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected TMDBPageObject doInBackground(String... params) {
            isProcessOngoing = true;
            publishProgress();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            String sortType = params[0];
            String apiKey = BuildConfig.TMDB_API_KEY;

            try {
                final String API_KEY_PARAM = "api_key";
                final String API_KEY_PAGE = "page";

                Uri.Builder builderUri = Uri.parse(TMDB_MOVIES_BASE_URL).buildUpon()
                        .appendPath(sortType)
                        .appendQueryParameter(API_KEY_PARAM, apiKey);

                if (params[1] != null) builderUri.appendQueryParameter(API_KEY_PAGE, params[1]);
                Uri builtUri = builderUri.build();

                URL url = new URL(builtUri.toString());

                Log.d(TAG, "" + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(FetchMoviesTaskTAG, "Error " + e.getMessage(), e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(FetchMoviesTaskTAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(FetchMoviesTaskTAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            mCallback.AdapterCallbackRequest(AppCodes.CODE_ENABLE_PROGRESS_BAR);
        }

        @Override
        protected void onPostExecute(TMDBPageObject tmdbPageObject) {
            super.onPostExecute(tmdbPageObject);

            if (tmdbPageObject != null) {
                page = tmdbPageObject.getPage();
                if (page == 1) {
                    if (sortType.equals(AppCodes.PREF_SORT_POPULAR))
                        mCallback.AdapterCallbackRequest(AppCodes.CODE_TITLE_POPULAR);
                    else if (sortType.equals(AppCodes.PREF_SORT_TOP_RATED))
                        mCallback.AdapterCallbackRequest(AppCodes.CODE_TITLE_TOP_RATED);

                    movieObjects.clear();
                }

                movieObjects.addAll(tmdbPageObject.getMovieObjects());
                notifyDataSetChanged();
            } else
                Toast.makeText(mContext, "Oops, Something went wrong", Toast.LENGTH_SHORT).show();

            isProcessOngoing = false;
            mCallback.AdapterCallbackRequest(AppCodes.CODE_DISABLE_PROGRESS_BAR);
        }

        private TMDBPageObject getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            int page = 0;
            ArrayList<MovieObject> movieObjects = new ArrayList<>();
            TMDBPageObject tmdbPageObject = new TMDBPageObject();

            final String KEY_PAGE = "page";
            final String KEY_RESULTS = "results";
            final String KEY_POSTER_PATH = "poster_path";
            final String KEY_ADULT = "adult";
            final String KEY_OVERVIEW = "overview";
            final String KEY_RELEASE_DATE = "release_date";
            final String KEY_GENRE_IDS = "genre_ids";
            final String KEY_ID = "id";
            final String KEY_ORIGINAL_TITLE = "original_title";
            final String KEY_ORIGINAL_LANGUAGE = "original_language";
            final String KEY_TITLE = "title";
            final String KEY_BACKDROP_PATH = "backdrop_path";
            final String KEY_POPULARITY = "popularity";
            final String KEY_VOTE_COUNT = "vote_count";
            final String KEY_VIDEO = "video";
            final String KEY_VOTE_AVERAGE = "vote_average";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            page = moviesJson.getInt(KEY_PAGE);
            JSONArray resultsArray = moviesJson.getJSONArray(KEY_RESULTS);

            for (int i = 0; i < resultsArray.length(); i++) {
                MovieObject movieObject = new MovieObject();
                movieObject.setPoster_path(resultsArray.getJSONObject(i).getString(KEY_POSTER_PATH));
                movieObject.setAdult(resultsArray.getJSONObject(i).getBoolean(KEY_ADULT));
                movieObject.setOverview(resultsArray.getJSONObject(i).getString(KEY_OVERVIEW));
                movieObject.setRelease_date(resultsArray.getJSONObject(i).getString(KEY_RELEASE_DATE));
                JSONArray genreIdsJsonArr = resultsArray.getJSONObject(i).getJSONArray(KEY_GENRE_IDS);
                int[] genreIds = new int[genreIdsJsonArr.length()];

                for (int j = 0; j < genreIdsJsonArr.length(); ++j) {
                    genreIds[j] = genreIdsJsonArr.optInt(j);
                }
                movieObject.setGenre_ids(genreIds);
                movieObject.setId(resultsArray.getJSONObject(i).getInt(KEY_ID));
                movieObject.setOriginal_title(resultsArray.getJSONObject(i).getString(KEY_ORIGINAL_TITLE));
                movieObject.setOriginal_language(resultsArray.getJSONObject(i).getString(KEY_ORIGINAL_LANGUAGE));
                movieObject.setTitle(resultsArray.getJSONObject(i).getString(KEY_TITLE));
                movieObject.setBackdrop_path(resultsArray.getJSONObject(i).getString(KEY_BACKDROP_PATH));
                movieObject.setPopularity(resultsArray.getJSONObject(i).getDouble(KEY_POPULARITY));
                movieObject.setVote_count(resultsArray.getJSONObject(i).getInt(KEY_VOTE_COUNT));
                movieObject.setVideo(resultsArray.getJSONObject(i).getBoolean(KEY_VIDEO));
                movieObject.setVote_average(resultsArray.getJSONObject(i).getDouble(KEY_VOTE_AVERAGE));

                movieObjects.add(movieObject);
            }

            tmdbPageObject.setPage(page);
            tmdbPageObject.setMovieObjects(movieObjects);

            return tmdbPageObject;
        }

    }
}
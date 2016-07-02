package com.satandigital.moviz.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.satandigital.moviz.callbacks.MovizCallback;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;
import com.satandigital.moviz.database.MoviesContract;
import com.satandigital.moviz.models.MovieObject;

import java.util.ArrayList;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 7/2/2016.
 */
public class DatabaseTask extends AsyncTask<String, Void, String[]> {

    private final String TAG = DatabaseTask.class.getSimpleName();

    MovizCallback mCallback;
    Context mContext;
    MovieObject movieObject;
    ArrayList<MovieObject> movieObjects = null;

    public DatabaseTask(MovizCallback callback, Context context, MovieObject movieObject) {
        mCallback = callback;
        mContext = context;
        this.movieObject = movieObject;
    }

    @Override
    protected String[] doInBackground(String... params) {

        String TASK_TYPE = params[0];
        String MOVIE_ID = params[1];
        String[] retStr;

        switch (TASK_TYPE) {
            case AppCodes.TASK_IS_FAVORITE: {
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(MOVIE_ID).build();
                Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0)
                    retStr = new String[]{AppCodes.CALLBACK_IS_FAVORITE, "true"};
                else
                    retStr = new String[]{AppCodes.CALLBACK_IS_FAVORITE, "false"};
                if (cursor != null) cursor.close();
                return retStr;
            }
            case AppCodes.TASK_DELETE_FAVORITE: {
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(MOVIE_ID).build();
                int rows = mContext.getContentResolver().delete(uri, null, null);
                if (rows > 0)
                    return new String[]{AppCodes.CALLBACK_DELETE_FAVORITE, "true"};
                else
                    return new String[]{AppCodes.CALLBACK_DELETE_FAVORITE, "false"};
            }
            case AppCodes.TASK_ADD_FAVORITE: {
                Uri success = mContext.getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, Utils.getContentValues(movieObject));
                if (success != null)
                    return new String[]{AppCodes.CALLBACK_ADD_FAVORITE, "true"};
                else
                    return new String[]{AppCodes.CALLBACK_ADD_FAVORITE, "false"};
            }
            case AppCodes.TASK_QUERY_FAVORITE_LIST: {
                Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
                Log.d(TAG, "Query Started: " + uri);
                Cursor c = mContext.getContentResolver().query(uri, null, null, null, null);

                if (c != null) {
                    Log.d(TAG, "Query Ended: " + c.getCount());
                    movieObjects = new ArrayList<>();

                    while (c.moveToNext()) {
                        MovieObject mObject = new MovieObject();
                        mObject.setPoster_path(c.getString(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH)));
                        mObject.setAdult(c.getInt(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ADULT)) > 0);
                        mObject.setOverview(c.getString(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW)));
                        mObject.setRelease_date(c.getString(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE)));
                        mObject.setGenre_ids(Utils.getGenreArray(c.getString(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_GENRE_IDS))));
                        mObject.setId(c.getInt(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ID)));
                        mObject.setOriginal_title(c.getString(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE)));
                        mObject.setOriginal_language(c.getString(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE)));
                        mObject.setTitle(c.getString(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE)));
                        mObject.setBackdrop_path(c.getString(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH)));
                        mObject.setPopularity(c.getDouble(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_POPULARITY)));
                        mObject.setVote_count(c.getInt(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT)));
                        mObject.setVideo(c.getInt(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VIDEO)) > 0);
                        mObject.setVote_average(c.getDouble(c.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE)));

                        movieObjects.add(mObject);
                    }
                    c.close();
                    return new String[]{AppCodes.CALLBACK_QUERY_FAVORITE_LIST};
                } else Log.d(TAG, "Query Failed");
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        String request = strings[0];

        if (!request.equals(AppCodes.CALLBACK_QUERY_FAVORITE_LIST))
            mCallback.CallbackRequest(request, strings[1]);
        else mCallback.CallbackRequest(request, movieObjects);
    }
}

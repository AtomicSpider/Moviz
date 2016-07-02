package com.satandigital.moviz.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.satandigital.moviz.callbacks.MovizCallback;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;
import com.satandigital.moviz.database.MoviesContract;
import com.satandigital.moviz.models.MovieObject;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 7/2/2016.
 */
public class DatabaseTask extends AsyncTask<String, Void, String[]> {

    MovizCallback mCallback;
    Context mContext;
    MovieObject movieObject;

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
            case AppCodes.TASK_ADD_FAVORITE:
                Uri success = mContext.getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, Utils.getContentValues(movieObject));
                if (success != null)
                    return new String[]{AppCodes.CALLBACK_ADD_FAVORITE, "true"};
                else
                    return new String[]{AppCodes.CALLBACK_ADD_FAVORITE, "false"};
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        String request = strings[0];
        String data = strings[1];

        mCallback.CallbackRequest(request, data);
    }
}

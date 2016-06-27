package com.satandigital.moviz.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/26/2016.
 */
public class MoviesProvider extends ContentProvider {

    private final String TAG = MoviesProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper moviesDbHelper;

    static final int MOVIE_LIST = 100;
    static final int MOVIE = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_FAV_MOVIES, MOVIE_LIST);
        matcher.addURI(authority, MoviesContract.PATH_FAV_MOVIES + "/#", MOVIE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_LIST:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            default:
                Log.e(TAG, "Unknown uri");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MoviesEntry._ID + " = ?",
                        new String[]{MoviesContract.MoviesEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_LIST:
                retCursor = moviesDbHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                Log.e(TAG, "Unknown uri");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE_LIST:
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = MoviesContract.MoviesEntry.buildMovieUri(_id);
                else {
                    Log.e(TAG, "Failed to insert row into " + uri);
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                Log.e(TAG, "Unknown uri");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MoviesContract.MoviesEntry.TABLE_NAME,
                        MoviesContract.MoviesEntry._ID + " = ?",
                        new String[]{MoviesContract.MoviesEntry.getMovieIdFromUri(uri)});
                break;
            default:
                Log.e(TAG, "Unknown uri");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}

package com.satandigital.moviz.common;

import android.content.ContentValues;
import android.content.SharedPreferences;

import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.database.MoviesContract;
import com.satandigital.moviz.models.objects.MovieObject;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public final class Utils {

    private Utils() {
    }

    public static void saveToSharedPreferences(String key, String sValue) {
        SharedPreferences.Editor mEditor = MovizApp.mSharedPreferences.edit();
        mEditor.putString(key, sValue);
        mEditor.apply();

        //Update local reference
        MovizApp.savedMovieListType = sValue;
    }

    public static String readSharedPreferences(String key, String valueDefault) {
        return MovizApp.mSharedPreferences.getString(key, valueDefault);
    }

    public static String getGenre(int[] genre_ids) {
        StringBuilder genreBuilder = new StringBuilder();

        for (int i = 0; i < genre_ids.length; i++) {
            switch (genre_ids[i]) {
                case 28:
                    genreBuilder.append("Action");
                    break;
                case 12:
                    genreBuilder.append("Adventure");
                    break;
                case 16:
                    genreBuilder.append("Animation");
                    break;
                case 35:
                    genreBuilder.append("Comedy");
                    break;
                case 80:
                    genreBuilder.append("Crime");
                    break;
                case 99:
                    genreBuilder.append("Documentary");
                    break;
                case 18:
                    genreBuilder.append("Drama");
                    break;
                case 10751:
                    genreBuilder.append("Family");
                    break;
                case 14:
                    genreBuilder.append("Fantasy");
                    break;
                case 10769:
                    genreBuilder.append("Foreign");
                    break;
                case 36:
                    genreBuilder.append("History");
                    break;
                case 27:
                    genreBuilder.append("Horror");
                    break;
                case 10402:
                    genreBuilder.append("Music");
                    break;
                case 9648:
                    genreBuilder.append("Mystery");
                    break;
                case 10749:
                    genreBuilder.append("Romance");
                    break;
                case 878:
                    genreBuilder.append("SciFi");
                    break;
                case 10770:
                    genreBuilder.append("TV Movie");
                    break;
                case 53:
                    genreBuilder.append("Thriller");
                    break;
                case 10752:
                    genreBuilder.append("War");
                    break;
                case 37:
                    genreBuilder.append("Western");
                    break;
            }
            if (i != genre_ids.length - 1) genreBuilder.append(" | ");
        }
        return genreBuilder.toString();
    }

    public static ContentValues getContentValues(MovieObject movieObject) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_PATH, movieObject.getPoster_path());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_ADULT, movieObject.isAdult() ? 1 : 0);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movieObject.getOverview());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movieObject.getRelease_date());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_GENRE_IDS, getGenreString(movieObject.getGenre_ids()));
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_ID, movieObject.getId());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE, movieObject.getOriginal_title());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_ORIGINAL_LANGUAGE, movieObject.getOriginal_language());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movieObject.getTitle());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH, movieObject.getBackdrop_path());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movieObject.getPopularity());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_COUNT, movieObject.getVote_count());
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_VIDEO, movieObject.isVideo() ? 1 : 0);
        contentValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, movieObject.getVote_average());

        return contentValues;
    }

    public static String getGenreString(int[] genre_ids) {
        String genreIdsStr = "";

        for (int i = 0; i < genre_ids.length; i++) {
            genreIdsStr += String.valueOf(genre_ids[i]);
            if (i != genre_ids.length - 1) genreIdsStr += ",";
        }

        return genreIdsStr;
    }

    public static int[] getGenreArray(String string) {
        String[] strArray = string.split(",");
        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]);
        }
        return intArray;
    }
}

package com.satandigital.moviz.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

import com.satandigital.moviz.MovizApp;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class Utils {

    public static void saveToSharedPreferences(String key, String sValue) {
        SharedPreferences.Editor mEditor = MovizApp.mSharedPreferences.edit();
        mEditor.putString(key, sValue);
        mEditor.apply();
    }

    public static void saveToSharedPreferences(String key, boolean bValue) {
        SharedPreferences.Editor mEditor = MovizApp.mSharedPreferences.edit();
        mEditor.putBoolean(key, bValue);
        mEditor.apply();
    }

    public static boolean readSharedPreferences(String key, boolean valueDefault) {
        return MovizApp.mSharedPreferences.getBoolean(key, valueDefault);
    }

    public static String readSharedPreferences(String key, String valueDefault) {
        return MovizApp.mSharedPreferences.getString(key, valueDefault);
    }

    public static String getGenre(int[] genre_ids) {
        String genre = "";
        for (int i = 0; i < genre_ids.length; i++) {
            if (genre_ids[i] == 28)
                genre += "Action";
            if (genre_ids[i] == 12)
                genre += "Adventure";
            if (genre_ids[i] == 16)
                genre += "Animation";
            if (genre_ids[i] == 35)
                genre += "Comedy";
            if (genre_ids[i] == 80)
                genre += "Crime";
            if (genre_ids[i] == 99)
                genre += "Documentary";
            if (genre_ids[i] == 18)
                genre += "Drama";
            if (genre_ids[i] == 10751)
                genre += "Family";
            if (genre_ids[i] == 14)
                genre += "Fantasy";
            if (genre_ids[i] == 10769)
                genre += "Foreign";
            if (genre_ids[i] == 36)
                genre += "History";
            if (genre_ids[i] == 27)
                genre += "Horror";
            if (genre_ids[i] == 10402)
                genre += "Music";
            if (genre_ids[i] == 9648)
                genre += "Mystery";
            if (genre_ids[i] == 10749)
                genre += "Romance";
            if (genre_ids[i] == 878)
                genre += "SciFi";
            if (genre_ids[i] == 10770)
                genre += "TV Movie";
            if (genre_ids[i] == 53)
                genre += "Thriller";
            if (genre_ids[i] == 10752)
                genre += "War";
            if (genre_ids[i] == 37)
                genre += "Western";

            if (i != genre_ids.length - 1) genre += " | ";
        }
        return genre;
    }
}

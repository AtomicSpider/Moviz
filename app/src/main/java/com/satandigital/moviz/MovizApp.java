package com.satandigital.moviz;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class MovizApp extends Application {

    private String TAG = MovizApp.class.getSimpleName();

    public static SharedPreferences mSharedPreferences;
    public static String savedMovieListType;
    public static boolean itemAutoSelect = true;

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPreferences = getSharedPreferences(AppCodes.prefName, Context.MODE_PRIVATE);
        savedMovieListType = Utils.readSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, AppCodes.PREF_MOVIE_LIST_POPULAR);

        //Switch to popular if in search state
        if (AppCodes.PREF_MOVIE_LIST_SEARCH.equals(savedMovieListType))
            savedMovieListType = AppCodes.PREF_MOVIE_LIST_POPULAR;
        Log.i(TAG, "Pref: " + savedMovieListType);
    }
}

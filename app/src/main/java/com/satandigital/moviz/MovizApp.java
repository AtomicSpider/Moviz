package com.satandigital.moviz;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
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
    public static String movieListType;
    public static boolean isPreLollipop = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPreferences = getSharedPreferences(AppCodes.prefName, Context.MODE_PRIVATE);
        movieListType = Utils.readSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, AppCodes.PREF_MOVIE_LIST_POPULAR);
        Log.i(TAG, "Pref: " + movieListType);

        isPreLollipop = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }
}

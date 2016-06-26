package com.satandigital.moviz;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class MovizApp extends Application {

    private String TAG = MovizApp.class.getSimpleName();

    public static SharedPreferences mSharedPreferences;
    public static String sortType;

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPreferences = getSharedPreferences(AppCodes.prefName, Context.MODE_PRIVATE);
        sortType = Utils.readSharedPreferences(AppCodes.PREF_SORT_TYPE, AppCodes.PREF_SORT_POPULAR);

    }
}

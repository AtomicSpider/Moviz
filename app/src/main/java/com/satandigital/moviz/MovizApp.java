package com.satandigital.moviz;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class MovizApp extends Application {

    private String TAG = MovizApp.class.getSimpleName();

    //Data
    public static final String prefName = "MOVIZ_PREF";

    public static SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }
}

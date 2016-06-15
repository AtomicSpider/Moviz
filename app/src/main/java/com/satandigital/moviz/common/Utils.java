package com.satandigital.moviz.common;

import android.content.SharedPreferences;

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

}

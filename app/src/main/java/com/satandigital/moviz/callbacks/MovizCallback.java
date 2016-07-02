package com.satandigital.moviz.callbacks;

import android.database.Cursor;

import com.satandigital.moviz.models.MovieObject;

import java.util.ArrayList;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/30/2016.
 */
public interface MovizCallback {
    void CallbackRequest(String request, String data);
    void CallbackRequest(String request, ArrayList<MovieObject> movieObjects);
}




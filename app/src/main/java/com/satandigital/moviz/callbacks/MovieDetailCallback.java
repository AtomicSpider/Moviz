package com.satandigital.moviz.callbacks;

import android.os.Bundle;

import com.satandigital.moviz.models.MovieObject;

import java.util.ArrayList;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/30/2016.
 */
public interface MovieDetailCallback {
    void CallbackRequest(String request, Bundle movieBundle);
}




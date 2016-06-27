package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.models.MovieObject;
import com.satandigital.moviz.models.TmdbRawObject;
import com.satandigital.moviz.retrofit.TmdbClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/14/2016.
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    //Views
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        setContentView(R.layout.activity_main);
    }

    private void setupActionBar() {
        mActionBar = getSupportActionBar();
        if (MovizApp.movieListType.equals(AppCodes.CODE_TITLE_POPULAR))
            mActionBar.setTitle("Popular");
        else if (MovizApp.movieListType.equals(AppCodes.CODE_TITLE_TOP_RATED))
            mActionBar.setTitle("Top Rated");
    }
}

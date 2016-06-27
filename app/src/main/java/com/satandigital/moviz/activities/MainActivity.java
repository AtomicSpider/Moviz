package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.common.AppCodes;

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

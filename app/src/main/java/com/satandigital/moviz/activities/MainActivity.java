package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.callbacks.MovieDetailCallback;
import com.satandigital.moviz.callbacks.MovizCallback;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.fragments.DetailsFragment;
import com.satandigital.moviz.models.MovieObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/14/2016.
 */
public class MainActivity extends AppCompatActivity implements MovizCallback, MovieDetailCallback {

    private final String TAG = MainActivity.class.getSimpleName();
    private final String DETAIL_FRAG_TAG = "DETAIL_FRAG_TAG";

    public static MovizCallback mCallback;

    //Views
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.spinner_toolbar)
    Spinner spinner_toolbar;

    //Data
    private boolean isSpinnerTouched = false;
    public static boolean twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        twoPane = findViewById(R.id.movie_details_container) != null;

        setUpToolbar();
        setUpSpinner();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setUpSpinner() {
        spinner_toolbar = (Spinner) findViewById(R.id.spinner_toolbar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, R.layout.spinner_item_actionbar);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_actionbar);
        spinner_toolbar.setAdapter(adapter);

        //Load spinner state from preference
        switch (MovizApp.movieListType) {
            case AppCodes.PREF_MOVIE_LIST_POPULAR:
                spinner_toolbar.setSelection(0);
                break;
            case AppCodes.PREF_MOVIE_LIST_TOP_RATED:
                spinner_toolbar.setSelection(1);
                break;
            case AppCodes.PREF_MOVIE_LIST_FAVORITES:
                spinner_toolbar.setSelection(2);
                break;
        }

        //Only on user touch
        spinner_toolbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isSpinnerTouched = true;
                return false;
            }
        });

        spinner_toolbar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isSpinnerTouched) {
                    isSpinnerTouched = false;
                    switch (i) {
                        case 0:
                            Log.i(TAG, "Popular selected");
                            mCallback.CallbackRequest(AppCodes.CALLBACK_FETCH_MOVIES_WITH_TYPE, AppCodes.PREF_MOVIE_LIST_POPULAR);
                            break;
                        case 1:
                            Log.i(TAG, "Top Rated selected");
                            mCallback.CallbackRequest(AppCodes.CALLBACK_FETCH_MOVIES_WITH_TYPE, AppCodes.PREF_MOVIE_LIST_TOP_RATED);
                            break;
                        case 2:
                            Log.i(TAG, "Favorites selected");
                            mCallback.CallbackRequest(AppCodes.CALLBACK_FETCH_MOVIES_WITH_TYPE, AppCodes.PREF_MOVIE_LIST_FAVORITES);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public static void setCallback(MovizCallback callback) {
        mCallback = callback;
    }

    @Override
    public void CallbackRequest(String request, String data) {
        if (request.equals(AppCodes.CALLBACK_TOGGLE_SPINNER)) {
            if (data.equals("ENABLE"))
                spinner_toolbar.setEnabled(true);
            else if (data.equals("DISABLE"))
                spinner_toolbar.setEnabled(false);
        } else if (request.equals(AppCodes.CALLBACK_REFRESH_FAVORITES)) {
            Log.i(TAG, "Refresh Favorites List");
            mCallback.CallbackRequest(AppCodes.CALLBACK_REFRESH_FAVORITES, "");
        }
    }

    @Override
    public void CallbackRequest(String request, ArrayList<MovieObject> movieObjects) {

    }

    @Override
    public void CallbackRequest(String request, Bundle movieBundle) {

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(movieBundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_details_container, fragment, DETAIL_FRAG_TAG)
                .commit();
    }
}

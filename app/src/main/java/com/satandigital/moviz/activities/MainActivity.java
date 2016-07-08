package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.callbacks.MovieDetailCallback;
import com.satandigital.moviz.callbacks.MovizCallback;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.fragments.DetailsFragment;
import com.satandigital.moviz.models.MovieObject;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/14/2016.
 */
public class MainActivity extends AppCompatActivity implements MovizCallback, MovieDetailCallback {

    private final String TAG = MainActivity.class.getSimpleName();

    public static MovizCallback mCallback;

    //Data
    public static boolean twoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        twoPane = findViewById(R.id.movie_details_container) != null;

        setUpTitle();
    }

    private void setUpTitle() {

        //Set title from preference
        switch (MovizApp.movieListType) {
            case AppCodes.PREF_MOVIE_LIST_POPULAR:
                setTitle("Popular");
                break;
            case AppCodes.PREF_MOVIE_LIST_TOP_RATED:
                setTitle("Top Rated");
                break;
            case AppCodes.PREF_MOVIE_LIST_FAVORITES:
                setTitle("Favorites");
                break;
        }
    }

    public static void setCallback(MovizCallback callback) {
        mCallback = callback;
    }

    @Override
    public void CallbackRequest(String request, String data) {
        if (request.equals(AppCodes.CALLBACK_REFRESH_FAVORITES)) {
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

        String DETAIL_FRAG_TAG = "DETAIL_FRAG_TAG";
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_details_container, fragment, DETAIL_FRAG_TAG)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}

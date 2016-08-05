package com.satandigital.moviz.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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
import com.satandigital.moviz.models.objects.MovieObject;

import java.util.ArrayList;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/14/2016.
 */
public class MainActivity extends AppCompatActivity implements MovizCallback, MovieDetailCallback {

    private final String TAG = MainActivity.class.getSimpleName();

    public static MovizCallback mCallback;

    //Data
    public static boolean twoPane = false;
    private boolean hideSortMenu = false;
    private String queryString = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twoPane = findViewById(R.id.movie_details_container) != null;
        setUpTitle();

        if (savedInstanceState != null)
            queryString = savedInstanceState.getString(AppCodes.KEY_CURRENT_QUERY_STRING);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i(TAG, "New Intent");

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "Query parameter:" + query);

            mCallback.CallbackRequest(AppCodes.CALLBACK_VIEW_SEARCH_RESULTS, query);
        }
    }

    private void setUpTitle() {

        //Set title from preference
        switch (MovizApp.savedMovieListType) {
            case AppCodes.PREF_MOVIE_LIST_POPULAR:
                setTitle("Popular");
                break;
            case AppCodes.PREF_MOVIE_LIST_TOP_RATED:
                setTitle("Top Rated");
                break;
            case AppCodes.PREF_MOVIE_LIST_NOW_PLAYING:
                setTitle("Now Playing");
                break;
            case AppCodes.PREF_MOVIE_LIST_UPCOMING:
                setTitle("Upcoming");
                break;
            case AppCodes.PREF_MOVIE_LIST_FAVORITES:
                setTitle("Favorites");
                break;
            case AppCodes.PREF_MOVIE_LIST_SEARCH:
                setTitle("Search");
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

        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if (queryString != null) {
            searchItem.expandActionView();
            searchView.setQuery(queryString, false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryString = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "Search Expanded");
                hideSortMenu = true;
                invalidateOptionsMenu();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                Log.d(TAG, "Search Collapsed");
                hideSortMenu = false;
                invalidateOptionsMenu();
                queryString = null;
                mCallback.CallbackRequest(AppCodes.CALLBACK_FETCH_POPULAR, "");
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        Log.i(TAG, "OnPrepare()");
        menu.findItem(R.id.action_popular).setEnabled(!hideSortMenu);
        menu.findItem(R.id.action_top_rated).setEnabled(!hideSortMenu);
        menu.findItem(R.id.action_now_playing).setEnabled(!hideSortMenu);
        menu.findItem(R.id.action_upcoming).setEnabled(!hideSortMenu);
        menu.findItem(R.id.action_favorites).setEnabled(!hideSortMenu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(AppCodes.KEY_CURRENT_QUERY_STRING, queryString);
    }
}

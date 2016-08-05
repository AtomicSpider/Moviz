package com.satandigital.moviz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.activities.MainActivity;
import com.satandigital.moviz.adapters.MoviesRecyclerViewAdapter;
import com.satandigital.moviz.asynctasks.DatabaseTask;
import com.satandigital.moviz.callbacks.MovieDetailCallback;
import com.satandigital.moviz.callbacks.MovizCallback;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;
import com.satandigital.moviz.models.objects.MovieObject;
import com.satandigital.moviz.models.views.RecyclerViewWithEmptyView;
import com.satandigital.moviz.models.objects.TmdbRawMoviesObject;
import com.satandigital.moviz.retrofit.TmdbClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/26/2016.
 */
public class MoviesFragment extends Fragment implements MovizCallback, MovieDetailCallback {

    private String TAG = MoviesFragment.class.getSimpleName();

    private MoviesRecyclerViewAdapter mAdapter;
    private MovieDetailCallback movieDetailCallback;

    //Views
    @BindView(R.id.recycler_view)
    RecyclerViewWithEmptyView mRecyclerView;
    @BindView(R.id.emptyView)
    TextView emptyView;
    @BindView(R.id.progress_bar)
    CircularProgressView mProgressView;

    //Data
    private boolean isFetchOngoing = false;
    private boolean firstFetch = true;
    private boolean manualMenuCheck = false;
    private String movieListType;
    private int currentPage = 1;
    private String queryString = "Batman";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, rootView);

        setRecyclerView();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        movieListType = MovizApp.savedMovieListType;
        MainActivity.setCallback(this);

        movieDetailCallback = (MovieDetailCallback) getActivity();

        if (savedInstanceState == null) fetchMovies(null, 1, movieListType);
        else {
            firstFetch = savedInstanceState.getBoolean(AppCodes.KEY_FIRST_FETCH);
            movieListType = savedInstanceState.getString(AppCodes.KEY_MOVIE_LIST_TYPE);
            currentPage = savedInstanceState.getInt(AppCodes.KEY_CURRENT_PAGE);
            queryString = savedInstanceState.getString(AppCodes.KEY_CURRENT_QUERY_STRING);
            ArrayList<MovieObject> movieObjects = savedInstanceState.getParcelableArrayList(AppCodes.KEY_MOVIE_OBJECTS);

            populateRecyclerView(movieListType, movieObjects);
            mRecyclerView.scrollToPosition(savedInstanceState.getInt(AppCodes.KEY_LIST_POSITION));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //ToDo stop repeat at start
        //if (movieListType.equals(AppCodes.PREF_MOVIE_LIST_FAVORITES)) fetchMovies(1, movieListType);
    }

    private void setRecyclerView() {
        int columnSize = getResources().getInteger(R.integer.grid_size);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnSize));
        mAdapter = new MoviesRecyclerViewAdapter(getActivity());
        mAdapter.setCallback(this);
        mAdapter.setMovieDetailCallback(this);
        mRecyclerView.setUpEmptyView(getActivity(), emptyView);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void populateRecyclerView(String movieListType, ArrayList<MovieObject> movieObjects) {
        if (movieObjects == null || movieObjects.size() == 0) {
            if (AppCodes.PREF_MOVIE_LIST_FAVORITES.equals(movieListType)) {
                mRecyclerView.setState(AppCodes.STATE_NO_FAV);
                mAdapter.populateView(false, false, new ArrayList<MovieObject>());
                Toast.makeText(getActivity(), "No movies added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                if (movieListType.equals(MovizApp.savedMovieListType) && !firstFetch)
                    mAdapter.populateView(false, true, new ArrayList<MovieObject>());
                else {
                    //ToDo check network
                    mRecyclerView.setState(AppCodes.STATE_NO_NETWORK);
                    mAdapter.populateView(false, false, new ArrayList<MovieObject>());
                    Toast.makeText(getActivity(), "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (AppCodes.PREF_MOVIE_LIST_FAVORITES.equals(movieListType)) {
                mAdapter.populateView(false, false, movieObjects);
                mRecyclerView.scrollToPosition(0);
            } else {
                if (currentPage == 1) {
                    mAdapter.populateView(true, false, movieObjects);
                    mRecyclerView.scrollToPosition(0);
                } else mAdapter.populateView(true, true, movieObjects);
            }
        }

        firstFetch = false;
    }

    @Override
    public void CallbackRequest(String request, String data) {
        if (request.equals(AppCodes.CALLBACK_FETCH_MOVIES_WITH_PAGE) && !isFetchOngoing)
            fetchMovies(queryString, currentPage + 1, movieListType);
        else if (request.equals(AppCodes.CALLBACK_REFRESH_FAVORITES) && !isFetchOngoing) {
            Log.i(TAG, "Refresh Favorites List");
            fetchMovies(null, 1, movieListType);
        } else if (request.equals(AppCodes.CALLBACK_VIEW_SEARCH_RESULTS)) {
            if (!isFetchOngoing) {
                Log.i(TAG, "Search Movies");
                getActivity().setTitle("Search");
                queryString = data;
                movieListType = AppCodes.PREF_MOVIE_LIST_SEARCH;
                fetchMovies(queryString, 1, movieListType);
            } else
                Toast.makeText(getActivity(), "Process already running, Please wait...", Toast.LENGTH_SHORT).show();
        } else if (request.equals(AppCodes.CALLBACK_FETCH_POPULAR) && !isFetchOngoing) {
            Log.i(TAG, "Searchview closed, fetch popular");
            getActivity().setTitle("Popular");
            movieListType = AppCodes.PREF_MOVIE_LIST_POPULAR;
            fetchMovies(null, 1, movieListType);
            manualMenuCheck = true;
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void CallbackRequest(String request, ArrayList<MovieObject> movieObjects) {
        //Data from database query

        currentPage = 1;

        populateRecyclerView(movieListType, movieObjects);
        saveIfNecessary(movieListType);

        isFetchOngoing = false;
        toggleProgressBar(false);
    }

    @Override
    public void CallbackRequest(String request, Bundle movieBundle) {
        movieDetailCallback.CallbackRequest(AppCodes.CALLBACK_MOVIE_BUNDLE, movieBundle);
    }

    private void fetchMovies(String queryStr, int nextPage, final String mListType) {
        Log.i(TAG, "Fetch movies, Page: " + nextPage + " ListType: " + mListType);
        isFetchOngoing = true;
        toggleProgressBar(true);

        if (!mListType.equals(AppCodes.PREF_MOVIE_LIST_FAVORITES)) {
            Call<TmdbRawMoviesObject> call = null;
            switch (mListType) {
                case AppCodes.PREF_MOVIE_LIST_POPULAR:
                    call = TmdbClient.getInstance(getActivity())
                            .getPopularMoviesClient()
                            .getPopularMovies(nextPage);
                    break;
                case AppCodes.PREF_MOVIE_LIST_TOP_RATED:
                    call = TmdbClient.getInstance(getActivity())
                            .getTopRatedMoviesClient()
                            .getTopRatedMovies(nextPage);
                    break;
                case AppCodes.PREF_MOVIE_LIST_NOW_PLAYING:
                    call = TmdbClient.getInstance(getActivity())
                            .getNowPlayingMoviesClient()
                            .getNowPlayingMovies(nextPage);
                    break;
                case AppCodes.PREF_MOVIE_LIST_UPCOMING:
                    call = TmdbClient.getInstance(getActivity())
                            .getUpcomingMoviesClient()
                            .getUpcomingMovies(nextPage);
                    break;
                case AppCodes.PREF_MOVIE_LIST_SEARCH:
                    call = TmdbClient.getInstance(getActivity())
                            .getSearchMoviesClient()
                            .getSearchMovies(queryStr, nextPage);
                    break;
            }
            Callback<TmdbRawMoviesObject> callback = new Callback<TmdbRawMoviesObject>() {
                @Override
                public void onResponse(Call<TmdbRawMoviesObject> call, Response<TmdbRawMoviesObject> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "Retrofit Response Successful");

                        currentPage = response.body().getPage();
                        populateRecyclerView(movieListType, response.body().getResults());
                    } else {
                        Log.e(TAG, "Retrofit Response Failure" + response.message());
                        populateRecyclerView(movieListType, new ArrayList<MovieObject>());
                    }

                    saveIfNecessary(movieListType);
                    fetchEnded();
                }

                @Override
                public void onFailure(Call<TmdbRawMoviesObject> call, Throwable t) {
                    Log.e(TAG, "Retrofit Failure" + t.getMessage());
                    populateRecyclerView(movieListType, new ArrayList<MovieObject>());

                    saveIfNecessary(movieListType);
                    fetchEnded();
                }

                private void fetchEnded() {
                    isFetchOngoing = false;
                    toggleProgressBar(false);
                }
            };
            call.enqueue(callback);
        } else {
            new DatabaseTask(this, getActivity(), null).execute(AppCodes.TASK_QUERY_FAVORITE_LIST, null);
        }
    }

    private void toggleProgressBar(boolean visible) {
        if (visible) mProgressView.setVisibility(View.VISIBLE);
        else mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(AppCodes.KEY_MOVIE_OBJECTS, mAdapter.getMovieObjects());
        outState.putInt(AppCodes.KEY_LIST_POSITION, mAdapter.getPosition());
        outState.putString(AppCodes.KEY_MOVIE_LIST_TYPE, movieListType);
        outState.putInt(AppCodes.KEY_CURRENT_PAGE, currentPage);
        outState.putString(AppCodes.KEY_CURRENT_QUERY_STRING, queryString);
        outState.putBoolean(AppCodes.KEY_FIRST_FETCH, firstFetch);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_details, menu);

        switch (MovizApp.savedMovieListType) {
            case AppCodes.PREF_MOVIE_LIST_POPULAR:
                menu.findItem(R.id.action_popular).setChecked(true);
                break;
            case AppCodes.PREF_MOVIE_LIST_TOP_RATED:
                menu.findItem(R.id.action_top_rated).setChecked(true);
                break;
            case AppCodes.PREF_MOVIE_LIST_NOW_PLAYING:
                menu.findItem(R.id.action_now_playing).setChecked(true);
                break;
            case AppCodes.PREF_MOVIE_LIST_UPCOMING:
                menu.findItem(R.id.action_upcoming).setChecked(true);
                break;
            case AppCodes.PREF_MOVIE_LIST_FAVORITES:
                menu.findItem(R.id.action_favorites).setChecked(true);
                break;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (manualMenuCheck)
            menu.findItem(R.id.action_popular).setChecked(true);
        manualMenuCheck = false;
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (!isFetchOngoing && !item.isChecked() && itemId != R.id.action_sort && itemId != R.id.search) {
            switch (itemId) {
                case R.id.action_popular:
                    Log.d(TAG, "Popular");
                    getActivity().setTitle("Popular");
                    movieListType = AppCodes.PREF_MOVIE_LIST_POPULAR;
                    break;
                case R.id.action_top_rated:
                    Log.d(TAG, "Top Rated");
                    getActivity().setTitle("Top Rated");
                    movieListType = AppCodes.PREF_MOVIE_LIST_TOP_RATED;
                    break;
                case R.id.action_now_playing:
                    Log.d(TAG, "Now Playing");
                    getActivity().setTitle("Now Playing");
                    movieListType = AppCodes.PREF_MOVIE_LIST_NOW_PLAYING;
                    break;
                case R.id.action_upcoming:
                    Log.d(TAG, "Upcoming");
                    getActivity().setTitle("Upcoming");
                    movieListType = AppCodes.PREF_MOVIE_LIST_UPCOMING;
                    break;
                case R.id.action_favorites:
                    Log.d(TAG, "Favorites");
                    getActivity().setTitle("Favorites");
                    movieListType = AppCodes.PREF_MOVIE_LIST_FAVORITES;
                    break;
            }
            item.setChecked(true);
            fetchMovies(null, 1, movieListType);
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveIfNecessary(String type) {
        if (!type.equals(MovizApp.savedMovieListType)) {
            Log.d(TAG, "Saving pref as: " + type);
            Utils.saveToSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, type);
        }
    }
}

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
import com.satandigital.moviz.models.MovieObject;
import com.satandigital.moviz.models.RecyclerViewWithEmptyView;
import com.satandigital.moviz.models.TmdbRawMoviesObject;
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
    private String movieListType;
    private int currentPage = 1;
    private boolean firstDisplay = true;

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

        movieListType = MovizApp.movieListType;
        MainActivity.setCallback(this);

        movieDetailCallback = (MovieDetailCallback) getActivity();

        if (savedInstanceState == null) fetchMovies(1, movieListType);
        else {
            currentPage = savedInstanceState.getInt(AppCodes.KEY_CURRENT_PAGE);
            firstDisplay = savedInstanceState.getBoolean(AppCodes.KEY_FIRST_DISPLAY);
            movieListType = savedInstanceState.getString(AppCodes.KEY_MOVIE_LIST_TYPE);
            ArrayList<MovieObject> movieObjects = savedInstanceState.getParcelableArrayList(AppCodes.KEY_MOVIE_OBJECTS);
            if (movieListType.equals(AppCodes.PREF_MOVIE_LIST_FAVORITES)) mAdapter.setPaging(false);
            else mAdapter.setPaging(true);
            mAdapter.clearAllAndPopulate(movieObjects);
            mRecyclerView.scrollToPosition(savedInstanceState.getInt(AppCodes.KEY_LIST_POSITION));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (movieListType.equals(AppCodes.PREF_MOVIE_LIST_FAVORITES)) fetchMovies(1, movieListType);
    }

    private void setRecyclerView() {
        int columnSize = getResources().getInteger(R.integer.grid_size);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnSize));
        mAdapter = new MoviesRecyclerViewAdapter(getActivity());
        mAdapter.setCallback(this);
        mAdapter.setMovieDetailCallback(this);
        mRecyclerView.setEmptyView(emptyView);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void CallbackRequest(String request, String data) {
        if (request.equals(AppCodes.CALLBACK_FETCH_MOVIES_WITH_PAGE) && !isFetchOngoing)
            fetchMovies(currentPage + 1, movieListType);
        else if (request.equals(AppCodes.CALLBACK_REFRESH_FAVORITES) && !isFetchOngoing) {
            Log.i(TAG, "Refresh Favorites List");
            fetchMovies(1, movieListType);
        }
    }

    @Override
    public void CallbackRequest(String request, ArrayList<MovieObject> movieObjects) {
        currentPage = 1;
        firstDisplay = false;
        Log.d(TAG, "Saving pref as: " + movieListType);
        Utils.saveToSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, movieListType);
        if (movieObjects != null && movieObjects.size() > 0) {
            mAdapter.setPaging(false);
            mAdapter.clearAllAndPopulate(movieObjects);
        } else {
            mAdapter.setPaging(false);
            mAdapter.clearAllAndPopulate(new ArrayList<MovieObject>());
            Toast.makeText(getActivity(), "No movies added to Favorites", Toast.LENGTH_SHORT).show();
        }

        isFetchOngoing = false;
        toggleProgressBar(false);
    }

    @Override
    public void CallbackRequest(String request, Bundle movieBundle) {
        movieDetailCallback.CallbackRequest(AppCodes.CALLBACK_MOVIE_BUNDLE, movieBundle);
    }

    private void fetchMovies(int nextPage, final String mListType) {
        Log.i(TAG, "Fetch movies, Page: " + nextPage + " ListType: " + mListType);
        isFetchOngoing = true;
        toggleProgressBar(true);

        if (!mListType.equals(AppCodes.PREF_MOVIE_LIST_FAVORITES)) {
            Call<TmdbRawMoviesObject> call = null;
            if (mListType.equals(AppCodes.PREF_MOVIE_LIST_POPULAR)) {
                call = TmdbClient.getInstance(getActivity())
                        .getPopularMoviesClient()
                        .getPopularMovies(nextPage);
            } else if (mListType.equals(AppCodes.PREF_MOVIE_LIST_TOP_RATED)) {
                call = TmdbClient.getInstance(getActivity())
                        .getTopRatedMoviesClient()
                        .getTopRatedMovies(nextPage);
            }
            Callback<TmdbRawMoviesObject> callback = new Callback<TmdbRawMoviesObject>() {
                @Override
                public void onResponse(Call<TmdbRawMoviesObject> call, Response<TmdbRawMoviesObject> response) {
                    if (response.isSuccessful()) {
                        firstDisplay = false;
                        Log.i(TAG, "Retrofit Response Successful");

                        currentPage = response.body().getPage();
                        mAdapter.setPaging(true);
                        if (currentPage == 1)
                            mAdapter.clearAllAndPopulate(response.body().getResults());
                        else mAdapter.addItemsAndPopulate(response.body().getResults());
                    } else {
                        Toast.makeText(getActivity(), "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Retrofit Response Failure" + response.message());
                        CleanRecyclerView();
                    }
                    Log.d(TAG, "Saving pref as: " + movieListType);
                    Utils.saveToSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, movieListType);

                    fetchEnded();
                }

                @Override
                public void onFailure(Call<TmdbRawMoviesObject> call, Throwable t) {
                    CleanRecyclerView();

                    Log.d(TAG, "Saving pref as: " + movieListType);
                    Utils.saveToSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, movieListType);

                    Toast.makeText(getActivity(), "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Retrofit Failure" + t.getMessage());

                    fetchEnded();
                }

                private void CleanRecyclerView() {

                    if (!movieListType.equals(MovizApp.movieListType) || firstDisplay)
                        mAdapter.clearAllAndPopulate(new ArrayList<MovieObject>());

                    firstDisplay = false;
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
        outState.putInt(AppCodes.KEY_LIST_POSITION, mAdapter.getListPosition());
        outState.putString(AppCodes.KEY_MOVIE_LIST_TYPE, movieListType);
        outState.putInt(AppCodes.KEY_CURRENT_PAGE, currentPage);
        outState.putBoolean(AppCodes.KEY_FIRST_DISPLAY, firstDisplay);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_details, menu);

        switch (MovizApp.movieListType) {
            case AppCodes.PREF_MOVIE_LIST_POPULAR:
                menu.findItem(R.id.action_popular).setChecked(true);
                break;
            case AppCodes.PREF_MOVIE_LIST_TOP_RATED:
                menu.findItem(R.id.action_top_rated).setChecked(true);
                break;
            case AppCodes.PREF_MOVIE_LIST_FAVORITES:
                menu.findItem(R.id.action_favorites).setChecked(true);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (!isFetchOngoing && !item.isChecked() && itemId != R.id.action_sort) {
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
                case R.id.action_favorites:
                    Log.d(TAG, "Favorites");
                    getActivity().setTitle("Favorites");
                    movieListType = AppCodes.PREF_MOVIE_LIST_FAVORITES;
                    break;
            }
            item.setChecked(true);
            fetchMovies(1, movieListType);
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.satandigital.moviz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.adapters.MoviesRecyclerViewAdapter;
import com.satandigital.moviz.adapters.MoviesRecyclerViewAdapter.AdapterCallback;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;
import com.satandigital.moviz.models.MovieObject;
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
public class MoviesFragment extends Fragment implements AdapterCallback {

    private String TAG = MoviesFragment.class.getSimpleName();

    private MoviesRecyclerViewAdapter mAdapter;

    //Views
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    CircularProgressView mProgressView;

    //Data
    private boolean isFetchOngoing = false;
    private String movieListType;
    private int currentPage = 1;

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

        if (savedInstanceState == null) fetchMovies(1, movieListType);
        else {
            currentPage = savedInstanceState.getInt(AppCodes.KEY_CURRENT_PAGE);
            movieListType = savedInstanceState.getString(AppCodes.KEY_MOVIE_LIST_TYPE);
            ArrayList<MovieObject> movieObjects = savedInstanceState.getParcelableArrayList(AppCodes.KEY_MOVIE_OBJECTS);
            if (movieObjects != null) {
                mAdapter.clearAllAndPopulate(movieObjects);
                mRecyclerView.scrollToPosition(savedInstanceState.getInt(AppCodes.KEY_LIST_POSITION));
            }
        }

        return rootView;
    }

    private void setRecyclerView() {
        int columnSize = Integer.parseInt(getResources().getString(R.string.grid_size));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnSize));
        mAdapter = new MoviesRecyclerViewAdapter(getActivity());
        mAdapter.setCallback(this);
        mRecyclerView.setAdapter(mAdapter);
        movieListType = MovizApp.movieListType;
    }

    @Override
    public void AdapterCallbackRequest(String requestCode) {
        if (requestCode.equals(AppCodes.CODE_FETCH_NEXT_PAGE) && !isFetchOngoing)
            fetchMovies(currentPage + 1, movieListType);
    }

    private void fetchMovies(int nextPage, final String mListType) {
        Log.i(TAG, "Fetch movies, Page: " + nextPage + " ListType: " + mListType);
        isFetchOngoing = true;
        toggleProgressBar(true);

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
                    Log.i(TAG, "Retrofit Response Successful");

                    currentPage = response.body().getPage();
                    if (currentPage == 1) {
                        if (mListType.equals(AppCodes.PREF_MOVIE_LIST_POPULAR)) {
                            getActivity().setTitle("Popular");
                            Utils.saveToSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, AppCodes.PREF_MOVIE_LIST_POPULAR);
                        } else if (mListType.equals(AppCodes.PREF_MOVIE_LIST_TOP_RATED)) {
                            getActivity().setTitle("Top Rated");
                            Utils.saveToSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, AppCodes.PREF_MOVIE_LIST_TOP_RATED);
                        }
                        mAdapter.clearAllAndPopulate(response.body().getResults());
                    } else mAdapter.addItemsAndPopulate(response.body().getResults());
                } else {
                    Toast.makeText(getActivity(), "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Retrofit Response Failure" + response.message());
                }
                fetchEnded();
            }

            @Override
            public void onFailure(Call<TmdbRawMoviesObject> call, Throwable t) {
                Toast.makeText(getActivity(), "Oops, Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Retrofit Failure" + t.getMessage());
                fetchEnded();
            }

            private void fetchEnded() {
                isFetchOngoing = false;
                toggleProgressBar(false);
            }
        };
        call.enqueue(callback);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            toggleSort();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleSort() {
        if (isFetchOngoing)
            Toast.makeText(getActivity(), "Search Ongoing...", Toast.LENGTH_SHORT).show();
        else {
            if (movieListType.equals(AppCodes.PREF_MOVIE_LIST_POPULAR)) {
                movieListType = AppCodes.PREF_MOVIE_LIST_TOP_RATED;
                Log.i(TAG, "Sort by Popularity");
            } else if (movieListType.equals(AppCodes.PREF_MOVIE_LIST_TOP_RATED)) {
                movieListType = AppCodes.PREF_MOVIE_LIST_POPULAR;
                Log.i(TAG, "Sort by Ratings");
            }
            fetchMovies(1, movieListType);
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
    }
}

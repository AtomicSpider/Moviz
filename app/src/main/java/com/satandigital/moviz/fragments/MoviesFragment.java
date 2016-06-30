package com.satandigital.moviz.fragments;

import android.content.Context;
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
import com.satandigital.moviz.activities.MainActivity;
import com.satandigital.moviz.adapters.MoviesRecyclerViewAdapter;
import com.satandigital.moviz.adapters.MoviesRecyclerViewAdapter.AdapterCallback;
import com.satandigital.moviz.callbacks.MovizCallback;
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
public class MoviesFragment extends Fragment implements AdapterCallback, MovizCallback {

    private String TAG = MoviesFragment.class.getSimpleName();

    private MoviesRecyclerViewAdapter mAdapter;
    public static MovizCallback mCallback;

    //Views
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    CircularProgressView mProgressView;

    //Data
    private boolean isFetchOngoing = false;
    private String movieListType;
    private int currentPage = 1;

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
        mCallback = (MovizCallback) getActivity();

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
    }

    private void setRecyclerView() {
        int columnSize = getResources().getInteger(R.integer.grid_size);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnSize));
        mAdapter = new MoviesRecyclerViewAdapter(getActivity());
        mAdapter.setCallback(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void CallbackRequest(String request, String data) {
        if (request.equals(AppCodes.CALLBACK_FETCH_MOVIES_WITH_TYPE)) {
            movieListType = data;
            fetchMovies(1, movieListType);
        }
    }

    @Override
    public void AdapterCallbackRequest(String requestCode) {
        if (requestCode.equals(AppCodes.CODE_FETCH_NEXT_PAGE) && !isFetchOngoing)
            fetchMovies(currentPage + 1, movieListType);
    }

    private void fetchMovies(int nextPage, final String mListType) {
        Log.i(TAG, "Fetch movies, Page: " + nextPage + " ListType: " + mListType);
        isFetchOngoing = true;
        mCallback.CallbackRequest(AppCodes.CALLBACK_TOGGLE_SPINNER, "DISABLE");
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
                        Log.i(TAG, "Saving Pref as: " + movieListType);
                        Utils.saveToSharedPreferences(AppCodes.PREF_MOVIE_LIST_TYPE, movieListType);
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
                mCallback.CallbackRequest(AppCodes.CALLBACK_TOGGLE_SPINNER, "ENABLE");
            }
        };
        call.enqueue(callback);
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

package com.satandigital.moviz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;
import com.satandigital.moviz.models.RecyclerViewAdapter;
import com.satandigital.moviz.models.RecyclerViewAdapter.AdapterCallback;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/26/2016.
 */
public class MoviesFragment extends Fragment implements AdapterCallback {

    private String TAG = MoviesFragment.class.getSimpleName();

    private RecyclerViewAdapter mAdapter;

    //Views
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    CircularProgressView mProgressView;

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

    private void setRecyclerView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new RecyclerViewAdapter(getActivity(), MovizApp.sortType);
        mAdapter.setCallback(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void AdapterCallbackRequest(String requestCode) {
        if (requestCode.equals(AppCodes.CODE_TITLE_POPULAR)) {
            getActivity().setTitle("Popular");
            Utils.saveToSharedPreferences(AppCodes.PREF_SORT_TYPE, AppCodes.PREF_SORT_POPULAR);
        } else if (requestCode.equals(AppCodes.CODE_TITLE_TOP_RATED)) {
            getActivity().setTitle("Top Rated");
            Utils.saveToSharedPreferences(AppCodes.PREF_SORT_TYPE, AppCodes.PREF_SORT_TOP_RATED);
        }
        else if (requestCode.equals(AppCodes.CODE_ENABLE_PROGRESS_BAR))
            mProgressView.setVisibility(View.VISIBLE);
        else if (requestCode.equals(AppCodes.CODE_DISABLE_PROGRESS_BAR))
            mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            mAdapter.toggleSort();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

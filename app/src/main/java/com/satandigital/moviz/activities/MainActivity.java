package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.satandigital.moviz.R;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;
import com.satandigital.moviz.models.RecyclerViewAdapter;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/14/2016.
 */
public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.AdapterCallback {

    private String TAG = MainActivity.class.getSimpleName();

    private RecyclerViewAdapter mAdapter;

    //Views
    private RecyclerView mRecyclerView;
    private CircularProgressView mProgressView;
    private ActionBar mActionBar;

    //Data
    private String sortType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setupActionBar();
        findViews();
        setRecyclerView();
    }

    private void setupActionBar() {
        sortType = Utils.readSharedPreferences(AppCodes.PREF_SORT_TYPE, AppCodes.PREF_SORT_POPULAR);

        mActionBar = getSupportActionBar();
        if (sortType.equals(AppCodes.CODE_TITLE_POPULAR))
            mActionBar.setTitle("Popular");
        else if (sortType.equals(AppCodes.CODE_TITLE_TOP_RATED))
            mActionBar.setTitle("Top Rated");
    }

    private void findViews() {
        mProgressView = (CircularProgressView) findViewById(R.id.progress_bar);
    }

    private void setRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        mAdapter = new RecyclerViewAdapter(MainActivity.this, sortType);
        mAdapter.setCallback(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public void AdapterCallbackRequest(String requestCode) {
        if (requestCode.equals(AppCodes.CODE_ENABLE_PROGRESS_BAR))
            mProgressView.setVisibility(View.VISIBLE);
        else if (requestCode.equals(AppCodes.CODE_DISABLE_PROGRESS_BAR))
            mProgressView.setVisibility(View.GONE);
        else if (requestCode.equals(AppCodes.CODE_TITLE_POPULAR)) {
            mActionBar.setTitle("Popular");
            Utils.saveToSharedPreferences(AppCodes.PREF_SORT_TYPE, AppCodes.PREF_SORT_POPULAR);
        } else if (requestCode.equals(AppCodes.CODE_TITLE_TOP_RATED)) {
            mActionBar.setTitle("Top Rated");
            Utils.saveToSharedPreferences(AppCodes.PREF_SORT_TYPE, AppCodes.PREF_SORT_TOP_RATED);
        }
    }
}

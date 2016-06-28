package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.adapters.ReviewsRecyclerViewAdapter;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.models.MovieObject;
import com.satandigital.moviz.models.TmdbRawReviewObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/28/2016.
 */
public class ReviewsActivity extends AppCompatActivity implements ReviewsRecyclerViewAdapter.AdapterCallback {

    private final String TAG = ReviewsActivity.class.getSimpleName();

    private ReviewsRecyclerViewAdapter mAdapter;

    //Views
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar)
    CircularProgressView mProgressView;

    //Data
    private String original_title;
    private TmdbRawReviewObject mTmdbRawReviewObject;
    private boolean paging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);

        original_title = getIntent().getStringExtra(AppCodes.KEY_MOVIE_NAME);
        getSupportActionBar().setTitle(original_title);
        mTmdbRawReviewObject = getIntent().getParcelableExtra(AppCodes.KEY_TMDB_RAW_REVIEW_OBJECT);
        if (mTmdbRawReviewObject.getTotal_pages() > 1) paging = true;
        setRecyclerView(savedInstanceState);
    }

    private void setRecyclerView(Bundle savedInstanceState) {
        mAdapter = new ReviewsRecyclerViewAdapter(ReviewsActivity.this);
        mAdapter.setCallback(ReviewsActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        if (savedInstanceState == null)
            mAdapter.clearAllAndPopulate(mTmdbRawReviewObject.getResults());
        else {
        }
    }

    @Override
    public void AdapterCallbackRequest(String requestCode) {

    }
}

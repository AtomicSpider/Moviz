package com.satandigital.moviz.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.adapters.ReviewsRecyclerViewAdapter;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.models.MovieObject;
import com.satandigital.moviz.models.ReviewObject;
import com.satandigital.moviz.models.TmdbRawReviewObject;
import com.satandigital.moviz.retrofit.TmdbClient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private int movie_id;
    private ArrayList<ReviewObject> mReviewObjects;
    private boolean paging = false;
    private int currentPage = 1;
    private boolean isFetchOngoing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);

        mReviewObjects = new ArrayList<>();
        setUpRecyclerView();

        if (savedInstanceState == null) {
            original_title = getIntent().getStringExtra(AppCodes.KEY_MOVIE_TITLE);
            movie_id = getIntent().getIntExtra(AppCodes.KEY_MOVIE_ID, 0);
            TmdbRawReviewObject mTmdbRawReviewObject = getIntent().getParcelableExtra(AppCodes.KEY_TMDB_RAW_REVIEW_OBJECT);
            mReviewObjects = mTmdbRawReviewObject.getResults();
            paging = mTmdbRawReviewObject.getTotal_pages() > currentPage;
        } else {
            original_title = savedInstanceState.getString(AppCodes.KEY_MOVIE_TITLE);
            movie_id = savedInstanceState.getInt(AppCodes.KEY_MOVIE_ID);
            mReviewObjects = savedInstanceState.getParcelableArrayList(AppCodes.KEY_REVIEW_OBJECTS);
            paging = savedInstanceState.getBoolean(AppCodes.KEY_REVIEW_PAGING);
            currentPage = savedInstanceState.getInt(AppCodes.KEY_CURRENT_PAGE);
        }

        getSupportActionBar().setTitle(original_title);
        populateRecyclerView();
        if (savedInstanceState != null)
            mRecyclerView.scrollToPosition(savedInstanceState.getInt(AppCodes.KEY_LIST_POSITION));
    }

    private void setUpRecyclerView() {
        mAdapter = new ReviewsRecyclerViewAdapter(ReviewsActivity.this);
        mAdapter.setCallback(ReviewsActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void populateRecyclerView() {
        mAdapter.setPaging(paging);
        mAdapter.clearAllAndPopulate(mReviewObjects);
    }

    @Override
    public void AdapterCallbackRequest(String requestCode) {
        if (requestCode.equals(AppCodes.CODE_FETCH_NEXT_PAGE) && !isFetchOngoing)
            fetchReviews(currentPage + 1, movie_id);
    }

    private void fetchReviews(int nextPage, int id) {
        Log.i(TAG, "Fetch reviews, Page: " + nextPage);
        isFetchOngoing = true;
        toggleProgressBar(true);

        Call<TmdbRawReviewObject> call = TmdbClient.getInstance(ReviewsActivity.this)
                .getMovieReviewsClient()
                .getMovieReviews(id, nextPage);

        Callback<TmdbRawReviewObject> callback = new Callback<TmdbRawReviewObject>() {
            @Override
            public void onResponse(Call<TmdbRawReviewObject> call, Response<TmdbRawReviewObject> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Retrofit Reviews Response Successful");
                    currentPage = response.body().getPage();
                    paging = response.body().getTotal_pages() > currentPage;
                    mReviewObjects.addAll(response.body().getResults());
                    populateRecyclerView();
                } else
                    Log.e(TAG, "Retrofit Reviews Response Failure" + response.message());
                fetchEnded();
            }

            @Override
            public void onFailure(Call<TmdbRawReviewObject> call, Throwable t) {
                Log.e(TAG, "Retrofit Reviews Failure" + t.getMessage());
                fetchEnded();
            }

            private void fetchEnded() {
                isFetchOngoing = false;
                toggleProgressBar(false);
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

        outState.putString(AppCodes.KEY_MOVIE_TITLE, original_title);
        outState.putInt(AppCodes.KEY_MOVIE_ID, movie_id);
        outState.putParcelableArrayList(AppCodes.KEY_REVIEW_OBJECTS, mReviewObjects);
        outState.putBoolean(AppCodes.KEY_REVIEW_PAGING, paging);
        outState.putInt(AppCodes.KEY_CURRENT_PAGE, currentPage);
        outState.putInt(AppCodes.KEY_LIST_POSITION, mAdapter.getListPosition());
    }
}

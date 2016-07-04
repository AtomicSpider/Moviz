package com.satandigital.moviz.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.satandigital.moviz.MovizApp;
import com.satandigital.moviz.R;
import com.satandigital.moviz.activities.MainActivity;
import com.satandigital.moviz.activities.ReviewsActivity;
import com.satandigital.moviz.adapters.VideosRecyclerViewAdapter;
import com.satandigital.moviz.asynctasks.DatabaseTask;
import com.satandigital.moviz.callbacks.MovizCallback;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;
import com.satandigital.moviz.models.ExpandableTextView;
import com.satandigital.moviz.models.MovieObject;
import com.satandigital.moviz.models.ReviewObject;
import com.satandigital.moviz.models.TmdbRawReviewObject;
import com.satandigital.moviz.models.TmdbRawVideosObject;
import com.satandigital.moviz.models.VideoObject;
import com.satandigital.moviz.retrofit.TmdbClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class DetailsFragment extends Fragment implements MovizCallback {

    private final String TAG = DetailsFragment.class.getSimpleName();

    private VideosRecyclerViewAdapter mAdapter;
    private MovizCallback movizCallback = this;
    private MovizCallback mCallback;

    //Views
    @BindView(R.id.backdrop)
    ImageView backdropIV;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.original_title)
    TextView originalTitleTv;
    @BindView(R.id.vote_average)
    TextView voteAverageTv;
    @BindView(R.id.adult)
    TextView adultTv;
    @BindView(R.id.release_date)
    TextView releaseDateTv;
    @BindView(R.id.genre)
    TextView genreTv;
    @BindView(R.id.overview)
    ExpandableTextView overviewTv;
    @BindView(R.id.videosLL)
    LinearLayout videosLL;
    @BindView(R.id.videos_recycler_view)
    RecyclerView videosRv;
    @BindView(R.id.reviewsLL)
    LinearLayout reviewsLL;
    @BindView(R.id.review1LL)
    LinearLayout review1LL;
    @BindView(R.id.review1_author)
    TextView review1_author;
    @BindView(R.id.review1_content)
    ExpandableTextView review1_content;
    @BindView(R.id.review2LL)
    LinearLayout review2LL;
    @BindView(R.id.review2_author)
    TextView review2_author;
    @BindView(R.id.review2_content)
    ExpandableTextView review2_content;
    @BindView(R.id.read_all_reviews)
    TextView read_all_reviews;

    //Data
    @BindString(R.string.TMDB_BASE_BACKDROP_POSTER_PATH)
    String TMDB_BASE_BACKDROP_POSTER_PATH;

    private MovieObject movieObject;
    private ArrayList<VideoObject> mVideoObjects = new ArrayList<>();
    private TmdbRawReviewObject mTmdbRawReviewObject;
    private boolean isFavorite = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        //Max height for landscape mode
        if (getResources().getString(R.string.orientation).equals("landscape"))
            backdropIV.setMaxHeight(getDeviceHeight() / 2);

        setVideosRecyclerView();
        getData(savedInstanceState);
        setUpFab();
        decorateView();

        new DatabaseTask(this, getActivity(), null).execute(AppCodes.TASK_IS_FAVORITE, String.valueOf(movieObject.getId()));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (MainActivity.twoPane) mCallback = (MovizCallback) getActivity();
    }

    private void setVideosRecyclerView() {
        mAdapter = new VideosRecyclerViewAdapter(getActivity());
        videosRv.setAdapter(mAdapter);
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle mBundle = getArguments();
            if (mBundle != null) movieObject = mBundle.getParcelable("data");
            else movieObject = getActivity().getIntent().getParcelableExtra("data");
            getVideos(movieObject.getId());
            getReviews(movieObject.getId());
        } else {
            movieObject = savedInstanceState.getParcelable(AppCodes.KEY_MOVIE_OBJECT);
            mVideoObjects = savedInstanceState.getParcelableArrayList(AppCodes.KEY_VIDEO_OBJECTS);
            mTmdbRawReviewObject = savedInstanceState.getParcelable(AppCodes.KEY_TMDB_RAW_REVIEW_OBJECT);
            populateWithVideos(mVideoObjects);
            populateWithReviews(mTmdbRawReviewObject.getResults());
        }
    }

    private void setUpFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) {
                    new DatabaseTask(movizCallback, getActivity(), null).execute(AppCodes.TASK_DELETE_FAVORITE, String.valueOf(movieObject.getId()));
                } else {
                    new DatabaseTask(movizCallback, getActivity(), movieObject).execute(AppCodes.TASK_ADD_FAVORITE, null);
                }
            }
        });
    }

    private void decorateView() {
        Picasso.with(getActivity()).load(TMDB_BASE_BACKDROP_POSTER_PATH + movieObject.getBackdrop_path()).placeholder(R.drawable.placeholder_backdrop_poster).error(R.drawable.placeholder_backdrop_error_poster).into(backdropIV);
        originalTitleTv.setText(movieObject.getOriginal_title());
        voteAverageTv.setText(String.valueOf(movieObject.getVote_average()));
        if (movieObject.isAdult()) adultTv.setVisibility(View.VISIBLE);
        releaseDateTv.setText(movieObject.getRelease_date());
        genreTv.setText(Utils.getGenre(movieObject.getGenre_ids()));
        overviewTv.setText(movieObject.getOverview());
    }

    private void getVideos(int id) {
        Call<TmdbRawVideosObject> call = TmdbClient.getInstance(getActivity())
                .getMovieVideosClient()
                .getMovieVideos(id);

        Callback<TmdbRawVideosObject> callback = new Callback<TmdbRawVideosObject>() {
            @Override
            public void onResponse(Call<TmdbRawVideosObject> call, Response<TmdbRawVideosObject> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Retrofit Videos Response Successful");
                    mVideoObjects = response.body().getResults();
                    populateWithVideos(mVideoObjects);
                } else
                    Log.e(TAG, "Retrofit Videos Response Failure" + response.message());
            }

            @Override
            public void onFailure(Call<TmdbRawVideosObject> call, Throwable t) {
                Log.e(TAG, "Retrofit Videos Failure" + t.getMessage());
            }
        };
        call.enqueue(callback);
    }

    private void populateWithVideos(ArrayList<VideoObject> mVideoObjects) {
        if (mVideoObjects.size() > 0) {
            videosLL.setVisibility(View.VISIBLE);
            mAdapter.clearAllAndPopulate(mVideoObjects);
        }
    }

    private void getReviews(int id) {
        Call<TmdbRawReviewObject> call = TmdbClient.getInstance(getActivity())
                .getMovieReviewsClient()
                .getMovieReviews(id, 1);

        Callback<TmdbRawReviewObject> callback = new Callback<TmdbRawReviewObject>() {
            @Override
            public void onResponse(Call<TmdbRawReviewObject> call, Response<TmdbRawReviewObject> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Retrofit Reviews Response Successful");
                    mTmdbRawReviewObject = response.body();
                    populateWithReviews(mTmdbRawReviewObject.getResults());
                } else
                    Log.e(TAG, "Retrofit Reviews Response Failure" + response.message());
            }

            @Override
            public void onFailure(Call<TmdbRawReviewObject> call, Throwable t) {
                Log.e(TAG, "Retrofit Reviews Failure" + t.getMessage());
            }
        };
        call.enqueue(callback);
    }

    private void populateWithReviews(ArrayList<ReviewObject> mReviewObjects) {
        if (mReviewObjects.size() > 0) {
            reviewsLL.setVisibility(View.VISIBLE);
            review1LL.setVisibility(View.VISIBLE);
            review1_author.setText(mReviewObjects.get(0).getAuthor());
            review1_content.setText(mReviewObjects.get(0).getContent());
        }
        if (mReviewObjects.size() > 1) {
            review2LL.setVisibility(View.VISIBLE);
            review2_author.setText(mReviewObjects.get(1).getAuthor());
            review2_content.setText(mReviewObjects.get(1).getContent());
        }
        if (mReviewObjects.size() > 2) {
            read_all_reviews.setVisibility(View.VISIBLE);
            read_all_reviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mIntent = new Intent(getActivity(), ReviewsActivity.class);
                    mIntent.putExtra(AppCodes.KEY_MOVIE_TITLE, movieObject.getOriginal_title());
                    mIntent.putExtra(AppCodes.KEY_MOVIE_ID, movieObject.getId());
                    mIntent.putExtra(AppCodes.KEY_TMDB_RAW_REVIEW_OBJECT, mTmdbRawReviewObject);
                    startActivity(mIntent);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppCodes.KEY_MOVIE_OBJECT, movieObject);
        outState.putParcelableArrayList(AppCodes.KEY_VIDEO_OBJECTS, mVideoObjects);
        outState.putParcelable(AppCodes.KEY_TMDB_RAW_REVIEW_OBJECT, mTmdbRawReviewObject);
    }

    private int getDeviceHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    @Override
    public void CallbackRequest(String request, String data) {
        switch (request) {
            case AppCodes.CALLBACK_IS_FAVORITE:
                if (data.equals("true")) {
                    Log.d(TAG, "Movie is in Favorites");
                    isFavorite = true;
                    fab.setImageResource(R.drawable.ic_heart_24_4);
                } else if (data.equals("false")) {
                    isFavorite = false;
                    Log.d(TAG, "Movie is not in Favorites");
                }
                break;
            case AppCodes.CALLBACK_DELETE_FAVORITE:
                if (data.equals("true")) {
                    Log.d(TAG, "Movie deleted from Favorites");
                    isFavorite = false;
                    fab.setImageResource(R.drawable.ic_heart_disabled_24_4);
                    if (MovizApp.movieListType.equals(AppCodes.PREF_MOVIE_LIST_FAVORITES))
                        mCallback.CallbackRequest(AppCodes.CALLBACK_REFRESH_FAVORITES, "");
                    Toast.makeText(getActivity(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else if (data.equals("false")) {
                    Log.d(TAG, "Movie could not be deleted from Favorites");
                    isFavorite = true;
                    fab.setImageResource(R.drawable.ic_heart_24_4);
                }
                break;
            case AppCodes.CALLBACK_ADD_FAVORITE:
                if (data.equals("true")) {
                    Log.d(TAG, "Movie added to Favorites");
                    isFavorite = true;
                    fab.setImageResource(R.drawable.ic_heart_24_4);
                    if (MovizApp.movieListType.equals(AppCodes.PREF_MOVIE_LIST_FAVORITES))
                        mCallback.CallbackRequest(AppCodes.CALLBACK_REFRESH_FAVORITES, "");
                    Toast.makeText(getActivity(), "Added to favorites", Toast.LENGTH_SHORT).show();
                } else if (data.equals("false")) {
                    Log.d(TAG, "Movie could not be added to Favorites");
                    isFavorite = false;
                    fab.setImageResource(R.drawable.ic_heart_disabled_24_4);
                }
                break;
        }
    }

    @Override
    public void CallbackRequest(String request, ArrayList<MovieObject> movieObjects) {

    }
}

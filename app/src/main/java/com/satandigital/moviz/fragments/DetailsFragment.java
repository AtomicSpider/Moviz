package com.satandigital.moviz.fragments;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.satandigital.moviz.R;
import com.satandigital.moviz.adapters.VideosRecyclerViewAdapter;
import com.satandigital.moviz.common.AppCodes;
import com.satandigital.moviz.common.Utils;
import com.satandigital.moviz.models.MovieObject;
import com.satandigital.moviz.models.TmdbRawVideosObject;
import com.satandigital.moviz.models.VideoObject;
import com.satandigital.moviz.retrofit.TmdbClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */

//ToDo Retain instance
public class DetailsFragment extends Fragment {

    private final String TAG = DetailsFragment.class.getSimpleName();

    private VideosRecyclerViewAdapter mAdapter;

    //Views
    @BindView(R.id.backdrop)
    ImageView backdropIV;
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
    @BindView(R.id.videos_recycler_view)
    RecyclerView videosRv;

    //Data
    MovieObject movieObject;
    ArrayList<VideoObject> mVideoObjects;
    private String TMDB_BASE_BACKDROP_POSTER_PATH;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);

        //Max height for tablet mode
        if (getResources().getString(R.string.orientation).equals("landscape"))
        backdropIV.setMaxHeight(getDeviceHeight()/2);

        mVideoObjects = new ArrayList<>();
        TMDB_BASE_BACKDROP_POSTER_PATH = getActivity().getResources().getString(R.string.TMDB_BASE_BACKDROP_POSTER_PATH);

        setVideosRecyclerView();

        if (savedInstanceState == null) {
            movieObject = getActivity().getIntent().getParcelableExtra("data");
            getVideos(movieObject.getId());
        }
        else {
            movieObject = savedInstanceState.getParcelable(AppCodes.KEY_MOVIE_OBJECT);
            mVideoObjects = savedInstanceState.getParcelableArrayList(AppCodes.KEY_VIDEO_OBJECTS);
            mAdapter.clearAllAndPopulate(mVideoObjects);
        }
        decorateView();

        return rootView;
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

    private void setVideosRecyclerView() {
        mAdapter = new VideosRecyclerViewAdapter(getActivity());
        videosRv.setAdapter(mAdapter);
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
                    mAdapter.clearAllAndPopulate(mVideoObjects);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(AppCodes.KEY_MOVIE_OBJECT, movieObject);
        outState.putParcelableArrayList(AppCodes.KEY_VIDEO_OBJECTS, mVideoObjects);
    }

    private int getDeviceHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }
}

package com.satandigital.moviz.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.satandigital.moviz.R;
import com.satandigital.moviz.models.MovieObject;
import com.squareup.picasso.Picasso;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class DetailsFragment extends android.support.v4.app.Fragment {

    private String TAG = DetailsFragment.class.getSimpleName();

    //Views
    ImageView posterIV;
    TextView titleTv, releaseDateTv, ratingTv, genreTv, overviewTv;

    //Data
    MovieObject movieObject;
    private String TMDB_BASE_POSTER_PATH;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        movieObject = getActivity().getIntent().getParcelableExtra("data");
        TMDB_BASE_POSTER_PATH = getActivity().getResources().getString(R.string.TMDB_BASE_POSTER_PATH);
        findViews(rootView);
        setData();

        return rootView;
    }

    private void findViews(View rootView) {
        posterIV = (ImageView) rootView.findViewById(R.id.poster);
        titleTv = (TextView) rootView.findViewById(R.id.original_title);
        releaseDateTv = (TextView) rootView.findViewById(R.id.release_date);
        ratingTv = (TextView) rootView.findViewById(R.id.vote_average);
        genreTv = (TextView) rootView.findViewById(R.id.genre);
        overviewTv = (TextView) rootView.findViewById(R.id.overview);
    }

    private void setData() {
        Picasso.with(getActivity()).load(TMDB_BASE_POSTER_PATH + movieObject.getPoster_path()).placeholder(R.drawable.placeholder_poster).into(posterIV);
        titleTv.setText(movieObject.getOriginal_title());
        releaseDateTv.setText("(" + movieObject.getRelease_date() + ")");
        ratingTv.setText(String.valueOf(movieObject.getVote_average()));
        genreTv.setText(getGenre(movieObject.getGenre_ids()));
        overviewTv.setText(movieObject.getOverview());
    }

    private String getGenre(int[] genre_ids) {
        String genre = "";
        for (int i = 0; i < genre_ids.length; i++) {
            if (genre_ids[i] == 28)
                genre += "Action";
            if (genre_ids[i] == 12)
                genre += "Adventure";
            if (genre_ids[i] == 16)
                genre += "Animation";
            if (genre_ids[i] == 35)
                genre += "Comedy";
            if (genre_ids[i] == 80)
                genre += "Crime";
            if (genre_ids[i] == 99)
                genre += "Documentary";
            if (genre_ids[i] == 18)
                genre += "Drama";
            if (genre_ids[i] == 10751)
                genre += "Family";
            if (genre_ids[i] == 14)
                genre += "Fantasy";
            if (genre_ids[i] == 10769)
                genre += "Foreign";
            if (genre_ids[i] == 36)
                genre += "History";
            if (genre_ids[i] == 27)
                genre += "Horror";
            if (genre_ids[i] == 10402)
                genre += "Music";
            if (genre_ids[i] == 9648)
                genre += "Mystery";
            if (genre_ids[i] == 10749)
                genre += "Romance";
            if (genre_ids[i] == 878)
                genre += "SciFi";
            if (genre_ids[i] == 10770)
                genre += "TV Movie";
            if (genre_ids[i] == 53)
                genre += "Thriller";
            if (genre_ids[i] == 10752)
                genre += "War";
            if (genre_ids[i] == 37)
                genre += "Western";

            if (i != genre_ids.length - 1) genre += " | ";
        }

        return genre;
    }
}

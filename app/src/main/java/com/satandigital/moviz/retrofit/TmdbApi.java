package com.satandigital.moviz.retrofit;

import com.satandigital.moviz.models.TmdbRawMoviesObject;
import com.satandigital.moviz.models.TmdbRawVideosObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/27/2016.
 */
public class TmdbApi {
    public interface PopularMovies {
        @GET("movie/popular")
        Call<TmdbRawMoviesObject> getPopularMovies(@Query("page") Integer page);
    }

    public interface TopRatedMovies {
        @GET("movie/top_rated")
        Call<TmdbRawMoviesObject> getTopRatedMovies(@Query("page") Integer page);
    }

    public interface MovieVideos {
        @GET("movie/{id}/videos")
        Call<TmdbRawVideosObject> getMovieVideos(@Path("id") Integer id);
    }
}
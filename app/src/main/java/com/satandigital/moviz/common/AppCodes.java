package com.satandigital.moviz.common;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class AppCodes {

    // Prefs
    public static final String prefName = "MOVIZ_PREF";

    public static final String PREF_MOVIE_LIST_TYPE = "PREF_MOVIE_LIST_TYPE";
    public static final String PREF_MOVIE_LIST_POPULAR = "popular";
    public static final String PREF_MOVIE_LIST_TOP_RATED = "top_rated";
    public static final String PREF_MOVIE_LIST_FAVORITES = "favorites";

    //Callbacks
    public static final String CALLBACK_FETCH_MOVIES_WITH_PAGE = "CALLBACK_FETCH_MOVIES_WITH_PAGE";

    public static final String CALLBACK_IS_FAVORITE = "CALLBACK_IS_FAVORITE";
    public static final String CALLBACK_DELETE_FAVORITE = "CALLBACK_DELETE_FAVORITE";
    public static final String CALLBACK_ADD_FAVORITE = "CALLBACK_ADD_FAVORITE";
    public static final String CALLBACK_QUERY_FAVORITE_LIST = "CALLBACK_QUERY_FAVORITE_LIST";

    public static final String CALLBACK_MOVIE_BUNDLE = "CALLBACK_MOVIE_BUNDLE";
    public static final String CALLBACK_REFRESH_FAVORITES = "CALLBACK_REFRESH_FAVORITES";
    public static final String CALLBACK_VIEW_SEARCH_RESULTS = "CALLBACK_VIEW_SEARCH_RESULTS";

    //MoviesListFragment
    public static final String KEY_MOVIE_OBJECTS = "KEY_MOVIE_OBJECTS";
    public static final String KEY_MOVIE_LIST_TYPE = "KEY_MOVIE_LIST_TYPE";
    public static final String KEY_CURRENT_PAGE = "KEY_CURRENT_PAGE";
    public static final String KEY_LIST_POSITION = "KEY_LIST_POSITION";
    public static final String KEY_FIRST_DISPLAY = "KEY_FIRST_DISPLAY";

    //DetailsFragment
    public static final String KEY_MOVIE_OBJECT = "KEY_MOVIE_OBJECT";
    public static final String KEY_VIDEO_OBJECTS = "KEY_VIDEO_OBJECTS";
    public static final String KEY_MOVIE_TITLE = "KEY_MOVIE_TITLE";
    public static final String KEY_MOVIE_ID = "KEY_MOVIE_ID";
    public static final String KEY_TMDB_RAW_REVIEW_OBJECT = "KEY_TMDB_RAW_REVIEW_OBJECT";
    public static final String KEY_REVIEW_OBJECTS = "KEY_REVIEW_OBJECTS";
    public static final String KEY_REVIEW_PAGING = "KEY_REVIEW_PAGING";

    //Database Related
    public static final String TASK_IS_FAVORITE = "TASK_IS_FAVORITE";
    public static final String TASK_DELETE_FAVORITE = "TASK_DELETE_FAVORITE";
    public static final String TASK_ADD_FAVORITE = "TASK_ADD_FAVORITE";
    public static final String TASK_QUERY_FAVORITE_LIST = "TASK_QUERY_FAVORITE_LIST";
}

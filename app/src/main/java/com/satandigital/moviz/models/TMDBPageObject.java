package com.satandigital.moviz.models;

import java.util.ArrayList;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/15/2016.
 */
public class TMDBPageObject {

    private int page;
    private ArrayList<MovieObject> movieObjects;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<MovieObject> getMovieObjects() {
        return movieObjects;
    }

    public void setMovieObjects(ArrayList<MovieObject> movieObjects) {
        this.movieObjects = movieObjects;
    }
}

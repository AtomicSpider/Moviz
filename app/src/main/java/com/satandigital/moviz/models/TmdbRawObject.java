package com.satandigital.moviz.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/27/2016.
 */
public class TmdbRawObject {

    @Expose
    @SerializedName("page")
    private int page;

    @Expose
    @SerializedName("results")
    private ArrayList<MovieObject> movies;

    @Expose
    @SerializedName("total_pages")
    private int totalPages;

    @Expose
    @SerializedName("total_results")
    private int totalResults;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<MovieObject> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<MovieObject> movies) {
        this.movies = movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
package com.satandigital.moviz.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/27/2016.
 */
public class TmdbRawVideosObject {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("results")
    private ArrayList<VideoObject> results = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<VideoObject> getResults() {
        return results;
    }

    public void setResults(ArrayList<VideoObject> results) {
        this.results = results;
    }
}

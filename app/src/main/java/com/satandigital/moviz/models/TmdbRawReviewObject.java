package com.satandigital.moviz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/27/2016.
 */
public class TmdbRawReviewObject implements Parcelable {

    @Expose
    @SerializedName("page")
    private int page;

    @Expose
    @SerializedName("results")
    private ArrayList<ReviewObject> results = new ArrayList<>();

    @Expose
    @SerializedName("total_pages")
    private int total_pages;

    @Expose
    @SerializedName("total_results")
    private int total_results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<ReviewObject> getResults() {
        return results;
    }

    public void setResults(ArrayList<ReviewObject> results) {
        this.results = results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.page);
        dest.writeTypedList(this.results);
        dest.writeInt(this.total_pages);
        dest.writeInt(this.total_results);
    }

    public TmdbRawReviewObject() {
    }

    protected TmdbRawReviewObject(Parcel in) {
        this.page = in.readInt();
        this.results = in.createTypedArrayList(ReviewObject.CREATOR);
        this.total_pages = in.readInt();
        this.total_results = in.readInt();
    }

    public static final Parcelable.Creator<TmdbRawReviewObject> CREATOR = new Parcelable.Creator<TmdbRawReviewObject>() {
        @Override
        public TmdbRawReviewObject createFromParcel(Parcel source) {
            return new TmdbRawReviewObject(source);
        }

        @Override
        public TmdbRawReviewObject[] newArray(int size) {
            return new TmdbRawReviewObject[size];
        }
    };
}

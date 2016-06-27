package com.satandigital.moviz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/27/2016.
 */
public class VideoObject implements Parcelable {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("key")
    private String key;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("site")
    private String site;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
    }

    public VideoObject() {
    }

    protected VideoObject(Parcel in) {
        this.id = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
    }

    public static final Parcelable.Creator<VideoObject> CREATOR = new Parcelable.Creator<VideoObject>() {
        @Override
        public VideoObject createFromParcel(Parcel source) {
            return new VideoObject(source);
        }

        @Override
        public VideoObject[] newArray(int size) {
            return new VideoObject[size];
        }
    };
}

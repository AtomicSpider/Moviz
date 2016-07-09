package com.satandigital.moviz.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.satandigital.moviz.R;
import com.satandigital.moviz.models.objects.VideoObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/27/2016.
 */
public class VideosRecyclerViewAdapter extends RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder> {

    private final String TAG = MoviesRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    //Data
    private ArrayList<VideoObject> mVideoObjects;
    private String YOUTUBE_THUMBNAIL_URL;
    private String YOUTUBE_VIDEO_URL;

    public VideosRecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mVideoObjects = new ArrayList<>();

        YOUTUBE_THUMBNAIL_URL = mContext.getResources().getString(R.string.YOUTUBE_THUMBNAIL_URL);
        YOUTUBE_VIDEO_URL = mContext.getResources().getString(R.string.YOUTUBE_VIDEO_URL);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final boolean isYoutube = mVideoObjects.get(position).getSite().equals("YouTube");
        if (isYoutube)
            Picasso.with(mContext).load(String.format(YOUTUBE_THUMBNAIL_URL, mVideoObjects.get(position).getKey())).placeholder(R.drawable.placeholder_youtube_thumbnail).error(R.drawable.placeholder_youtube_thumbnail).into(holder.video_image_view);

        holder.video_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isYoutube) {
                    String video_url = String.format(YOUTUBE_VIDEO_URL, mVideoObjects.get(position).getKey());
                    Log.i(TAG, "Launching video " + video_url);
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video_url)));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_image_view)
        ImageView video_image_view;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void clearAllAndPopulate(ArrayList<VideoObject> videos) {
        mVideoObjects.clear();
        mVideoObjects.addAll(videos);
        notifyDataSetChanged();
    }
}

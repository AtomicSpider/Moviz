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
import android.widget.TextView;

import com.satandigital.moviz.R;
import com.satandigital.moviz.models.ExpandableTextView;
import com.satandigital.moviz.models.ReviewObject;
import com.satandigital.moviz.models.VideoObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 6/27/2016.
 */
public class ReviewsRecyclerViewAdapter extends RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder> {

    private final String TAG = ReviewsRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;
    private AdapterCallback mCallback;
    private LayoutInflater mLayoutInflater;

    //Data
    private ArrayList<ReviewObject> mReviewObjects;
    private int listPosition = 0;

    public ReviewsRecyclerViewAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mReviewObjects = new ArrayList<>();
    }

    public interface AdapterCallback {
        void AdapterCallbackRequest(String requestCode);
    }

    public void setCallback(AdapterCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        listPosition = position;
        holder.review_author.setText(mReviewObjects.get(position).getAuthor());
        holder.review_content.setText(mReviewObjects.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mReviewObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_author)
        TextView review_author;
        @BindView(R.id.review_content)
        ExpandableTextView review_content;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void clearAllAndPopulate(ArrayList<ReviewObject> reviews) {
        mReviewObjects.clear();
        mReviewObjects.addAll(reviews);
        notifyDataSetChanged();
    }

    public void addItemsAndPopulate(ArrayList<ReviewObject> reviews) {
        mReviewObjects.addAll(reviews);
        notifyDataSetChanged();
    }

    public ArrayList<ReviewObject> getReviewObjects() {
        return mReviewObjects;
    }

    public int getListPosition() {
        return listPosition;
    }
}

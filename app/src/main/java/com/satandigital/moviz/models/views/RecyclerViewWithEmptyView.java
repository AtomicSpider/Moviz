package com.satandigital.moviz.models.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.satandigital.moviz.R;
import com.satandigital.moviz.common.AppCodes;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Project : Moviz
 * Created by Sanat Dutta on 7/6/2016.
 * http://www.satandigital.com/
 */
public class RecyclerViewWithEmptyView extends RecyclerView {

    private TextView emptyView;
    private String State = AppCodes.STATE_NO_NETWORK;

    //Strings
    private String no_network_text;
    private String no_fav_text;

    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    if (AppCodes.STATE_NO_NETWORK.equals(State)) emptyView.setText(no_network_text);
                    else if (AppCodes.STATE_NO_FAV.equals(State)) emptyView.setText(no_fav_text);
                    emptyView.setVisibility(View.VISIBLE);
                    RecyclerViewWithEmptyView.this.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    RecyclerViewWithEmptyView.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    public RecyclerViewWithEmptyView(Context context) {
        super(context);
    }

    public RecyclerViewWithEmptyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewWithEmptyView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        //emptyObserver.onChanged();
    }

    public void setUpEmptyView(Context context, TextView emptyView) {
        no_network_text = context.getResources().getString(R.string.no_network_text);
        no_fav_text = context.getResources().getString(R.string.no_fav_text);
        this.emptyView = emptyView;
    }

    public void setState(String State) {
        this.State = State;
    }
}

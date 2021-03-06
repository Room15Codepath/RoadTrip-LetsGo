package com.codepath.roadtrip_letsgo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.models.TripLocation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yingbwan on 10/22/2017.
 */

public class TripRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TripLocation> items;
    private final int STOP = 0, BUTTON = 1;

    Context mContext;
    private AdapterCallback mAdapterCallback;

    public TripRecyclerAdapter(Context context, List<TripLocation> items) {
        this.mContext = context;
        this.items = items;

        try {
            this.mAdapterCallback = ((AdapterCallback) context);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 1) {
            return STOP;
        } else {
            return BUTTON;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case STOP:
                View v1 = inflater.inflate(R.layout.item_stop_result, parent, false);
                viewHolder = new ViewHolder1(v1);
                break;
            case BUTTON:
                View v2 = inflater.inflate(R.layout.item_button_result, parent, false);
                viewHolder = new ViewHolder2(v2);
                break;
            default:
                viewHolder = null;
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case STOP:
                ViewHolder1 vh1 = (ViewHolder1) holder;
                configureViewHolder1(vh1, position);
                vh1.ivRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            mAdapterCallback.onAdapterCallback(position);
                        } catch (ClassCastException exception) {
                            // do something
                        }
                    }
                });

                break;
            case BUTTON:
                ViewHolder2 vh2 = (ViewHolder2) holder;
                configureViewHolder2(vh2, position);
                break;
            default:
                break;
        }
    }

    public void configureViewHolder1(ViewHolder1 holder, int position) {
        TripLocation stop = items.get(position);

        // set items
        TextView tvName = holder.tvName;
        tvName.setText(stop.loc_name);
        TextView tvAddr = holder.tvAddr;
        tvAddr.setText(stop.address);

    }

    public void configureViewHolder2(ViewHolder2 holder, int position) {
        //Doc article = items.get(position);
        Log.d("STOP", "button line: " + position);
        // set items
    }

    public static class ViewHolder1 extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        public TextView tvName;
        @BindView(R.id.tvAddr)
        public TextView tvAddr;
        @BindView(R.id.ivRemove)
        public ImageView ivRemove;

        public ViewHolder1(View view) {
            // Very important to call the parent constructor
            // as this ensures that the imageView field is populated.
            super(view);
            ButterKnife.bind(this, view);
        }

        public TextView getTvName() {
            return tvName;
        }

        public void setTvName(TextView tvName) {
            this.tvName = tvName;
        }

        public TextView getTvAddr() {
            return tvAddr;
        }

        public void setTvAddr(TextView tvAddr) {
            this.tvAddr = tvAddr;
        }
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {
        @BindView(R.id.tvAdd)
        public TextView tvAdd;

        public ViewHolder2(View view) {
            // Very important to call the parent constructor
            // as this ensures that the imageView field is populated.
            super(view);
            ButterKnife.bind(this, view);
        }

        public TextView getTvAdd() {
            return tvAdd;
        }

        public void setTvAdd(TextView tvAdd) {
            this.tvAdd = tvAdd;
        }
    }

    public static interface AdapterCallback {
        void onAdapterCallback(int position);
    }

}

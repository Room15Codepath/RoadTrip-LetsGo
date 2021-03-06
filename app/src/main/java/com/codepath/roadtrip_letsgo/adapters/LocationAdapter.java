package com.codepath.roadtrip_letsgo.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.helper.GlideApp;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.models.TripStop;
import com.codepath.roadtrip_letsgo.utils.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by yingbwan on 10/14/2017.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    List<TripStop> mLocations;
    Context context;
    TripLocation origin;

    public LocationAdapter(List<TripStop> locations) {
        mLocations = locations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        origin = Util.getOrigin(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TripStop location = mLocations.get(position);
        Log.d("DEBUG", "adapter bind holder location id :" + location.trip_location.loc_name);

        holder.tvName.setText(location.trip_location.loc_name);
        holder.tvAddress.setText(location.trip_location.address);
        float distance = Util.getDistance(origin.lat, origin.lng, location.trip_location.lat,
                location.trip_location.lng);
        Log.d("distance between", String.valueOf(distance));
        String miles = String.format("%.1f mi", distance * 0.0006213719);
        holder.tvMiles.setText(miles);
        holder.tvPrice.setText(location.price);
        holder.tvReviewCount.setText(location.review_count + " Reviews");
        holder.rbRating.setRating((int) location.rating);

        GlideApp.with(holder.ivStopType.getContext()).load(location.image_url)
                .apply(bitmapTransform(new RoundedCornersTransformation(25, 0,
                        RoundedCornersTransformation.CornerType.ALL)))
                .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_home))
                .into(holder.ivStopType);

    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    public void addItems(List<TripStop> list) {
        mLocations.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvAddress)
        TextView tvAddress;
        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvMiles)
        TextView tvMiles;

        @BindView(R.id.iv_stop_type)
        ImageView ivStopType;

        @BindView(R.id.rbRating)
        RatingBar rbRating;

        @BindView(R.id.tvReviewCount)
        TextView tvReviewCount;

        @BindView(R.id.tvPrice)
        TextView tvPrice;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

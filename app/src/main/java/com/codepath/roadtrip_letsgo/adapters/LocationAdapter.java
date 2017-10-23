package com.codepath.roadtrip_letsgo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.models.TripStop;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yingbwan on 10/14/2017.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    List<TripStop> mLocations;
    Context context;

    public LocationAdapter(List<TripStop> locations) {
        mLocations = locations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
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
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    public void addItems(List<TripStop> list){
        mLocations.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvAddress)
        TextView tvAddress;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.ibAdd)
        ImageButton ibAdd;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent();
  //                  intent.putExtra("stop", );
    //                ((SearchActivity)view.getContext()).setResult(RESULT_OK, intent);
      //              ((SearchActivity)view.getContext()).finish();
                    Log.d("DEBUG:", "add route button clicked");
                }
            });
        }
    }

}

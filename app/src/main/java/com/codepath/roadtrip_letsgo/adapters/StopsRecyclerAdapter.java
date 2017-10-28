package com.codepath.roadtrip_letsgo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.helper.ItemTouchHelperAdapter;
import com.codepath.roadtrip_letsgo.helper.ItemTouchHelperViewHolder;
import com.codepath.roadtrip_letsgo.helper.OnStartDragListener;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.utils.Util;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by luba on 10/27/17.
 */

public class StopsRecyclerAdapter extends RecyclerView.Adapter<StopsRecyclerAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private ArrayList<TripLocation> mItems = new ArrayList<>();

    private final OnStartDragListener mDragStartListener;
    Context mContext;


    public StopsRecyclerAdapter(Context context, OnStartDragListener dragStartListener) {
        mContext =context;
        mDragStartListener = dragStartListener;
        //mItems.addAll(Arrays.asList(context.getResources().getStringArray(R.array.dummy_items)));
        mItems.addAll(Util.getStops(context));
        notifyDataSetChanged();

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stop, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.stopName.setText(mItems.get(position).getLoc_name());
        holder.stopAddress.setText(mItems.get(position).getAddress());

        // Start a drag whenever the handle view it touched
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    public void customNotifyDataSetChanged(ArrayList<TripLocation> items) {
        mItems = new ArrayList<>(items);
        notifyDataSetChanged();
    }


    @Override
    public void onItemDismiss(int position) {
        Log.d ("DEBUG", "onItemDismiss"+mItems.get(position).getLoc_name());
        Util.deleteStop(mContext, mItems.get(position));
        mItems.remove(position);
        notifyItemRemoved(position);

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        Util.saveStops(mContext, mItems);
        return true;
    }



    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView stopName;
        public final TextView stopAddress;

        public ItemViewHolder(View itemView) {
            super(itemView);
            stopName = (TextView) itemView.findViewById(R.id.tvName);
            stopAddress = (TextView) itemView.findViewById(R.id.tvAddress);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}

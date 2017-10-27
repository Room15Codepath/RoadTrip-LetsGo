package com.codepath.roadtrip_letsgo.helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by luba on 10/27/17.
 */

public interface OnStartDragListener {

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    void onStartDrag(RecyclerView.ViewHolder viewHolder);

}

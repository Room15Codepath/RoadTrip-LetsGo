package com.codepath.roadtrip_letsgo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.activities.PlaceDetailActivity;
import com.codepath.roadtrip_letsgo.adapters.LocationAdapter;
import com.codepath.roadtrip_letsgo.helper.ItemClickSupport;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.models.TripStop;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;

/**
 * A fragment representing a list of Items.
 */
public class ListViewFragment extends Fragment { // need to add view holder

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    //  private OnListFragmentInteractionListener mListener;
    ArrayList<TripStop> locations;
    LocationAdapter adapter;
    TripLocation origin;
    TripLocation dest;
    int pos = -1;

    Unbinder unbinder;

    //@BindView(R.id.rvLocations)
    RecyclerView rvLocations;
    public LottieAnimationView animationView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListViewFragment() {
    }

    public void addTrip(TripLocation start, TripLocation end) {
        origin = start;
        dest = end;
    }

    public void setPosition(int position) {
        pos = position;
    }

    //
    public static ListViewFragment newInstance(int inp) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
//        args.putParcelable("start", Parcels.wrap(start));
        //      args.putParcelable("end", Parcels.wrap(end));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //      if (getArguments() != null) {
        //         origin = Parcels.unwrap(getArguments().getParcelable("start"));
        //          dest = Parcels.unwrap(getArguments().getParcelable("end"));
        //      }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        // unbinder = ButterKnife.bind(getContext(), view);
        rvLocations = (RecyclerView) view.findViewById(R.id.rvLocations);
        animationView = (LottieAnimationView) view.findViewById(R.id.animation_view);
        locations = new ArrayList<>();
        adapter = new LocationAdapter(locations);
        rvLocations.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvLocations.setLayoutManager(linearLayoutManager);
        animationView.addColorFilterToLayer("Pin 1",
                new PorterDuffColorFilter(this.getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.SRC_ATOP));
        animationView.addColorFilterToLayer("C3",
                new PorterDuffColorFilter(this.getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.SRC_ATOP));

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(rvLocations.getContext(), DividerItemDecoration.VERTICAL);
        rvLocations.addItemDecoration(itemDecoration);


        //animationView.setImageDrawable(this.itemView.getResources().getDrawable(R.drawable
        // .ic_add_location));

        ItemClickSupport.addTo(rvLocations).setOnItemClickListener(
                (recyclerView, position, vw) -> {
                    //create intent
                    Intent intent = new Intent(getContext(), PlaceDetailActivity.class);
                    TripStop loc = locations.get(position);
                    intent.putExtra("start", Parcels.wrap(origin));
                    intent.putExtra("end", Parcels.wrap(dest));

                    intent.putExtra("location", Parcels.wrap(loc));
                    intent.putExtra("position", pos);
                    //launch activity
                    startActivity(intent);
                }
        );
        //mListener.onComplete();

        return view;
    }

    public void cleanList() {
        locations.clear();
        adapter.notifyDataSetChanged();
    }

    public void addItems(List<TripStop> bList) {
        //locations.clear();
        animationView.setVisibility(View.VISIBLE);
        animationView.addColorFilterToLayer("Pin 1",
                new PorterDuffColorFilter(this.getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.SRC_ATOP));
        animationView.addColorFilterToLayer("C3",
                new PorterDuffColorFilter(this.getResources().getColor(R.color.colorAccent),
                        PorterDuff.Mode.SRC_ATOP));
        animationView.playAnimation();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Log.i("tag", "This'll run 1500 milliseconds later");
                        locations.addAll(bList);
                        adapter.notifyItemInserted(locations.size() - 1);
                        animationView.cancelAnimation();
                        animationView.setVisibility(View.GONE);
                    }
                },
                1500);

        Log.d("DEBUG", "after insertion, total:" + locations.size());
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do
    // this automatically.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //  unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity) {

            activity = (Activity) context;

            /*try {
                this.mListener = (OnCompleteListener)activity;
            } catch (final ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
            }*/
        }
    }

    public static interface OnCompleteListener {
        public abstract void onComplete();
    }

    private OnCompleteListener mListener;

}

package com.codepath.roadtrip_letsgo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.activities.SearchActivity;
import com.codepath.roadtrip_letsgo.adapters.LocationAdapter;
import com.codepath.roadtrip_letsgo.fragments.dummy.DummyContent.DummyItem;
import com.codepath.roadtrip_letsgo.helper.ItemClickSupport;
import com.codepath.roadtrip_letsgo.models.TripLocation;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Unbinder;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ListViewFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    ArrayList<TripLocation> locations;
    LocationAdapter adapter;
    Unbinder unbinder;

//@BindView(R.id.rvLocations)
RecyclerView rvLocations;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListViewFragment() {
    }

    //
    public static ListViewFragment newInstance(int columnCount) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
     //   unbinder = ButterKnife.bind(getContext(), view);
        rvLocations = (RecyclerView) view.findViewById(R.id.rvLocations);
        locations = new ArrayList<>();
        adapter = new LocationAdapter(locations);
        rvLocations.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvLocations.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(rvLocations.getContext(), DividerItemDecoration.VERTICAL);
        rvLocations.addItemDecoration(itemDecoration);

        ItemClickSupport.addTo(rvLocations).setOnItemClickListener(
                (recyclerView, position, vw) -> {
                    //create intent
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    TripLocation loc = locations.get(position);
                    intent.putExtra("location", Parcels.wrap(loc));
                    //launch activity
                    startActivity(intent);
                }
        );

        return view;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override public void onDestroyView() {
        super.onDestroyView();
     //   unbinder.unbind();
    }
}

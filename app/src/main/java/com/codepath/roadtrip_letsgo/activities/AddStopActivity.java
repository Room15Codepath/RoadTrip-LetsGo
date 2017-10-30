package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.TripRecyclerAdapter;
import com.codepath.roadtrip_letsgo.helper.ItemClickSupport;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.utils.Util;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddStopActivity extends AppCompatActivity {
    public final int SEARCH = 220;


    @BindView(R.id.rvResults)
    RecyclerView rvResults;
    @BindView(R.id.btnStart)
    Button btnStart;
    String stopType;
    TripLocation origin;
    TripLocation dest;

    ArrayList<TripLocation> trips;

    TripRecyclerAdapter adapter;
    private BottomSheetBehavior mBottomSheetBehavior;
    SupportMapFragment mapFragment;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stop);
        ButterKnife.bind(this);

        trips = new ArrayList<>();
        adapter = new TripRecyclerAdapter(this, trips);
        rvResults.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvResults.setLayoutManager(linearLayoutManager);
     //   parseIntent();
        getData();
        parseIntent();
        setButtonAction();
        ItemClickSupport.addTo(rvResults).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    //create intent
                    if(position%2 ==1) {
                        Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                        i.putExtra("origin", Parcels.wrap(origin));
                        i.putExtra("destination", Parcels.wrap(dest));
                        i.putExtra("stopType", stopType);
                        i.putExtra("position", position);
                        //launch activity
                        startActivity(i);
                    }
                }
        );
        compactToSave();
        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mBottomSheetBehavior.setPeekHeight(60);  // show top title
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                Log.d("DEBUG", "state change: " + newState);
                TextView tvTitle = bottomSheet.findViewById(R.id.bsTitle);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_downward, 0, 0, 0);
                }
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_upward, 0, 0, 0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                Log.d("DEBUG", "slide : " + slideOffset);
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                loadMap(googleMap);
            }
        });


    }
    public void parseIntent() {
        TripLocation stop = Parcels.unwrap(getIntent().getParcelableExtra("stop"));
//        Log.d("DEBUG", "new stop:" + stop.loc_name);
 //       dest = Parcels.unwrap(getIntent().getParcelableExtra("destination"));
        int pos = getIntent().getIntExtra("position", -1);
        if(pos>=0){
            trips.add(pos,new TripLocation());
            trips.add(pos+1, stop);
            adapter.notifyDataSetChanged();
        }else{
            if(stop !=null){
                Log.d("DEBUG", "size before:" + trips.size());
                trips.add(trips.size()-1, stop);
                trips.add(trips.size()-1,new TripLocation());
                adapter.notifyDataSetChanged();
                Log.d("DEBUG", "size after:" + trips.size());

            }
        }
        compactToSave();
    }

    private void getData(){
        ArrayList<TripLocation> list = Util.getStops(getApplicationContext());
        //trips.addAll(list);
        Log.d("DEBUG", "saved list size" + list.size());
     //   ArrayList<Parcelable> pList= getIntent().getParcelableArrayListExtra("stops");
        for( int i=0; i< list.size();i++) {
        //    Parcelable p = pList.get(i);
        //    TripLocation tp = Parcels.unwrap(p);
            trips.add(list.get(i));
            TripLocation bt = new TripLocation();
            trips.add(bt);
            if(i==0){
                origin = list.get(i);
            }
            if(i==list.size()-1) {
                dest =list.get(i);
            }
        }
        trips.remove(trips.size()-1);
        adapter.notifyDataSetChanged();
        Log.d("DEBUG", "after loading from pref:" + trips.size());
    }

    public void compactToSave(){
        ArrayList<TripLocation> list = new ArrayList<>();
     for(int i = 0;i<trips.size();i=i+2){
         list.add(trips.get(i));
     }
     Util.saveStops(getApplicationContext(),list);

    }

    private void setButtonAction() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
       //         mBottomSheetBehavior.setPeekHeight(300);
         //       mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


                StringBuilder sb = new StringBuilder();
                sb.append("https://www.google.com/maps/dir");
                for(int i =0; i< trips.size();i=i+2) {

                    sb.append("/" + trips.get(i).lat +","+ trips.get(i).lng );
                }

                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);

            }
        });
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SEARCH) {
            if(requestCode ==200) {
                TripLocation tp = Parcels.unwrap(data.getParcelableExtra("stop"));
                int position = data.getIntExtra("position", -1);
                if(position >=0) {
                    trips.add(position,new TripLocation());
                    trips.add(position+1, tp);
                    adapter.notifyDataSetChanged();
                }else {

   //                 adapter.notifyDataSetChanged();
                }
            }
        }
    }
*/

    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            //      ResultsActivityPermissionsDispatcher.getMyLocationWithCheck(this);
            //     ResultsActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
            map.getUiSettings().setZoomControlsEnabled(true);
            ArrayList<TripLocation> list = Util.getStops(getApplicationContext());
            TripLocation origin = list.get(0);
            TripLocation dest = list.get(list.size()-1);
            Util.addLocationMarkers(origin, dest, this, map);
            Util.addRoute(origin, dest, this, map);

/*            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(stop.trip_location.lat, stop.trip_location.lng))
                    .title(stop.trip_location.loc_name)
                    .snippet(stop.trip_location.address)
                    .icon(defaultMarker));
*/
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
         //   builder.include(marker.getPosition());
            builder.include(new LatLng(origin.lat, origin.lng));
            builder.include(new LatLng(dest.lat, dest.lng));
            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            // Zoom in the Google Map
            map.animateCamera(cu);

        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void drawRoute(GoogleMap map, List<TripLocation> list){


    }
}

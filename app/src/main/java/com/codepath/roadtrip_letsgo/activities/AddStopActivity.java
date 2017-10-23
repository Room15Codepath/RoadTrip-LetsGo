package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.TripRecyclerAdapter;
import com.codepath.roadtrip_letsgo.helper.ItemClickSupport;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.utils.StopType;

import org.parceler.Parcels;

import java.util.ArrayList;

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
        setButtonAction();
        ItemClickSupport.addTo(rvResults).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    //create intent
                    Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                    i.putExtra("origin", Parcels.wrap(origin));
                    i.putExtra("destination", Parcels.wrap(dest));
                    i.putExtra("stopType", stopType);
                    //launch activity
                    startActivityForResult(i, SEARCH);
                }
        );

    }
    public void parseIntent() {
        origin = Parcels.unwrap(getIntent().getParcelableExtra("origin"));
        dest = Parcels.unwrap(getIntent().getParcelableExtra("destination"));
        stopType = getIntent().getStringExtra("stopType");
        if(stopType == null){
            stopType = StopType.CAFE.toString();
        }
    }

    private void getData(){
        ArrayList<Parcelable> pList= getIntent().getParcelableArrayListExtra("stops");
        for( int i=0; i< pList.size();i++) {
            Parcelable p = pList.get(i);
            TripLocation tp = Parcels.unwrap(p);
            trips.add(tp);
            TripLocation bt = new TripLocation();
            trips.add(bt);
            if(i==0){
                origin = tp;
            }
            if(i==pList.size()-1) {
                dest =tp;
            }
            Log.d("LOAD", "stop:" + tp.loc_name );
            Log.d("LOAD", "stop:" + tp.address );

        }
        trips.remove(trips.size()-1);
        adapter.notifyDataSetChanged();
    }

    private void setButtonAction() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                sb.append("https://www.google.com/maps/dir");
                for(int i =0; i< trips.size();i=i+2) {

                    sb.append("/" + trips.get(i).lat +","+ trips.get(i).lng );
                }

//                sb.append("/" + origin.getLatLng().latitude +","+ origin.getLatLng().longitude );

  //              sb.append("/" + destination.getLatLng().latitude +","+ destination.getLatLng().longitude);

                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SEARCH) {
            if(requestCode ==200) {
                TripLocation tp = Parcels.unwrap(data.getParcelableExtra("stop"));
             //   trips.add(cPosition,tp);
            //    adapter.notifyDataSetChanged();
            }
        }
    }
}

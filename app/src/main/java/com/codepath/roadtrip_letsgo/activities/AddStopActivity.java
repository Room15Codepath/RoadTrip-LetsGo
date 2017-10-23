package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.TripRecyclerAdapter;
import com.codepath.roadtrip_letsgo.models.TripLocation;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddStopActivity extends AppCompatActivity {
    @BindView(R.id.rvResults)
    RecyclerView rvResults;
    @BindView(R.id.btnStart)
    Button btnStart;

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

        getData();
        setButtonAction();

    }

    private void getData(){
        ArrayList<Parcelable> pList= getIntent().getParcelableArrayListExtra("stops");
        for( Parcelable p: pList) {
            trips.add(Parcels.unwrap(p));
        }
        adapter.notifyDataSetChanged();
    }

    private void setButtonAction() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                sb.append("https://www.google.com/maps/dir");
                for(TripLocation t: trips) {
                    sb.append("/" + t.point.latitude +","+ t.point.longitude );
                }

//                sb.append("/" + origin.getLatLng().latitude +","+ origin.getLatLng().longitude );

  //              sb.append("/" + destination.getLatLng().latitude +","+ destination.getLatLng().longitude);

                Intent i = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(sb.toString()));
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);

            }
        });
    }
}

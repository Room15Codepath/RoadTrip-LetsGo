package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.codepath.roadtrip_letsgo.utils.Util;

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

    }
    public void parseIntent() {
        TripLocation stop = Parcels.unwrap(getIntent().getParcelableExtra("stop"));
        Log.d("DEBUG", "new stop:" + stop.loc_name);
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
}

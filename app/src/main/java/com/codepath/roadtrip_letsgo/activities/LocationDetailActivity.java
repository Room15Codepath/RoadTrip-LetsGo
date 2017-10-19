package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.helper.GlideApp;
import com.codepath.roadtrip_letsgo.models.TripStop;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationDetailActivity extends AppCompatActivity {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    TripStop stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);
        ButterKnife.bind(this);

        stop = Parcels.unwrap(getIntent().getParcelableExtra("location"));
        tvName.setText(stop.trip_location.loc_name);
        tvAddress.setText(stop.trip_location.address );
        //Glide.with(this).from(stop.image_url).into(ivImage);
        GlideApp.with(this).load(stop.image_url).override(300,300).fitCenter().into(ivImage);

        Log.d("DEBUG", "input location:" + stop.trip_location.loc_name);
    }

    public void onDirection(View view) {

        String gmmIntentUri = String.format("google.navigation:q=%s,+%s", stop.trip_location.loc_name, stop.trip_location.address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri));
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }


    }
}

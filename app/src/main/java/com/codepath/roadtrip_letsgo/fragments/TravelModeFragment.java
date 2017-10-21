package com.codepath.roadtrip_letsgo.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.activities.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by luba on 10/18/17.
 */

public class TravelModeFragment  extends Fragment {

    private static final String TAG_LOG = TravelModeFragment.class.getCanonicalName();

    public static String mode = "driving";

    public enum TravelMode {
        MODE_DRIVING,
        MODE_BICYCLING,
        MODE_WALKING
    }



    @BindView(R.id.btnCar)
    ImageView btnCar;

    @BindView(R.id.btnBike)
    ImageView btnBike;

    @BindView(R.id.btnWalk)
    ImageView btnWalk;

    @BindView(R.id.ratingSeekBar)
    SeekBar rating;

    @BindView(R.id.rangeSeekBar)
    SeekBar range;

    @BindView(R.id.labelMiles)
    TextView miles;

    @BindView(R.id.labelStars)
    TextView stars;

    @BindView(R.id.btnSave)
    Button btnSave;
    private Unbinder unbinder;



    public TravelModeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel_mode, container, false);
        unbinder = ButterKnife.bind(this, view);
        initializeDefaultValues();


        rating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String starsText = String.format("%.1f stars", rating.getProgress()/2.0);
                stars.setText(starsText);

            }
        });

        range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String milesText = String.format("%.1f miles", range.getProgress()/10.0);
                miles.setText(milesText);
            }
        });

        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTravelMode (TravelMode.MODE_DRIVING);
                mode = "driving";
            }
        });

        btnBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTravelMode (TravelMode.MODE_BICYCLING);
                mode = "bicycling";
            }
        });

        btnWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTravelMode (TravelMode.MODE_WALKING);
                mode="walking";

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d (TAG_LOG, "mode="+mode);
                float starsValue = (float) (rating.getProgress()/2.0);
                Log.d (TAG_LOG, "starsValue="+starsValue);
                float rangeValue = (float) (range.getProgress()/10.0);
                Log.d (TAG_LOG, "rangeValue="+rangeValue);
                Intent i = new Intent(getActivity(), HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", mode);
                bundle.putFloat("rating", starsValue);
                bundle.putFloat("range", rangeValue);
                i = i.putExtras(bundle);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setTravelMode (TravelMode type) {
        if (type == TravelMode.MODE_DRIVING) {
            btnCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car_clicked));
            btnBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike));
            btnWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk));
        }
        else if (type == TravelMode.MODE_BICYCLING) {
            btnBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike_clicked));
            btnCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car));
            btnWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk));
        }
        else {
            btnWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk_clicked));
            btnCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car));
            btnBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike));
        }
    }

    private void initializeDefaultValues() {
        setTravelMode (TravelMode.MODE_DRIVING);
        range.setProgress(20);
        range.setMax(250);
        String milesText = String.format("%.1f miles", range.getProgress()/10.0);
        miles.setText(milesText);

        rating.setProgress(2);
        rating.setMax(10);
        String starsText = String.format("%.1f stars", rating.getProgress()/2.0);
        stars.setText(starsText);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

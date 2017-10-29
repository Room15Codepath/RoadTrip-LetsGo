package com.codepath.roadtrip_letsgo.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codepath.roadtrip_letsgo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.SharedPreferences.Editor;

/**
 * Created by luba on 10/18/17.
 */

public class TravelModeFragment  extends DialogFragment {

    private static final String TAG_LOG = TravelModeFragment.class.getCanonicalName();
    private static final int MODAL_WIDTH = 1300;
    private static final int MODAL_HEIGHT = 1750;

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
    @BindView(R.id.btnCancel)
    Button btnCancel;

    private Unbinder unbinder;



    public TravelModeFragment() {
        // Required empty public constructor
    }

    public static TravelModeFragment newInstance() {
        TravelModeFragment fragment = new TravelModeFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        View view = inflater.inflate(R.layout.fragment_travel_mode, container, false);
        unbinder = ButterKnife.bind(this, view);
        initializeDefaultValues();
        btnCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car));
        btnBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike));
        btnWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk));

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

        btnCar.setOnClickListener(v -> {
            setTravelMode (TravelMode.MODE_DRIVING);
            mode = "driving";
        });

        btnBike.setOnClickListener(v -> {
            setTravelMode (TravelMode.MODE_BICYCLING);
            mode = "bicycling";
        });

        btnWalk.setOnClickListener(v -> {
            setTravelMode (TravelMode.MODE_WALKING);
            mode="walking";

        });

        btnSave.setOnClickListener(v -> {
            Log.d (TAG_LOG, "mode="+mode);
            float starsValue = (float) (rating.getProgress()/2.0);
            Log.d (TAG_LOG, "starsValue="+starsValue);
            float rangeValue = (float) (range.getProgress()/10.0);
            Log.d (TAG_LOG, "rangeValue="+rangeValue);
            SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences("settings", 0);
            Editor editor = settings.edit();
            editor.putString("mode",mode);
            editor.putFloat("rating",starsValue);
            editor.putFloat("range",rangeValue);
            editor.commit();
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(MODAL_WIDTH, MODAL_HEIGHT);
        window.setGravity(Gravity.CENTER);
    }

    private void setTravelMode (TravelMode type) {
        DrawableCompat.setTint(btnCar.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        DrawableCompat.setTint(btnBike.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        DrawableCompat.setTint(btnWalk.getDrawable(), ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        if (type == TravelMode.MODE_DRIVING) {
            DrawableCompat.setTint(btnCar.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        else if (type == TravelMode.MODE_BICYCLING) {
            DrawableCompat.setTint(btnBike.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        else {
            DrawableCompat.setTint(btnWalk.getDrawable(), ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    private void initializeDefaultValues() {

        SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences("settings", 0);
        float rangeValue = settings.getFloat("range", 20)*10;
        float ratingValue = settings.getFloat("rating", 2)*2;
        setTravelMode (TravelMode.MODE_DRIVING);
        range.setProgress((int)rangeValue);
        range.setMax(60);
        String milesText = String.format("%.1f miles", range.getProgress()/10.0);
        miles.setText(milesText);

        rating.setProgress((int)ratingValue);
        rating.setMax(10);
        String starsText = String.format("%.1f stars", rating.getProgress()/2.0);
        stars.setText(starsText);

    }
}

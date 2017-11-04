package com.codepath.roadtrip_letsgo.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.activities.HomeActivity;
import com.codepath.roadtrip_letsgo.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TravelModeFragment extends DialogFragment {

    private static final String TAG_LOG = TravelModeFragment.class.getCanonicalName();
    public static String mode;

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

    private Unbinder unbinder;

    public TravelModeFragment() {
        // Required empty public constructor
    }

    public static TravelModeFragment newInstance() {
        TravelModeFragment fragment = new TravelModeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_mode, container, false);

        unbinder = ButterKnife.bind(this, view);
        btnCar.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_car));
        btnBike.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_bike));
        btnWalk.setImageDrawable(getResources().getDrawable(R.drawable.ic_directions_walk));
        initializeDefaultMode();


        btnCar.setOnClickListener(v -> {
            setTravelMode(TravelMode.MODE_DRIVING);
            mode = "driving";
            Util.saveTravelMode(getActivity().getApplicationContext(), mode);
            ((HomeActivity) getActivity()).setIconForTravelMode(true, false, false);
            dismiss();
        });

        btnBike.setOnClickListener(v -> {
            setTravelMode(TravelMode.MODE_BICYCLING);
            mode = "bicycling";
            Util.saveTravelMode(getActivity().getApplicationContext(), mode);
            ((HomeActivity) getActivity()).setIconForTravelMode(false, true, false);
            dismiss();
        });

        btnWalk.setOnClickListener(v -> {
            setTravelMode(TravelMode.MODE_WALKING);
            mode = "walking";
            Util.saveTravelMode(getActivity().getApplicationContext(), mode);
            ((HomeActivity) getActivity()).setIconForTravelMode(false, false, true);
            dismiss();

        });

        return view;
    }

    private void initializeDefaultMode() {
        //SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences
        // ("settings", 0);
        String travelMode = Util.getTravelMode(getActivity().getApplicationContext());
        Log.d("home fragment", "default mode=" + travelMode);
        if (travelMode.equals("driving")) {
            setTravelMode(TravelMode.MODE_DRIVING);
        } else if (travelMode.equals("bicycling")) {
            setTravelMode(TravelMode.MODE_BICYCLING);
        } else if (travelMode.equals("walking")) {
            setTravelMode(TravelMode.MODE_WALKING);
        }

        if (travelMode.isEmpty()) {
            setTravelMode(TravelMode.MODE_DRIVING);
        }

    }

    private void setTravelMode(TravelMode type) {
        DrawableCompat.setTint(btnCar.getDrawable(),
                ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        DrawableCompat.setTint(btnBike.getDrawable(),
                ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        DrawableCompat.setTint(btnWalk.getDrawable(),
                ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        if (type == TravelMode.MODE_DRIVING) {
            Log.d("home fragment", "setTravelMode=" + type);
            DrawableCompat.setTint(btnCar.getDrawable(),
                    ContextCompat.getColor(getContext(), R.color.colorAccent));
        } else if (type == TravelMode.MODE_BICYCLING) {
            DrawableCompat.setTint(btnBike.getDrawable(),
                    ContextCompat.getColor(getContext(), R.color.colorAccent));
        } else {
            DrawableCompat.setTint(btnWalk.getDrawable(),
                    ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    /*private void  saveModeToShared() {
        Log.d (TAG_LOG, "mode="+mode);
        SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences
        ("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mode", mode);
        editor.commit();
        dismiss();
    }*/

}

package com.codepath.roadtrip_letsgo.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class FilterFragment extends DialogFragment {

    private static final String TAG_LOG = FilterFragment.class.getCanonicalName();
    //private static final int MODAL_WIDTH = 1200;
    //private static final int MODAL_HEIGHT = 1500;


    //@BindView(R.id.ratingSeekBar)
    //SeekBar rating;

    @BindView(R.id.rangeSeekBar)
    SeekBar range;

    @BindView(R.id.labelMiles)
    TextView miles;

    //@BindView(R.id.labelStars)
    //TextView stars;

    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.btnCancel)
    Button btnCancel;

    private Unbinder unbinder;


    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance() {
        FilterFragment fragment = new FilterFragment();

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
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        unbinder = ButterKnife.bind(this, view);
        initializeDefaultValues();
        /*rating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
        });*/

        range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String milesText = String.format("%.1f miles", range.getProgress() / 10.0);
                miles.setText(milesText);
            }
        });

        btnSave.setOnClickListener(v -> {
            //float starsValue = (float) (rating.getProgress()/2.0);
            //Log.d (TAG_LOG, "starsValue="+starsValue);
            float rangeValue = (float) (range.getProgress() / 10.0);
            Log.d(TAG_LOG, "rangeValue=" + rangeValue);
            SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences(
                    "settings", 0);
            Editor editor = settings.edit();
            //editor.putFloat("rating",starsValue);
            editor.putFloat("range", rangeValue);
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
        //Window window = getDialog().getWindow();
        //window.setLayout(MODAL_WIDTH, MODAL_HEIGHT);
        //window.setGravity(Gravity.CENTER);
    }


    private void initializeDefaultValues() {

        SharedPreferences settings = getActivity().getApplicationContext().getSharedPreferences(
                "settings", 0);
        float rangeValue = settings.getFloat("range", 20) * 10;
        float ratingValue = settings.getFloat("rating", 2) * 2;
        range.setProgress((int) rangeValue);
        range.setMax(60);
        String milesText = String.format("%.1f miles", range.getProgress() / 10.0);
        miles.setText(milesText);

        /*rating.setProgress((int)ratingValue);
        rating.setMax(10);
        String starsText = String.format("%.1f stars", rating.getProgress()/2.0);
        stars.setText(starsText);*/

    }
}

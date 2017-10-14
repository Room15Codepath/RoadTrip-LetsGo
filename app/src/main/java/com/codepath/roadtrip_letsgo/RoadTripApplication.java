package com.codepath.roadtrip_letsgo;

import android.app.Application;
import android.content.Context;

/**
 * Created by luba on 10/12/17.
 */

public class RoadTripApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        RoadTripApplication.context = this;

    }
}

package com.codepath.roadtrip_letsgo;

import android.app.Application;
import android.content.Context;

import com.codepath.roadtrip_letsgo.network.YelpClient;

/**
 * Created by luba on 10/12/17.
 */

public class RoadTripApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        RoadTripApplication.context = this;
        getYelpClient();
    }

    public static YelpClient getYelpClient() {
        return (YelpClient) YelpClient.getInstance(RoadTripApplication.context);
    }
}

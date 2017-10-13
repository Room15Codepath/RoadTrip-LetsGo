package com.codepath.roadtrip_letsgo.network;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.SearchResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tessavoon on 10/12/17.
 */

public class YelpClient {
    YelpFusionApiFactory yelpFusionApiFactory;
    YelpFusionApi yelpFusionApi;

    Thread thread = new Thread(new Runnable(){
        @Override
        public void run() {
            try {
                yelpFusionApiFactory = new YelpFusionApiFactory();
                yelpFusionApi = yelpFusionApiFactory.createAPI("r1-mFA6jNnBIaR3HY20j8w",
                        "N42SMbUfi5NCFRFv7PNg3PnoT2UsgcQjAn9pGG350OgMpEKV88f6zxDbg9RO0XhO");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

//    public static YelpClient getYelpInstance() {
//
//    }

    public YelpClient() {
        thread.start();
    }

    public void searchStops() {
        //YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
        try {
            Map<String, String> params = new HashMap<>();

            params.put("term", "indian food");
            params.put("latitude", "40.581140");
            params.put("longitude", "-111.914184");
            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            //Response<SearchResponse> response = call.execute();
            call.enqueue(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Callback<SearchResponse> callback = new Callback<SearchResponse>() {
        @Override
        public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
            SearchResponse searchResponse = response.body();
            // Update UI text with the searchResponse.
        }
        @Override
        public void onFailure(Call<SearchResponse> call, Throwable t) {
            // HTTP error happened, do something to handle it.
        }
    };


}

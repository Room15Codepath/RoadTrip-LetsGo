package com.codepath.roadtrip_letsgo.network;

import android.content.Context;
import android.util.Log;

import com.codepath.roadtrip_letsgo.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by tessavoon on 10/18/17.
 */

public class YelpClient {

    private static Context mContext;

    private static final String BASE_URL = "https://api.yelp.com/v3";
    private static final String TOKEN_URL = "https://api.yelp.com/oauth2/token";
    private static String CLIENT_ID;
    private static String SECRET_KEY;

    private AsyncHttpClient client;
    private static YelpClient instance;
    private String accessToken;

    private YelpClient() {
        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("grant_type", "client_credentials");
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", SECRET_KEY);
        client.post(TOKEN_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("yelp token", response.toString());
                try {
                    accessToken = response.getString("access_token");
                    client.addHeader("Authorization", "Bearer " + accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                    JSONObject errorResponse) {
                Log.d("yelp token", errorResponse.toString());
            }
        });

    }

    public static YelpClient getInstance(Context context) {
        mContext = context;
        CLIENT_ID = mContext.getString(R.string.yelp_client_id);
        SECRET_KEY = mContext.getString(R.string.yelp_secret_key);
        if (instance == null) {
            instance = new YelpClient();
        }
        return instance;
    }

    public void getBusinesses(RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteUrl("/businesses/search"), params, handler);
    }

    public void getBusinessDetails(String businessId, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteUrl("/businesses/" + businessId), handler);
    }

    public void getBusinessReviews(String businessId, AsyncHttpResponseHandler handler) {
        client.get(getAbsoluteUrl("/businesses/" + businessId + "/reviews"), handler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}

package com.codepath.roadtrip_letsgo.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.helper.GlideApp;
import com.codepath.roadtrip_letsgo.models.TripLocation;
import com.codepath.roadtrip_letsgo.models.TripStop;
import com.codepath.roadtrip_letsgo.network.YelpClient;
import com.codepath.roadtrip_letsgo.utils.Util;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.codepath.roadtrip_letsgo.RoadTripApplication.getYelpClient;

public class PlaceDetailActivity extends AppCompatActivity {
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvReviewCount)
    TextView tvReviewCount;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.tvCategories)
    TextView tvCategories;
    @BindView(R.id.tvHours)
    TextView tvHours;
    @BindView(R.id.ivReviewer)
    ImageView ivReviewer;
    @BindView(R.id.tvReviewBody)
    TextView tvReviewBody;
    @BindView(R.id.tvReviewDate)
    TextView tvReviewDate;
    @BindView(R.id.rbReview)
    RatingBar rbReview;
    @BindView(R.id.rlReview)
    RelativeLayout rlReview;
    @BindView(R.id.tvReviewLabel)
    TextView tvReviewLabel;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.header)
    ImageView header;


    // @BindView(R.id.btnStart)
    // Button btnStart;

    TripLocation origin;
    TripLocation dest;
    TripStop stop;
    GoogleMap map;
    String shareText;
    Context mContext;
    SupportMapFragment mapFragment;
    YelpClient yelpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        yelpClient = getYelpClient();
        origin = Util.getOrigin(mContext);
        dest = Util.getDestination(mContext);
        stop = Parcels.unwrap(getIntent().getParcelableExtra("location"));
        ratingBar.setRating((float)(stop.rating));
        tvPhone.setText(stop.phone);
        tvPrice.setText(stop.price);
        float distance = Util.getDistance(origin.lat, origin.lng, stop.trip_location.lat, stop.trip_location.lng);
        tvDistance.setText(String.format("%.1f", distance * 0.0006213719) +" mi");
        tvReviewCount.setText(stop.review_count + " Reviews");
        tvName.setText(stop.trip_location.loc_name);
        tvCategories.setText(stop.getCategoriesStr());
        setOpeningHours();
        setUserReview();
        toolbarLayout.setTitle(stop.trip_location.loc_name);
        toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        GlideApp.with(this).load(stop.image_url).fitCenter().into(header);
        tvAddress.setText(stop.trip_location.address);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                loadMap(googleMap);
            }
        });

        Log.d("DEBUG", "input location:" + stop.trip_location.loc_name);
        setSupportActionBar(toolbar);
    }

    public void setOpeningHours() {
        yelpClient.getBusinessDetails(stop.getYelp_id(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("yelp biz details", response.toString());
                try {
                    JSONArray hours = response.getJSONArray("hours");
                    if (hours != null) {
                        JSONArray openingHrs = hours.getJSONObject(0).getJSONArray("open");
                        if (openingHrs != null) {
                            StringBuilder sb = new StringBuilder();
                            for (int i=0; i<openingHrs.length(); i++) {
                                sb.append(Util.getOpeningHrsStr(openingHrs.getJSONObject(i)));
                                sb.append("\n");
                            }
                            Log.d("tvhours", sb.toString());
                            tvHours.setText(sb.toString());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("yelp biz details", errorResponse.toString());
            }
        });
    }


    public void setUserReview() {
        yelpClient.getBusinessReviews(stop.getYelp_id(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("yelp biz reviews", response.toString());
                try {
                    JSONArray reviewList = response.getJSONArray("reviews");
                    if (reviewList.length() > 0) {
                        JSONObject firstReview = reviewList.getJSONObject(0);
                        String imageUrl = firstReview.getJSONObject("user").getString("image_url");
                        String body = firstReview.getString("text");
                        String date = Util.getRelativeTimeAgo(firstReview.getString("time_created"));
                        int rating = firstReview.getInt("rating");
                        GlideApp.with(mContext).load(imageUrl)
                                .apply(bitmapTransform(new RoundedCornersTransformation(25, 0,
                                        RoundedCornersTransformation.CornerType.ALL)))
                                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.com_facebook_profile_picture_blank_square))
                                .into(ivReviewer);
                        tvReviewBody.setText(body);
                        tvReviewDate.setText(date);
                        rbReview.setRating(rating);
                    } else {
                        rlReview.setVisibility(View.GONE);
                        tvReviewLabel.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("yelp biz details", errorResponse.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);

        return super.onCreateOptionsMenu(menu);
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

    private void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
//            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            //      ResultsActivityPermissionsDispatcher.getMyLocationWithCheck(this);
            //     ResultsActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
            map.getUiSettings().setZoomControlsEnabled(true);
            Util.addLocationMarkers(origin, dest, this, map);
            BitmapDescriptor defaultMarker =
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(stop.trip_location.lat, stop.trip_location.lng))
                    .title(stop.trip_location.loc_name)
                    .snippet(stop.trip_location.address)
                    .icon(defaultMarker));
            Util.addRoute(origin, dest, this, map);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(marker.getPosition());
            builder.include(new LatLng(origin.lat, origin.lng));
            builder.include(new LatLng(dest.lat, dest.lng));
            LatLngBounds bounds = builder.build();

           int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
           // CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
         //   map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(stop.trip_location.lat, stop.trip_location.lng)));

            // Zoom in the Google Map
            map.animateCamera(cu);

        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onShare(MenuItem item) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareText = String.format("%s\n%s\n%s\nShared by Roadtrip LetsGo app", stop.trip_location.loc_name, stop.trip_location.address, stop.phone);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        final List<ResolveInfo> activities = getPackageManager().queryIntentActivities (shareIntent, 0);

        List<String> appNames = new ArrayList<String>();
        for (ResolveInfo info : activities) {
            appNames.add(info.loadLabel(getPackageManager()).toString());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share using...");
        builder.setItems(appNames.toArray(new CharSequence[appNames.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ResolveInfo info = activities.get(item);
                if (info.activityInfo.packageName.equals("com.facebook.katana")) {
                    setupFacebookShareIntent();
                }
                // start the selected activity
                shareIntent.setPackage(info.activityInfo.packageName);
                startActivity(shareIntent);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setupFacebookShareIntent() {
        ShareDialog shareDialog;
        shareDialog = new ShareDialog(this);

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote(shareText)
                .setContentUrl(Uri.parse(stop.url))
                .build();
        shareDialog.show(linkContent);
    }

    @OnClick(R.id.fab)
    public void onButtonClick() {
        ArrayList<TripLocation> listFromShared = Util.getStops(getApplicationContext());

        if (listFromShared.contains(stop.trip_location)) {
            listFromShared.set(listFromShared.indexOf(stop.trip_location), stop.trip_location);
        } else {
            Util.saveStop(mContext, stop.trip_location);
        }
        Snackbar.make(fab, R.string.snackbar_add_stop, Snackbar.LENGTH_LONG)
                .show();

    }
}

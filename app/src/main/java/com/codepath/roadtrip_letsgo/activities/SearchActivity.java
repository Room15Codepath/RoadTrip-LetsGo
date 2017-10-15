package com.codepath.roadtrip_letsgo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.SearchPagerAdapter;
import com.codepath.roadtrip_letsgo.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.roadtrip_letsgo.models.TripLocation;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchActivity extends AppCompatActivity {
    public static String POSITION = "POSITION";
    private final int REQUEST_CODE_ADD = 10;  //for add stop
    private final int REQUEST_CODE_SET = 20;  //for settings
    TripLocation origin;
    TripLocation dest;

    //private SearchView searchView;
    private SmartFragmentStatePagerAdapter adapterViewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.viewpager) ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
 //       ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapterViewPager =new SearchPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(adapterViewPager);
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        origin = Parcels.unwrap(getIntent().getParcelableExtra("origin"));
        dest = Parcels.unwrap(getIntent().getParcelableExtra("destination"));
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //query
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add) {
            Intent i = new Intent(this, AddStopActivity.class);
            i.putExtra("start", Parcels.wrap(origin));
            i.putExtra("end", Parcels.wrap(dest));
            startActivityForResult(i, REQUEST_CODE_ADD);
        }

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra("home", "MY HOME");
            startActivityForResult(i, REQUEST_CODE_SET);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }
}

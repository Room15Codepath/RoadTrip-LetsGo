package com.codepath.roadtrip_letsgo.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.codepath.roadtrip_letsgo.R;
import com.codepath.roadtrip_letsgo.adapters.SearchPagerAdapter;
import com.codepath.roadtrip_letsgo.adapters.SmartFragmentStatePagerAdapter;


public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private SmartFragmentStatePagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
       // Toolbar toolbar = (Toolbar) findViewById(R.);
       // setSupportActionBar(toolbar);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        adapterViewPager =new SearchPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(adapterViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
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
}

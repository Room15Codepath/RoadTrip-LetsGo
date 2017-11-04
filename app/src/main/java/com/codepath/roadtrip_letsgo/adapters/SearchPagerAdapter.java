package com.codepath.roadtrip_letsgo.adapters;

/**
 * Created by prernamanaktala on 10/13/17.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.roadtrip_letsgo.fragments.ListViewFragment;
import com.google.android.gms.maps.SupportMapFragment;


/**
 * Created by prernamanaktala on 10/5/17.
 */

public class SearchPagerAdapter extends SmartFragmentStatePagerAdapter {


    private String[] tabTitles = new String[]{"Map", "List"};
    private Context mContext;

    public SearchPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return SupportMapFragment.newInstance();
        } else if (position == 1) {
            return ListViewFragment.newInstance(1);
        } else {
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}

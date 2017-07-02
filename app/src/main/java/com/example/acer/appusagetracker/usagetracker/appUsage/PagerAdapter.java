package com.example.acer.appusagetracker.usagetracker.appUsage;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.acer.appusagetracker.usagetracker.appUsage.AppUsageStatisticsFragment;
import com.example.acer.appusagetracker.usagetracker.barchart.BarChartView;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AppUsageStatisticsFragment tab1 = new AppUsageStatisticsFragment();
                return tab1;
            case 1:
                BarChartView tab2 = new BarChartView();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

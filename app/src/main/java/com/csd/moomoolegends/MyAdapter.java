package com.csd.moomoolegends;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.Map;


public class MyAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;
    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RecordFragment meat = new RecordFragment("Meat");
                return meat;
            case 1:
                RecordFragment dairy = new RecordFragment("Dairy");
                return dairy;
            case 2:
                RecordFragment carbs = new RecordFragment("Carbs");
                return carbs;
            case 3:
                RecordFragment veg = new RecordFragment("Veg");
                return veg;
            case 4:
                RecordFragment seafood = new RecordFragment("Seafood");
                return seafood;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

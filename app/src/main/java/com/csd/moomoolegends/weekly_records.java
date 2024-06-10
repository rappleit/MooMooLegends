package com.csd.moomoolegends;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class weekly_records extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_records);
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);

        arrayList=new ArrayList<>(0);

        // Add title in array list
        arrayList.add("Basic");
        arrayList.add("Advance");
        arrayList.add("Pro");

        // Setup tab layout
        tabLayout.setupWithViewPager(viewPager);

        // Prepare view pager
        prepareViewPager(viewPager,arrayList);
        pieChartInit();
    }

    private void pieChartInit() {
        PieChart weekly=(PieChart) findViewById(R.id.weeklyPie);
        List<PieEntry> pieEntries= new ArrayList<>();
        pieEntries.add(new PieEntry(18f,"Meat"));
        pieEntries.add(new PieEntry(18f,"Diary"));
        pieEntries.add(new PieEntry(18f,"Carbs"));
        pieEntries.add(new PieEntry(18f,"Veg"));
        pieEntries.add(new PieEntry(18f,"Seafood"));
        PieDataSet weeklyPieSet=new PieDataSet(pieEntries,"Food Type");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#67C587"));
        colors.add(Color.parseColor("#88D1A1"));
        colors.add(Color.parseColor("#A9DEBA"));
        colors.add(Color.parseColor("#C9EAD4"));
        colors.add(Color.parseColor("#EAF6ED"));
        weeklyPieSet.setColors(colors);
        weeklyPieSet.setSliceSpace(2f);

        weeklyPieSet.setDrawValues(false);


        PieData pieData= new PieData(weeklyPieSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.parseColor("#7F4C00"));
        pieData.setValueTypeface(getResources().getFont(R.font.pixeloidsans));
        pieData.setValueTextSize(15f);
        weekly.setData(pieData);

        weekly.setEntryLabelTypeface(getResources().getFont(R.font.pixeloidsans));
        weekly.setEntryLabelColor(Color.parseColor("#7F4C00"));
        weekly.setEntryLabelTextSize(20f);
        weekly.setDrawHoleEnabled(false);
        weekly.setUsePercentValues(true);
        weekly.getDescription().setEnabled(false);
        weekly.setRotationAngle(0);
        // enable rotation of the chart by touch
        weekly.setRotationEnabled(true);
        weekly.setHighlightPerTapEnabled(true);
        weekly.animateY(1400, Easing.EaseInOutQuad);
        weekly.getLegend().setEnabled(false);



        weekly.invalidate();
    }


    private void prepareViewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        // Initialize main adapter
        MainAdapter adapter=new MainAdapter(getSupportFragmentManager());

        // Initialize main fragment
        dairy dairy_tab=new dairy();

        // Use for loop
        for(int i=0;i<arrayList.size();i++)
        {
            // Initialize bundle
            Bundle bundle=new Bundle();

            // Put title
            bundle.putString("title",arrayList.get(i));

            // set argument
            dairy_tab.setArguments(bundle);

            // Add fragment
            adapter.addFragment(dairy_tab, arrayList.get(i));
            dairy_tab=new dairy();
        }
        // set adapter
        viewPager.setAdapter(adapter);
    }

    private class MainAdapter extends FragmentPagerAdapter {
        // Initialize arrayList
        ArrayList<Fragment> fragmentArrayList= new ArrayList<>();
        ArrayList<String> stringArrayList=new ArrayList<>();


        // Create constructor
        public void addFragment(Fragment fragment,String s)
        {
            // Add fragment
            fragmentArrayList.add(fragment);
            // Add title
            stringArrayList.add(s);
        }

        public MainAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // return fragment position
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            // Return fragment array list size
            return fragmentArrayList.size();
        }
    }
}
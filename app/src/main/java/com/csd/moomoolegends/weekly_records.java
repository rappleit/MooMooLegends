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
        tabInit();
        pieChartInit();
    }

    private void tabInit() {
        tabLayout.addTab(tabLayout.newTab().setText("Meat"));
        tabLayout.addTab(tabLayout.newTab().setText("Dairy"));
        tabLayout.addTab(tabLayout.newTab().setText("Carbs"));
        tabLayout.addTab(tabLayout.newTab().setText("Veg"));
        tabLayout.addTab(tabLayout.newTab().setText("Seafood"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final MyAdapter adapter = new MyAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
}
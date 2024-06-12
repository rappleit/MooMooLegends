package com.csd.moomoolegends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.csd.moomoolegends.models.WeeklyRecords;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class weekly_records extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_records);
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        ImageButton backButt=findViewById(R.id.imageBackButton);
        backButt.setOnClickListener(v -> finish());
        //edit total co2e
        TextView weeklyCarbon=findViewById(R.id.weekly_carbon_text);
        String weeklyCarbonString=WeeklyRecords.getTotalCarbonFootprint()+" kg CO2e\nthis week";
        weeklyCarbon.setText(weeklyCarbonString);

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
        pieEntries.add(new PieEntry((Float) WeeklyRecords.getMeat().get("categoryCarbonFootprint"),"Meat"));
        pieEntries.add(new PieEntry((Float) WeeklyRecords.getDairy().get("categoryCarbonFootprint"),"Dairy"));
        pieEntries.add(new PieEntry((Float) WeeklyRecords.getCarbs().get("categoryCarbonFootprint"),"Carbs"));
        pieEntries.add(new PieEntry((Float) WeeklyRecords.getVeg().get("categoryCarbonFootprint"),"Veg"));
        pieEntries.add(new PieEntry((Float) WeeklyRecords.getSeafood().get("categoryCarbonFootprint"),"Seafood"));
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
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.parseColor("#7F4C00"));
        pieData.setValueTypeface(getResources().getFont(R.font.pixeloidsans));
        weekly.setData(pieData);

        weekly.setEntryLabelTypeface(getResources().getFont(R.font.pixeloidsans));
        weekly.setEntryLabelColor(Color.parseColor("#7F4C00"));
        weekly.setEntryLabelTextSize(15f);
        weekly.setDrawHoleEnabled(false);
        weekly.setUsePercentValues(true);
        weekly.getDescription().setEnabled(false);
        weekly.setRotationAngle(0);
        weekly.getLegend().setEnabled(false);

        // enable rotation of the chart by touch
        weekly.setRotationEnabled(true);
        weekly.setHighlightPerTapEnabled(true);
        weekly.animateY(1400, Easing.EaseInOutQuad);

        weekly.invalidate();
    }
}
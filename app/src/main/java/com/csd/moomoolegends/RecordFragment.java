package com.csd.moomoolegends;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.csd.moomoolegends.models.WeeklyRecords;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RecordFragment extends Fragment {
    String foodType;
    BarChart leBar;
    Map foodMap;

    public RecordFragment(String foodType) {
        this.foodType=foodType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leBar = view.findViewById(R.id.weekly_bar);
        barGraphInit();

    }
    private void barGraphInit() {
        switch (foodType) {
            case "Meat":
                foodMap=WeeklyRecords.getMeat();
                break;
            case "Dairy":
                foodMap=WeeklyRecords.getDairy();
                break;
            case "Carbs":
                foodMap=WeeklyRecords.getCarbs();
                break;
            case "Veg":
                foodMap=WeeklyRecords.getVeg();
                break;
            case "Seafood":
                foodMap=WeeklyRecords.getSeafood();
                break;
        }

        List<Map.Entry<String, Float>> list = new ArrayList<>(foodMap.entrySet());
        list.sort((Comparator.<Map.Entry<String, Float>>comparingDouble(list1 -> (double) list1.getValue()).thenComparingDouble(list2 -> (double) list2.getValue())).reversed());
        Log.d("Debug", list.toString());
        list.remove(0);
        Log.d("Debug", "Removed: " + list.toString());
        List<BarEntry> entries = new ArrayList<>();
        if (list.size()<=4) {
            switch (list.size()) {
                case 1:
                    entries.add(new BarEntry(0f, list.get(0).getValue()));
                    break;
                case 2:
                    entries.add(new BarEntry(0f, list.get(0).getValue()));
                    entries.add(new BarEntry(1f, list.get(1).getValue()));
                    break;
                case 3:
                    entries.add(new BarEntry(0f, list.get(0).getValue()));
                    entries.add(new BarEntry(1f, list.get(1).getValue()));
                    entries.add(new BarEntry(2f, list.get(2).getValue()));
                    break;
                case 4:
                    entries.add(new BarEntry(0f, list.get(0).getValue()));
                    entries.add(new BarEntry(1f, list.get(1).getValue()));
                    entries.add(new BarEntry(2f, list.get(2).getValue()));
                    entries.add(new BarEntry(3f, list.get(3).getValue()));
                    break;
            }
        }else {
            entries.add(new BarEntry(0f, list.get(0).getValue()));
            entries.add(new BarEntry(1f, list.get(1).getValue()));
            entries.add(new BarEntry(2f, list.get(2).getValue()));
            entries.add(new BarEntry(3f, list.get(3).getValue()));
            entries.add(new BarEntry(4f, list.get(4).getValue()));
        }

//        entries.add(new BarEntry(0f, list.get(0).getValue()));
//        entries.add(new BarEntry(1f, list.get(1).getValue()));
//        entries.add(new BarEntry(2f, list.get(2).getValue()));
//        entries.add(new BarEntry(3f, list.get(3).getValue()));
//        entries.add(new BarEntry(4f, list.get(4).getValue()));

        BarDataSet barDataSet = new BarDataSet(entries, "BarDataSet");
        barDataSet.setColor(Color.parseColor("#C9EAD4"));

        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);

        leBar.setDrawGridBackground(false);
        leBar.setData(barData);
        leBar.getDescription().setEnabled(false);
        leBar.getLegend().setEnabled(false);

        ArrayList<String> labels=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            labels.add(list.get(i).getKey());
        }
        Log.d("Debug", "Labels: "+labels.toString());
        leBar.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        leBar.getXAxis().setTypeface(getResources().getFont(R.font.pixeloidsans));
        leBar.getXAxis().setCenterAxisLabels(false);
        leBar.getXAxis().setDrawAxisLine(false);
        leBar.getXAxis().setDrawGridLines(false);
        leBar.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        leBar.getAxisLeft().setEnabled(false);
        leBar.getAxisRight().setEnabled(false);
        leBar.setFitBars(true); // make the x-axis fit exactly all bars
        leBar.invalidate(); // refresh
    }
}

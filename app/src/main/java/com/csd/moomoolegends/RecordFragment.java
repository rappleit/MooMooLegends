package com.csd.moomoolegends;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.List;

public class RecordFragment extends Fragment {
    String foodType;
    BarChart leBar;

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
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        entries.add(new BarEntry(4f, 50f));
        BarDataSet barDataSet = new BarDataSet(entries, "BarDataSet");
        barDataSet.setColor(Color.parseColor("#C9EAD4"));

        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);

        leBar.setDrawGridBackground(false);
        leBar.setData(barData);
        leBar.getDescription().setEnabled(false);
        leBar.getLegend().setEnabled(false);

        final String[] labels = new String[] {"Cows" , "Birds", "Cats", "Dogs", "Lamb"};
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

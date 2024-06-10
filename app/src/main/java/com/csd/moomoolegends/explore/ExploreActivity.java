package com.csd.moomoolegends.explore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.home.HomeActivity;

public class ExploreActivity extends AppCompatActivity {

    private final String[] dishes = { "Chicken Rice", "Chicken Rice", "Chicken Rice", "Chicken Rice", "Chicken Rice" };
    private final String[] stalls = { "Chicken & Co.", "Tian Tian Hai Nan Ji Fan", "Uncle Ming Ji's Chicken Rice", "Winner Winner Chicken Dinner", "Chix Rice Pte Ltd" };
    private final int[] distances = { 200, 350, 400, 500, 700 };
    private final double[] carbonFootprints = { 2.5, 2.5, 2.5, 2.5, 2.5 };
    private final int[] photoDrawables = { R.drawable.chicken_rice, R.drawable.chicken_rice, R.drawable.chicken_rice, R.drawable.chicken_rice, R.drawable.chicken_rice };

    // TODO replace with real data
    private final String location = "10 Bayfront Avenue, Singapore 018956";

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Handle recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ExploreCard adapter = new ExploreCard(dishes, stalls, distances, carbonFootprints, photoDrawables);
        recyclerView.setAdapter(adapter);

        // Handle text views
        ((TextView) findViewById(R.id.textViewLocation)).setText(getString(R.string.location_label) + "\n" + location);
        ((TextView) findViewById(R.id.textViewRefresh)).setOnClickListener(view -> {
            // TODO handle location refresh and re-population
            adapter.notifyDataSetChanged();
        });
        ((TextView) findViewById(R.id.textViewBack)).setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });
    }
}
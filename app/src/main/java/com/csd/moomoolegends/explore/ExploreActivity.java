package com.csd.moomoolegends.explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.home.HomeActivity;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class ExploreActivity extends AppCompatActivity {

    private final String[] dishes = { "Chicken Rice", "Chicken Rice", "Chicken Rice", "Chicken Rice", "Chicken Rice" };
    private final String[] stalls = { "Chicken & Co.", "Tian Tian Hai Nan Ji Fan", "Uncle Ming Ji's Chicken Rice", "Winner Winner Chicken Dinner", "Chix Rice Pte Ltd" };
    private final int[] distances = { 200, 350, 400, 500, 700 };
    private final double[] carbonFootprints = { 2.5, 2.5, 2.5, 2.5, 2.5 };
    private final int[] photoDrawables = { R.drawable.chicken_rice, R.drawable.chicken_rice, R.drawable.chicken_rice, R.drawable.chicken_rice, R.drawable.chicken_rice };

    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static LatLng currLocation;

    private static final int REQUEST_CODE = 101;
    private static final String LOG_TAG = "LOGCAT_ExploreActivity";

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

        // Prepare location manager for location updates
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_CODE
            );
        }
        else {
            configureLocationManager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            configureLocationManager();
        }
    }

    @SuppressLint("MissingPermission")
    private void configureLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = location ->
                currLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currLocation = new LatLng(
                Objects.requireNonNull(lastLocation).getLatitude(),
                Objects.requireNonNull(lastLocation).getLongitude()
        );
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    public LatLng getCurrLocation() {
        Log.d(LOG_TAG, currLocation.toString());
        return currLocation;
    }
}
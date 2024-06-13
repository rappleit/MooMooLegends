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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.home.HomeActivity;
import com.csd.moomoolegends.models.FirestoreInstance;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExploreActivity extends AppCompatActivity {

    private String[] dishes = { "Pad Thai", "Chicken Rice", "Curry Chicken", "Char Kway Teow", "Laksa" };
    private String[] stalls = { "Zesty! Asian Food", "Golden Mile Hainanese Boneless Chicken Rice", "Kamal's Restaurant", "Outram Park Fried Kway Teow Mee (#02-17)", "Sungei Road Laksa" };
    private int[] distances = { 600, 1200, 1200, 1400, 2600 };
    private LatLng[] locations = {
            new LatLng(1.283610, 103.852120),
            new LatLng(1.282840, 103.849300),
            new LatLng(1.279930, 103.847710),
            new LatLng(1.284580, 103.837670),
            new LatLng(1.289440, 103.849980)
    };
    private double[] carbonFootprints = { 1.75, 2.59, 2.98, 0.64, 1.52 };
    private int[] photoDrawables = { R.drawable.pad_thai, R.drawable.chicken_rice, R.drawable.curry_chicken, R.drawable.char_kway_teow, R.drawable.laksa };

    private final static HashMap<String, Double> allDishes = new HashMap<>();

    private static PlacesClient placesClient;
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static LatLng currLocation = new LatLng(1.282100, 103.858543);

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
        ExploreCard adapter = new ExploreCard(dishes, stalls, distances, carbonFootprints, photoDrawables, locations);
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

        // Query Firebase for all food options
        /*FirebaseFirestore db = FirestoreInstance.getInstance().getFirestore();
        db.collection("dishes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    allDishes.put(
                            document.get("dishName").toString(),
                            Double.parseDouble(document.get("carbonFootprint").toString())
                    );
                }
            }
            else {
                Log.d(LOG_TAG, "Error getting documents: ", task.getException());
            }
        });

        // Initialise Places API
        Places.initializeWithNewPlacesApiEnabled(
                getApplicationContext(),
                "AIzaSyBG0srjhGU17RtXjnS8SPrQZVM93IsD1wU"
        );
        placesClient = Places.createClient(this);

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
            getRecommendations();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO update current location

        // Refresh recommendations based on current location
        //getRecommendations();
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
        if (lastLocation != null) {
            currLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 0f,
                locationListener
        );
    }

    private void getRecommendations() {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG
        );

        HashMap<Place, String> allPlaces = new HashMap<>();
        for (String dish : allDishes.keySet()) {
            Log.d(LOG_TAG, "Processing " + dish);
            final SearchByTextRequest searchByTextRequest = SearchByTextRequest.builder(dish + " near me", placeFields)
                    .setMaxResultCount(2)
                    .setLocationRestriction(RectangularBounds.newInstance(
                            new LatLng(currLocation.latitude - 0.005, currLocation.longitude - 0.005),
                            new LatLng(currLocation.latitude + 0.005, currLocation.longitude + 0.005)
                    ))
                    .build();
            placesClient.searchByText(searchByTextRequest)
                    .addOnSuccessListener(response -> {
                        List<Place> places = response.getPlaces();
                        for (Place place : places) {
                            Log.d(LOG_TAG, dish + ": " + place.getAddress());
                            allPlaces.put(place, dish);
                        }
                    });
        }

        // Assume all queries finish here...
        for (Place place : allPlaces.keySet())
            Log.d(LOG_TAG, "allPlaces entry: " + place.getAddress() + ", " + allPlaces.get(place));
        /*Object[] results = allPlaces.entrySet().stream().sorted((e1, e2) -> Double.compare(
                getDistanceTo(e1.getKey().getLatLng()) * 0.5 + allDishes.get(e1.getValue()) * 0.5,
                getDistanceTo(e2.getKey().getLatLng()) * 0.5 + allDishes.get(e2.getValue()) * 0.5
        )).toArray();*/
        //Log.d(LOG_TAG, results.toString());
    }

    public LatLng getCurrLocation() {
        Log.d(LOG_TAG, currLocation.toString());
        return currLocation;
    }

    private float getDistanceTo(LatLng location) {
        float[] result = new float[1];
        Location.distanceBetween(
                location.latitude, location.longitude,
                currLocation.latitude, currLocation.longitude,
                result
        );
        return result[0];
    }
}
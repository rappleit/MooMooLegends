package com.csd.moomoolegends.explore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private LatLng currLocation;

    public final static String IMAGE_KEY = "imageKey";
    public final static String NAME_KEY = "nameKey";
    public final static String STALL_KEY = "stallKey";
    public final static String DISTANCE_KEY = "distanceKey";
    public final static String CARBON_KEY = "carbonKey";
    public final static String LOCATION_KEY = "locationKey";

    public final static String LOG_TAG = "LOGCAT_MapActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ((Button) findViewById(R.id.btnClose)).setOnClickListener(view -> {
            Intent intent = new Intent(this, ExploreActivity.class);
            startActivity(intent);
        });

        // Configure single card view
        Intent intent = getIntent();
        byte[] bytes = Objects.requireNonNull(intent.getExtras()).getByteArray(IMAGE_KEY);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, Objects.requireNonNull(bytes).length);
        currLocation = intent.getParcelableExtra(LOCATION_KEY);

        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
        ((TextView) findViewById(R.id.textViewName)).setText(intent.getStringExtra(NAME_KEY));
        ((TextView) findViewById(R.id.textViewStall)).setText(intent.getStringExtra(STALL_KEY));
        ((TextView) findViewById(R.id.textViewDistance)).setText(intent.getStringExtra(DISTANCE_KEY));
        ((TextView) findViewById(R.id.textViewCarbon)).setText(intent.getStringExtra(CARBON_KEY));
        ((ImageButton) findViewById(R.id.imageBtnMap)).setVisibility(View.GONE);

        TextView textViewDirections = (TextView) findViewById(R.id.textViewDirections);
        textViewDirections.setVisibility(View.VISIBLE);
        textViewDirections.setOnClickListener(view -> {
            // TODO implicit intent to google maps or otherwise
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Configure Google Map
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.clear();

        // Obtain and display current location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 15));
        googleMap.addCircle(new CircleOptions()
                .center(currLocation)
                .radius(50)
                .fillColor(Color.BLUE)
                .strokeWidth(0)
        );

        // TODO Display target location
        /*googleMap.addMarker(new MarkerOptions()
                .position(currLocation)
                .title("Current Location")
        );*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        Objects.requireNonNull(supportMapFragment).getMapAsync(this);
    }
}
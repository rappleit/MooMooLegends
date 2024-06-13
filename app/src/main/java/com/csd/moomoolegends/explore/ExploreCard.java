package com.csd.moomoolegends.explore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;

public class ExploreCard extends RecyclerView.Adapter<ExploreCard.ViewHolder> {

    private final String[] dishes;
    private final String[] stalls;
    private final int[] distances;
    private final double[] carbonFootprints;
    private final int[] photoDrawables;
    private final LatLng[] locations;

    private static final String LOG_TAG = "LOGCAT_ExploreCard";
    private static final int EXPLORE_COUNT = 5;

    public ExploreCard(
            @NonNull String[] dishes,
            @NonNull String[] stalls,
            @NonNull int[] distances,
            @NonNull double[] carbonFootprints,
            @NonNull int[] photoDrawables,
            @NonNull LatLng[] locations
    ) {
        this.dishes = dishes;
        this.stalls = stalls;
        this.distances = distances;
        this.carbonFootprints = carbonFootprints;
        this.photoDrawables = photoDrawables;
        this.locations = locations;
    }

    @NonNull
    @Override
    public ExploreCard.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_explore, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreCard.ViewHolder holder, int position) {
        if (position < 0 || position >= this.getItemCount()) {
            Log.e(LOG_TAG, "ViewHolder position specified as '" + position + "'");
            return;
        }
        holder.bind(
                this.dishes[position],
                this.stalls[position],
                this.distances[position],
                this.carbonFootprints[position],
                this.photoDrawables[position],
                this.locations[position]
        );
    }

    @Override
    public int getItemCount() {
        return EXPLORE_COUNT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context context;

        private final ImageView imageView;
        private final TextView textViewName;
        private final TextView textViewStall;
        private final TextView textViewDistance;
        private final TextView textViewCarbon;

        private int imageDrawable;
        private LatLng location;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();

            this.imageView = (ImageView) view.findViewById(R.id.imageView);
            this.textViewName = (TextView) view.findViewById(R.id.textViewName);
            this.textViewStall = (TextView) view.findViewById(R.id.textViewStall);
            this.textViewDistance = (TextView) view.findViewById(R.id.textViewDistance);
            this.textViewCarbon = (TextView) view.findViewById(R.id.textViewCarbon);

            ((ImageButton) view.findViewById(R.id.imageBtnMap)).setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bind(String dishName, String stall, int distance, double carbon, int imageDrawable, LatLng location) {
            this.imageDrawable = imageDrawable;
            this.location = location;

            imageView.setImageResource(imageDrawable);
            textViewName.setText(dishName);
            textViewStall.setText(stall);
            textViewDistance.setText(distance + "m away");
            textViewCarbon.setText(carbon + "kg CO2e");
        }

        @Override
        public void onClick(@NonNull View view) {
            if (view.getId() != R.id.imageBtnMap) {
                return;
            }

            // Compress image drawable into byte array
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imageDrawable);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes = baos.toByteArray();

            // Configure intent to MapActivity
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra(MapActivity.IMAGE_KEY, bytes);
            intent.putExtra(MapActivity.NAME_KEY, textViewName.getText().toString());
            intent.putExtra(MapActivity.STALL_KEY, textViewStall.getText().toString());
            intent.putExtra(MapActivity.DISTANCE_KEY, textViewDistance.getText().toString());
            intent.putExtra(MapActivity.CARBON_KEY, textViewCarbon.getText().toString());
            intent.putExtra(MapActivity.LOCATION_KEY, ((ExploreActivity) context).getCurrLocation());
            intent.putExtra(MapActivity.LOCATION_KEY_2, this.location);

            context.startActivity(intent);
        }
    }
}
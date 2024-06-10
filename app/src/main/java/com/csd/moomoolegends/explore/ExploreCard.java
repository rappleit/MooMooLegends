package com.csd.moomoolegends.explore;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.csd.moomoolegends.R;

public class ExploreCard extends RecyclerView.Adapter<ExploreCard.ViewHolder> {

    private final String[] dishes;
    private final String[] stalls;
    private final int[] distances;
    private final double[] carbonFootprints;
    private final int[] photoDrawables;

    private static final String LOG_TAG = "LOGCAT_ExploreCard";
    private static final int EXPLORE_COUNT = 5;

    public ExploreCard(
            @NonNull String[] dishes,
            @NonNull String[] stalls,
            @NonNull int[] distances,
            @NonNull double[] carbonFootprints,
            @NonNull int[] photoDrawables
    ) {
        this.dishes = dishes;
        this.stalls = stalls;
        this.distances = distances;
        this.carbonFootprints = carbonFootprints;
        this.photoDrawables = photoDrawables;
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
                this.photoDrawables[position]
        );
    }

    @Override
    public int getItemCount() {
        return EXPLORE_COUNT;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final ImageButton imageBtnMap;
        private final TextView textViewName;
        private final TextView textViewStall;
        private final TextView textViewDistance;
        private final TextView textViewCarbon;

        public ViewHolder(View view) {
            super(view);

            this.imageView = (ImageView) view.findViewById(R.id.imageView);
            this.imageBtnMap = (ImageButton) view.findViewById(R.id.imageBtnMap);
            this.textViewName = (TextView) view.findViewById(R.id.textViewName);
            this.textViewStall = (TextView) view.findViewById(R.id.textViewStall);
            this.textViewDistance = (TextView) view.findViewById(R.id.textViewDistance);
            this.textViewCarbon = (TextView) view.findViewById(R.id.textViewCarbon);
        }

        @SuppressLint("SetTextI18n")
        public void bind(String dishName, String stall, int distance, double carbon, int imageDrawable) {
            this.imageView.setImageResource(imageDrawable);
            this.textViewName.setText(dishName);
            this.textViewStall.setText(stall);
            this.textViewDistance.setText(distance + "m away");
            this.textViewCarbon.setText(carbon + "kg CO2e");

            // Handle map click
            imageBtnMap.setOnClickListener(view -> {
                // TODO load google map dialog box
            });
        }
    }
}
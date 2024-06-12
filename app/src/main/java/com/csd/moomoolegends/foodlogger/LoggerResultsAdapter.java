package com.csd.moomoolegends.foodlogger;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csd.moomoolegends.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.Locale;

public class LoggerResultsAdapter extends RecyclerView.Adapter<LoggerResultsAdapter.LoggerResultsViewHolder> {
    private Context context;
    private ArrayList<LoggedIngredient> ingredientBreakdownList;
    private Double totalCarbon;

    public LoggerResultsAdapter(Context context, ArrayList<LoggedIngredient> ingredientBreakdownList, Double totalCarbon) {
        this.context = context;
        this.ingredientBreakdownList = ingredientBreakdownList;
        this.totalCarbon = totalCarbon;

    }
    @NonNull
    @Override
    public LoggerResultsAdapter.LoggerResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loggerresults_breakdown, parent, false);
        return new LoggerResultsAdapter.LoggerResultsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LoggerResultsAdapter.LoggerResultsViewHolder holder, int position) {
        LoggedIngredient ingredient = ingredientBreakdownList.get(position);
        holder.IngredientNameTextView.setText(ingredient.getDisplayName() + ": " + String.format(Locale.getDefault(), "%.2f", ingredient.getFootprint()) + " kg CO2");
        int progress = (int) (ingredient.getFootprint() * 100);
        int maxProgress = (int) (this.totalCarbon * 100);
        Log.d("Indicator", this.totalCarbon.toString());
        holder.ingredientIndicator.setMax(maxProgress);
        holder.ingredientIndicator.setProgress(progress, true);
    }

    @Override
    public int getItemCount() {
        return ingredientBreakdownList.size();
    }

    public static class LoggerResultsViewHolder extends RecyclerView.ViewHolder{
        private TextView IngredientNameTextView;
        private LinearProgressIndicator ingredientIndicator;
        public LoggerResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            IngredientNameTextView = itemView.findViewById(R.id.breakdown_ingredientname);
            ingredientIndicator = itemView.findViewById(R.id.ingredientindicator);
        }
    }
}


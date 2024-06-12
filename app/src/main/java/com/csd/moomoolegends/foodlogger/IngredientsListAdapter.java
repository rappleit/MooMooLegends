package com.csd.moomoolegends.foodlogger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csd.moomoolegends.R;

import java.util.ArrayList;

public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListAdapter.IngredientsListViewHolder> {
    private Context context;
    private ArrayList<LoggedIngredient> ingredientItemsList;



    public IngredientsListAdapter(Context context, ArrayList<LoggedIngredient> ingredientItemsList) {
        this.context = context;
        this.ingredientItemsList = ingredientItemsList;

    }
    @NonNull
    @Override
    public IngredientsListAdapter.IngredientsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ingredientlist_ingredient, parent, false);
        return new IngredientsListAdapter.IngredientsListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsListAdapter.IngredientsListViewHolder holder, int position) {
        LoggedIngredient ingredient = ingredientItemsList.get(position);
        holder.IngredientNameTextView.setText(ingredient.getDisplayName());
        holder.IngredientQtyTextView.setText(String.valueOf(ingredient.getQuantity()) + "g");
        holder.RemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredientItemsList.size();
    }

    private void removeItem(int position) {
        ingredientItemsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ingredientItemsList.size());
    }

    public static class IngredientsListViewHolder extends RecyclerView.ViewHolder{
        private TextView IngredientNameTextView;
        private TextView IngredientQtyTextView;

        private Button RemoveButton;
        public IngredientsListViewHolder(@NonNull View itemView) {
            super(itemView);
            IngredientNameTextView = itemView.findViewById(R.id.ingredientListItem_name);
            IngredientQtyTextView = itemView.findViewById(R.id.ingredientListItem_qty);
            RemoveButton = itemView.findViewById(R.id.removeButton);
        }
    }
}

package com.csd.moomoolegends.foodlogger;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.csd.moomoolegends.R;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class IngredientsListActivity extends AppCompatActivity {

    ImageView foodImageView;
    RecyclerView ingredientsListRecycler;
    ArrayList<LoggedIngredient> ingredientsList;
    private IngredientsListAdapter adapter;
    private Button confirmIngredientsBtn;
    private Button addIngredientsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients_list);
        processIngredients();
        foodImageView = findViewById(R.id.foodImageView);
        confirmIngredientsBtn = findViewById(R.id.confirmIngredients_button);
        addIngredientsBtn = findViewById(R.id.addIngredientsButton);
        addIngredientsBtn.setOnClickListener(v -> showAddIngredientDialog());

        confirmIngredientsBtn.setOnClickListener(v -> {
            ArrayList<String> tuplesList = new ArrayList<>();
            for (LoggedIngredient ingredient : ingredientsList) {
                tuplesList.add(ingredient.getDisplayName() + "," + ingredient.getQuantity());
            }
            Intent intent = new Intent(this, CarbonFootprintCalculatorActivity.class);
            intent.putStringArrayListExtra("ingredientTuples", tuplesList);
            startActivity(intent);
            finish();
        });

        ingredientsListRecycler = findViewById(R.id.ingredientList_recyclerView);
        adapter = new IngredientsListAdapter(this, ingredientsList);
        ingredientsListRecycler.setAdapter(adapter);
        ingredientsListRecycler.setLayoutManager(new LinearLayoutManager(this));

        String photoPath = getIntent().getStringExtra("photo_path");

        if (photoPath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            foodImageView.setImageBitmap(rotatedBitmap);
            foodImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ViewGroup.LayoutParams params = foodImageView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            foodImageView.setLayoutParams(params);

        }
    }



    public void processIngredients() {
        Intent intent = getIntent();
        String responseData = intent.getStringExtra("response_data");
        ingredientsList = new ArrayList<>();

        if (responseData != null) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
            JsonArray itemsArray = jsonObject.getAsJsonArray("items");

            for (JsonElement itemElement : itemsArray) {
                JsonObject itemObject = itemElement.getAsJsonObject();
                JsonArray foodArray = itemObject.getAsJsonArray("food");

                for (JsonElement foodElement : foodArray) {
                    JsonObject foodObject = foodElement.getAsJsonObject();
                    String displayName = foodObject.getAsJsonObject("food_info").get("display_name").getAsString();
                    double quantity = foodObject.get("quantity").getAsDouble();

                    LoggedIngredient ingredient = new LoggedIngredient(displayName, quantity, 0);
                    ingredientsList.add(ingredient);
                }
            }
        }


    }


    private void showAddIngredientDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_ingredient, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        EditText editTextIngredientName = dialogView.findViewById(R.id.editTextIngredientName);
        Spinner spinnerServings = dialogView.findViewById(R.id.spinnerServings);

        // Populate the spinner with numbers
        Integer[] servingsOptions = new Integer[]{1, 2, 3, 4, 5}; // Example servings
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, servingsOptions);
        spinnerServings.setAdapter(adapter);

        dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            String ingredientName = editTextIngredientName.getText().toString();
            int servings = (int) spinnerServings.getSelectedItem();
            double quantity = servings * 150.0;

            LoggedIngredient newIngredient = new LoggedIngredient(ingredientName, quantity, 0.0);
            ingredientsList.add(newIngredient);
            adapter.notifyDataSetChanged();
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
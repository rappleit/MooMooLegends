package com.csd.moomoolegends.foodlogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
}
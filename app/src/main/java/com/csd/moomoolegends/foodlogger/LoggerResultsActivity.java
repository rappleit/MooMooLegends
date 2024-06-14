package com.csd.moomoolegends.foodlogger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.home.HomeActivity;
import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.User;
import com.csd.moomoolegends.models.WeeklyRecords;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class LoggerResultsActivity extends AppCompatActivity {
    private Double totalCarbon = 0.0;
    private TextView totalCarbonTextView;
    private ArrayList<LoggedIngredient> ingredientsList = new ArrayList<>();
    private LoggerResultsAdapter adapter;

    private RecyclerView breakdownRecycler;

    private Button confirmLogButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger_results);

        totalCarbonTextView = findViewById(R.id.totalCarbonFootprint);
        breakdownRecycler = findViewById(R.id.loggerBreakdownRecyclerView);
        confirmLogButton = findViewById(R.id.confirmLogButton);
        Intent intent = getIntent();
        String carbonData = intent.getStringExtra("carbonData");

        confirmLogButton.setOnClickListener(v -> {

            // TODO: Connect to backend after log is confirmed
            for (LoggedIngredient ingredient : ingredientsList) {
                Log.d("LoggerResultsActivity", "Ingredient: " + ingredient.getDisplayName() + ", Quantity: " + ingredient.getQuantity() + ", Footprint: " + ingredient.getFootprint());
                WeeklyRecords.addRecord(ingredient.getCategory(), ingredient.getDisplayName(), (float) ingredient.getFootprint());
            }

            WeeklyRecords.updateFirestore(new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if (success) {
                        Log.d("LoggerResultsActivity", "Successfully updated Firestore");
                        User.setCurrentCarbonFootprint(User.getCurrentCarbonFootprint() + totalCarbon.floatValue());
                    } else {
                        Log.e("LoggerResultsActivity", "Failed to update Firestore: " + message);
                    }

                    int newCoins = User.getCoins() + 10;
                    User.setCoins(newCoins);
                }
            });

            Intent intent2 = new Intent(LoggerResultsActivity.this, HomeActivity.class);
            startActivity(intent2);
        });

        if (carbonData != null && !carbonData.isEmpty()) {
            try {
                JsonObject jsonObject = JsonParser.parseString(carbonData).getAsJsonObject();
                totalCarbon = jsonObject.get("total").getAsDouble();
                JsonArray breakdown = jsonObject.getAsJsonArray("breakdown");

                for (JsonElement element : breakdown) {
                    JsonObject ingredientJson = element.getAsJsonObject();
                    String name = ingredientJson.get("ingredientName").getAsString();
                    double footprint = ingredientJson.get("footprint").getAsDouble();
                    String category = ingredientJson.get("category").getAsString();

                    LoggedIngredient ingredient = new LoggedIngredient(name, 0.0, footprint); // Quantity is set to 0.0 by default here
                    ingredient.setCategory(category);
                    ingredientsList.add(ingredient);
                }

                adapter.notifyDataSetChanged();
                Log.d("LoggerResultsActivity", "Total Carbon Footprint: " + totalCarbon);
            } catch (Exception e) {
                Log.e("LoggerResultsActivity", "Failed to parse carbon data or extract 'total': " + e.getMessage());
            }
        } else {
            Log.e("LoggerResultsActivity", "No carbon data received");
        }

        totalCarbonTextView.setText(String.format(Locale.getDefault(), "%.2f", totalCarbon) + " kg CO2");
        Collections.sort(ingredientsList, (ingredient1, ingredient2) -> Double.compare(ingredient2.getFootprint(), ingredient1.getFootprint()));
        adapter = new LoggerResultsAdapter(this, ingredientsList, totalCarbon);
        breakdownRecycler.setAdapter(adapter);
        breakdownRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

}
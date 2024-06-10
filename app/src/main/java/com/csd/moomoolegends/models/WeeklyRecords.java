package com.csd.moomoolegends.models;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeeklyRecords extends FirestoreInstance{
    private static Map<String, Object> dairy;
    private static Map<String, Object> meat;
    private static Map<String, Object> carbs;
    private static Map<String, Object> veg;
    private static Map<String, Object> seafood;
    private static String[] categories = {"dairy", "meat", "carbs", "veg", "seafood"};
    private static LocalDate date;

    public WeeklyRecords(Map<String, Object> dairy, Map<String, Object> meat, Map<String, Object> carbs, Map<String, Object> veg, Map<String, Object> seafood) {
        WeeklyRecords.dairy = dairy;
        WeeklyRecords.meat = meat;
        WeeklyRecords.carbs = carbs;
        WeeklyRecords.veg = veg;
        WeeklyRecords.seafood = seafood;
    }

    public static void getWeeklyRecords(DocumentReference userDoc, OnFirestoreCompleteCallback callback){
        Log.d("WeeklyRecords", "Getting weekly records");
        // Get weekly records from Firestore
        String weekYear = WeeklyRecords.getWeekYearNumber();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));
        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(weekFields.getFirstDayOfWeek().plus(7)));
        Date endDate = Date.from(endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Query query = userDoc.collection("recordsCollection").whereEqualTo("weekYear", weekYear);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()){
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("startDate", startDate);
                    newData.put("endDate", endDate);
                    newData.put("totalCarbonFootprint", 0);

                    userDoc.collection("recordsCollection").document(weekYear).set(newData);
                    callback.onFirestoreComplete(true, "New weekly record created");;
                } else {
                    retrieveData(task.getResult().getDocuments().get(0), callback);
                }
            } else {
                callback.onFirestoreComplete(false, "Failed to get weekly records");
            }
        });

    }

    public static void retrieveData(DocumentSnapshot document, OnFirestoreCompleteCallback callback){
        Map<String, Map<String, Object>> categoryData = new HashMap<>();
        Date endDate = document.getDate("endDate");
        Date startDate = document.getDate("startDate");
        Number totalCarbonFootprint = document.getDouble("totalCarbonFootprint");

        for (String category : categories) {
            Map<String, Object> data = (Map<String, Object>) document.get(category);
            Map<String, Number> foodItems = new HashMap<>();
            Number categoryCarbonFootprint = 0;

            if (data != null){
                // Iterate over the data for each category
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (entry.getValue() instanceof Map && entry.getValue() != null) {
                        foodItems = (Map<String, Number>) entry.getValue();
                    } else if (entry.getKey().equals("categoryCarbonFootprint")) {
                        categoryCarbonFootprint = (Number) entry.getValue();
                    }
                }
            }

            // Store the food items and total carbon footprint in the category data
            data.put("foodItems", foodItems);
            data.put("totalCarbonFootprint", categoryCarbonFootprint);
            categoryData.put(category, data);
            callback.onFirestoreComplete(true, "Weekly records retrieved");
        }

        Log.d("WeeklyRecords", "Data retrieved");
        Log.d("WeeklyRecords", "End Date: " + endDate);
        Log.d("WeeklyRecords", "Start Date: " + startDate);
        Log.d("WeeklyRecords", "Total Carbon Footprint: " + totalCarbonFootprint);
        Log.d("WeeklyRecords", "Category Data: " + categoryData);
    }

    public static String getWeekYearNumber(){
        date = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = date.getYear();
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        return weekNumber + String.valueOf(year).substring(2);
    }
}

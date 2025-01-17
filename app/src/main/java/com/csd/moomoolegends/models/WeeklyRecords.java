package com.csd.moomoolegends.models;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import kotlin.jvm.internal.markers.KMutableMap;

public class WeeklyRecords extends FirestoreInstance{
    private static Map<String, Map<String, Float>> categoryData  = new HashMap<>();
    private static String[] categories = {"Dairy", "Meat", "Carbs", "Veg", "Seafood"};
    private static LocalDate date;
    private static Date endDate;
    private static Date startDate;
    private static float totalCarbonFootprint;

    public WeeklyRecords() {}

    public static void getWeeklyRecords(DocumentReference userDoc, OnFirestoreCompleteCallback callback){
        Log.d("WeeklyRecords", "Getting weekly records");
        // Get weekly records from Firestore
        String weekYear = WeeklyRecords.getWeekYearNumber();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(weekFields.getFirstDayOfWeek()));
        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(weekFields.getFirstDayOfWeek().plus(7)));
        Date endDate = Date.from(endOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Log.d("Debug", "Got dates: " + startDate.toString() + endDate.toString());

        if (startDate == null || endDate == null){
            callback.onFirestoreComplete(false, "Failed to get start and end date");
            return;
        }

        userDoc.collection("recordsCollection").document(weekYear).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()){
                    Log.d("WeeklyRecords", "Weekly records exist");
                    retrieveData(task.getResult(), callback);
                } else {
                    Log.d("WeeklyRecords", "Weekly records does not exist");
                    Map<String, Object> newData = new HashMap<>();
                    newData.put("startDate", startDate);
                    newData.put("endDate", endDate);
                    newData.put("totalCarbonFootprint", 0);
                    for (String category : categories){
                        Map<String, Float> newCategoryData = new HashMap<>();
                        newCategoryData.put("categoryCarbonFootprint", (float) 0);
                        newData.put(category, newCategoryData);
                        categoryData.put(category, newCategoryData);
                    }

                    userDoc.collection("recordsCollection").document(weekYear).set(newData);
                    callback.onFirestoreComplete(true, "New weekly record created");
                }
            } else {
                Log.d("Debug", "Failed to get weekly records");
                callback.onFirestoreComplete(false, "Failed to get weekly records");
            }
        });

    }

    public static void retrieveData(DocumentSnapshot document, OnFirestoreCompleteCallback callback){
        endDate = document.getDate("endDate");
        startDate = document.getDate("startDate");
        totalCarbonFootprint = document.getDouble("totalCarbonFootprint").floatValue();

        for (String currentCategory : categories) {
            Map<String, Number> category = (Map<String, Number>) document.get(currentCategory);
            Map<String, Float> floatCategory = new HashMap<>();
            if (category != null && !category.isEmpty()){

                for (Map.Entry<String, Number> value:category.entrySet()){
                    float newData = value.getValue().floatValue();
                    floatCategory.put(value.getKey(), newData);
                }

                categoryData.put(currentCategory, floatCategory);
            } else {
                categoryData.put(currentCategory, new HashMap<>());
            }

        }

        Log.d("WeeklyRecords", "Data retrieved");
        Log.d("WeeklyRecords", "End Date: " + endDate);
        Log.d("WeeklyRecords", "Start Date: " + startDate);
        Log.d("WeeklyRecords", "Total Carbon Footprint: " + totalCarbonFootprint);
        Log.d("WeeklyRecords", "Category Data: " + categoryData);
        callback.onFirestoreComplete(true, "Weekly records initialized successfully");
    }

    public static String getWeekYearNumber(){
        date = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = date.getYear();
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        return weekNumber + String.valueOf(year).substring(2);
    }

    public static float getTotalCarbonFootprint() {
        return totalCarbonFootprint;
    }

    public static Map<String, Float> getCarbs() {
        return categoryData.get("Carbs");
    }

    public static Map<String, Float> getDairy() {
        return categoryData.get("Dairy");
    }

    public static Map<String, Float> getMeat() {
        return categoryData.get("Meat");
    }

    public static Map<String, Float> getSeafood() {
        return categoryData.get("Seafood");
    }

    public static Map<String, Float> getVeg() {
        return categoryData.get("Veg");
    }

    public static void addRecord(String category, String ingredientName, float carbonFootprint){
        Log.d("WeeklyRecords", categoryData.toString());
        if (categoryData.get(category).get(ingredientName) != null){
            Number currentCarbonFootprint = categoryData.get(category).get(ingredientName);
            float currentFloatCarbonFootprint = currentCarbonFootprint.floatValue() + carbonFootprint;
            categoryData.get(category).put(ingredientName, currentFloatCarbonFootprint);
        } else {
            Map<String, Number> newIngredient = new HashMap<>();
            newIngredient.put(ingredientName, carbonFootprint);
            categoryData.get(category).put(ingredientName, carbonFootprint);
        }

        totalCarbonFootprint += carbonFootprint;

        Number currentCategoryCarbonFootprint = categoryData.get(category).get("categoryCarbonFootprint");
        float currentFloatCategoryCarbonFootprint = currentCategoryCarbonFootprint.floatValue() + carbonFootprint;
        categoryData.get(category).put("categoryCarbonFootprint", currentFloatCategoryCarbonFootprint);
    }

    public static void updateTotalCarbonFootprint(float carbonFootprint){
        totalCarbonFootprint += carbonFootprint;
    }

    public static void updateFirestore(OnFirestoreCompleteCallback callback){
        DocumentReference userDoc = User.getUserDoc();
        Map<String, Object> newData = new HashMap<>();
        newData.put("endDate", endDate);
        newData.put("startDate", startDate);
        newData.put("totalCarbonFootprint", totalCarbonFootprint);

        for (String category : categories){
            newData.put(category, categoryData.get(category));
        }

        userDoc.collection("recordsCollection").document(getWeekYearNumber()).set(newData);
        callback.onFirestoreComplete(true, "Weekly records updated");
    }

    public static String[] getCategories(){
        return categories;
    }

    public static  Map<String, Map<String, Float>> getCategoryData(){
        return categoryData;
    }

}


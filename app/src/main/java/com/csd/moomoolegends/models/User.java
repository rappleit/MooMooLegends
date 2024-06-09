package com.csd.moomoolegends.models;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

public class User {
    private static String username;
    private static String email;
    private static String userId;
    private static int rollsLeft;
    private static int coins;
    private static String roomCode;
    private static float weeklyThreshold;
    private static float currentCarbonFootprint;
    private static ArrayList<Map<String, Object>> userCows;
    private static DocumentReference userDoc;

    public User(Map<String, Object> user, DocumentReference userDoc, OnFirestoreCompleteCallback callback) {
        try {
            User.username = (String) user.get("username");
            User.email = (String) user.get("email");
            User.userId = (String) user.get("uid");
            User.rollsLeft = ((Number) user.get("rollsLeft")).intValue();
            User.coins = ((Number) user.get("coins")).intValue();
            User.roomCode = (String) user.get("roomCode");
            User. weeklyThreshold = ((Number) user.get("weeklyThreshold")).floatValue();
            User.currentCarbonFootprint = ((Number) user.get("currentCarbonFootprint")).floatValue();
            Object userCowsObj = user.get("userCows");
            if (userCowsObj == null) {
                User.userCows = new ArrayList<>();
            } else {
                User.userCows = (ArrayList<Map<String, Object>>) userCowsObj;
            }
            User.userDoc = userDoc;

            callback.onFirestoreComplete(true, "User successfully created");

        } catch (NullPointerException e) {
            callback.onFirestoreComplete(false, "Failed to create user " + e.getMessage());
        }
    }

    public User() {}

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

    public static String getUserId() {
        return userId;
    }

    public static int getRollsLeft() {
        return rollsLeft;
    }

    public static int getCoins() {
        return coins;
    }

    public static String getRoomCode() {
        return roomCode;
    }

    public static float getWeeklyThreshold() {
        return weeklyThreshold;
    }

    public static float getCurrentCarbonFootprint() {
        return currentCarbonFootprint;
    }

    public static ArrayList<Map<String, Object>> getUserCows() {
        return userCows;
    }

    public static DocumentReference getUserDoc() {
        return userDoc;
    }

    public static void setRollsLeft(int rollsLeft) {
        User.rollsLeft = rollsLeft;
    }

    public static void setCoins(int coins) {
        User.coins = coins;
    }

    public static void setRoomCode(String roomCode) {
        User.roomCode = roomCode;
    }

    public static void setWeeklyThreshold(float weeklyThreshold) {
        User.weeklyThreshold = weeklyThreshold;
    }

    public static void setCurrentCarbonFootprint(float currentCarbonFootprint) {
        User.currentCarbonFootprint = currentCarbonFootprint;
    }

    public static void setUserCows(ArrayList<Map<String, Object>> userCows) {
        User.userCows = userCows;
    }
}

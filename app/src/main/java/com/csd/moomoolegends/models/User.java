package com.csd.moomoolegends.models;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class User extends UserFirestore{

    //User info
    private static String username;
    private static String email;
    private static String userId;

    //Shop user info
    private static int rollsLeft;
    private static int coins;

    //Room info
    private static String roomCode;
    private static Room room;
    private static boolean inRoom;

    //Carbon tracking info
    private static float weeklyThreshold;
    private static float currentCarbonFootprint;

    //Cows info
    private static ArrayList<Cow> userCows;

    //User document reference
    private static DocumentReference userDoc;

    public User(Map<String, Object> user, DocumentReference userDoc, OnFirestoreCompleteCallback callback) {
        try {
            CountDownLatch latch = new CountDownLatch(3);
            ExecutorService executor = Executors.newFixedThreadPool(3);
            User.username = (String) user.get("username");
            User.email = (String) user.get("email");
            User.userId = (String) user.get("uid");
            User.rollsLeft = ((Number) user.get("rollsLeft")).intValue();
            User.coins = ((Number) user.get("coins")).intValue();
            User.roomCode = (String) user.get("roomCode");
            User.inRoom = !Objects.equals(User.roomCode, "");

            User.weeklyThreshold = ((Number) user.get("weeklyThreshold")).floatValue();
            User.currentCarbonFootprint = ((Number) user.get("currentCarbonFootprint")).floatValue();
            Object userCowsObj = user.get("userCows");

            if (userCowsObj == null) {
                User.userCows = new ArrayList<>();
            } else {
                User.userCows = (ArrayList<Cow>) userCowsObj;
            }
            User.userDoc = userDoc;

            executor.execute(() -> {
                if (User.inRoom){
                    RoomFirestore.getInstance().initializeRoomListener((String) user.get("roomCode"), new OnFirestoreCompleteCallback() {
                        @Override
                        public void onFirestoreComplete(boolean success, String message) {
                            if (success){
                                Log.d("Debug", message);
                                latch.countDown();
                            }else{
                                callback.onFirestoreComplete(false, message);
                            }
                        }
                    });
                } else {
                    Log.d("Debug", "User is not in a room");
                    latch.countDown();
                }
            });

            executor.execute(() -> {
                WeeklyRecords.getWeeklyRecords(userDoc, new OnFirestoreCompleteCallback() {
                    @Override
                    public void onFirestoreComplete(boolean success, String message) {
                        if (success){
                            Log.d("Debug", message);
                            latch.countDown();
                        }else{
                            callback.onFirestoreComplete(false, message);
                        }
                    }
                });
            });

            executor.execute(() -> {
                Shop.getInstance().initializeShop(new OnFirestoreCompleteCallback() {
                    @Override
                    public void onFirestoreComplete(boolean success, String message) {
                        if (success){
                            Log.d("Debug", message);
                            Log.d("Debug", Shop.oneRoll.toString());
                            Log.d("Debug", Shop.twoRolls.toString());
                            Log.d("Debug", Shop.threeRolls.toString());
                            latch.countDown();
                        }else{
                            callback.onFirestoreComplete(false, message);
                        }
                    }
                });
            });

            executor.execute(() -> {
                try {
                    Log.d("Firestore", "User object waiting");
                    latch.await();
                    Log.d("Firestore", "User object initialized");
                    callback.onFirestoreComplete(true, "User object initialized");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                    callback.onFirestoreComplete(false, "Failed to create user " + e.getMessage());
                }
            });

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

    public static boolean getInRoom() {
        return inRoom;
    }

    public static float getWeeklyThreshold() {
        return weeklyThreshold;
    }

    public static float getCurrentCarbonFootprint() {
        return currentCarbonFootprint;
    }

    public static ArrayList<Cow> getUserCows() {
        return userCows;
    }

    public static DocumentReference getUserDoc() {
        return userDoc;
    }

    public static Room getRoom() {
        return room;
    }

    public static void setRoom(Room room) {
        User.room = room;
    }

    public static void setRoomCode(String roomCode) {
        User.roomCode = roomCode;
    }

    public static void setInRoom(boolean inRoom) {
        User.inRoom = inRoom;
    }

    public static void setRollsLeft(int rollsLeft) {
        User.rollsLeft = rollsLeft;
        UserFirestore.getInstance().editRollsLeft(userId, rollsLeft, new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success) {
                    Log.d("Debug", message);
                } else {
                    Log.d("Debug", message);
                }
            }
        });
    }

    public static void setCoins(int coins) {
        User.coins = coins;
        UserFirestore.getInstance().editCoins(userId, coins, new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success) {
                    Log.d("Debug", message);
                } else {
                    Log.d("Debug", message);
                }
            }
        });
    }


    public static void setWeeklyThreshold(float weeklyThreshold) {
        User.weeklyThreshold = weeklyThreshold;
        UserFirestore.getInstance().editWeeklyThreshold(userId, weeklyThreshold, new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success) {
                    Log.d("Debug", message);
                } else {
                    Log.d("Debug", message);
                }
            }
        });
    }

    public static void setCurrentCarbonFootprint(float currentCarbonFootprint) {
        User.currentCarbonFootprint = currentCarbonFootprint;
        UserFirestore.getInstance().editCurrentCarbonFootprint(userId, currentCarbonFootprint, new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success) {
                    Log.d("Debug", message);
                } else {
                    Log.d("Debug", message);
                }
            }
        });
    }

    public static void addCow(Cow cow, OnFirestoreCompleteCallback callback){
        userCows.add(cow);
        editFirestoreCow(callback);
    }

    public static void removeCow(Cow cow, OnFirestoreCompleteCallback callback){
        userCows.remove(cow);
        editFirestoreCow(callback);
    }

    public static void removeRandomCow(OnCowRemoval callback){
        Random rand = new Random();
        int index = rand.nextInt(userCows.size());
        Cow removedCow = userCows.remove(index);
        editFirestoreCow(new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success){
                    callback.onCowRemoved(true, removedCow);
                }else{
                    callback.onCowRemoved(false, null);
                }
            }
        });
    }

    public static void editFirestoreCow(OnFirestoreCompleteCallback callback){
        UserFirestore.getInstance().editUserCows(userId, userCows, callback);
    }
}

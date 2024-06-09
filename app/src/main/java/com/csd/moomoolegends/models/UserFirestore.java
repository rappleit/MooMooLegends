package com.csd.moomoolegends.models;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class UserFirestore extends FirestoreInstance{
    private static UserFirestore instance;
    private FirebaseFirestore db = getFirestore();

    protected UserFirestore() {
        super();
    }

    public static UserFirestore getInstance() {
        if (instance == null) {
            instance = new UserFirestore();
        }
        return instance;
    }

    protected void editCoins(String uid, int coins, OnFirestoreCompleteCallback callback){
        db.collection("users").document(uid).update("coins", coins).addOnSuccessListener(aVoid -> {
            callback.onFirestoreComplete(true, "Coins updated successfully");
        }).addOnFailureListener(e -> {
            callback.onFirestoreComplete(false, "Failed to update coins");
        });
    }

    protected void editRollsLeft(String uid, int rollsLeft, OnFirestoreCompleteCallback callback){
        db.collection("users").document(uid).update("rollsLeft", rollsLeft).addOnSuccessListener(aVoid -> {
            callback.onFirestoreComplete(true, "Rolls left updated successfully");
        }).addOnFailureListener(e -> {
            callback.onFirestoreComplete(false, "Failed to update rolls left");
        });
    }

    protected void editRoomCode(String uid, String roomCode, OnFirestoreCompleteCallback callback){
        db.collection("users").document(uid).update("roomCode", roomCode).addOnSuccessListener(aVoid -> {
            callback.onFirestoreComplete(true, "Room code updated successfully");
        }).addOnFailureListener(e -> {
            callback.onFirestoreComplete(false, "Failed to update room code");
        });
    }

    protected void editWeeklyThreshold(String uid, float weeklyThreshold, OnFirestoreCompleteCallback callback){
        db.collection("users").document(uid).update("weeklyThreshold", weeklyThreshold).addOnSuccessListener(aVoid -> {
            callback.onFirestoreComplete(true, "Weekly threshold updated successfully");
        }).addOnFailureListener(e -> {
            callback.onFirestoreComplete(false, "Failed to update weekly threshold");
        });
    }

    protected void editCurrentCarbonFootprint(String uid, float currentCarbonFootprint, OnFirestoreCompleteCallback callback){
        db.collection("users").document(uid).update("currentCarbonFootprint", currentCarbonFootprint).addOnSuccessListener(aVoid -> {
            callback.onFirestoreComplete(true, "Current carbon footprint updated successfully");
        }).addOnFailureListener(e -> {
            callback.onFirestoreComplete(false, "Failed to update current carbon footprint");
        });
    }

    protected void editUserCows(String uid, ArrayList<Map<String, Object>> userCows, OnFirestoreCompleteCallback callback){
        db.collection("users").document(uid).update("userCows", userCows).addOnSuccessListener(aVoid -> {
            callback.onFirestoreComplete(true, "User cows updated successfully");
        }).addOnFailureListener(e -> {
            callback.onFirestoreComplete(false, "Failed to update user cows");
        });
    }


}

package com.csd.moomoolegends.models;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class SignUpLoginFirestore extends FirestoreInstance{
    private final FirebaseFirestore db = getFirestore();
    private static SignUpLoginFirestore instance;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SignUpLoginFirestore() {
    }

    public static SignUpLoginFirestore getInstance(){
        if (instance == null){
            instance = new SignUpLoginFirestore();
        }
        return instance;
    }

    public void signUp(Activity activity, String username, String email, String password, OnFirestoreCompleteCallback callback){
        checkUsername(username, new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                Log.d("SignUpFirestore", "username check complete");
                if (success){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(activity, task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Debug", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        Log.d("Debug", "User: id" + user.getUid() + " email: " + user.getEmail());
                                        String uid = user.getUid();
                                        signUpUser(username, email, password, uid, callback);
                                    } else {
                                        Log.w("Debug", "null, createUserWithEmail:failure", task.getException());
                                        //End loading screen
                                        callback.onFirestoreComplete(false, "Failed to register user, please try again.");
                                    }
                                } else {
                                    Log.w("Debug", "createUserWithEmail:failure", task.getException());
                                    //End loading screen
                                    callback.onFirestoreComplete(false, "Failed to register user, please try again.");
                                }
                            });
                }else{
                    callback.onFirestoreComplete(false, "Username taken, please choose another one.");
                }
            }
        });
    }

    public synchronized void signUpUser(String username, String email, String password, String uid, OnFirestoreCompleteCallback callback){
        Log.d("SignUpFirestore", "creating user");
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("password", password);
        user.put("uid", uid);
        user.put("rollsLeft", 0);
        user.put("coins", 0);
        user.put("roomCode", "");
        user.put("userCows", new ArrayList<>());
        user.put("weeklyThreshold", 0.0);
        user.put("currentCarbonFootprint", 0.0);
        Log.d("SignUpFirestore", "user created");

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("SignUpFirestore", "successfully added user");
                    db.collection("users").document(uid).collection("recordsCollection").document(getWeekYearNumber()).set(new HashMap<>())
                            .addOnSuccessListener(documentReference -> {
                                Log.d("SignUpFirestore", "successfully added user and initialized recordsCollection");
                                callback.onFirestoreComplete(true, "User registered successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.d("SignUpFirestore", "Failed to initialize recordsCollection");
                            });
                    UserDataFirestore.getInstance().initializeUser(user);
                })
                .addOnFailureListener(e -> callback.onFirestoreComplete(false, "Failed to register user, please try again."));
    }

    public synchronized void checkUsername(String username, OnFirestoreCompleteCallback callback){

        Query check = db.collection("users").whereEqualTo("username", username);
        check.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (task.getResult().isEmpty()){
                    // Username is available
                    Log.d("SignUpFirestore", "username is available");
                    callback.onFirestoreComplete(true, "username check complete");
                }else{
                    // Username is not available
                    Log.d("SignUpFirestore", "username is not available");
                    callback.onFirestoreComplete(false, "username check complete");
                }
            }
        });
    }

    public synchronized void login(Activity activity, String email, String password, OnFirestoreCompleteCallback callback){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Debug", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            Log.d("Debug", "User: id" + user.getUid() + " email: " + user.getEmail());
                            db.collection("users").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()){
                                            Log.d("Debug", "user exists");
                                            UserDataFirestore.getInstance().initializeUser(documentSnapshot.getData());
                                            callback.onFirestoreComplete(true, "Login successful");
                                        } else {
                                            Log.d("Debug", "user data does not exist");
                                            callback.onFirestoreComplete(false, "User does not exist");
                                        }
                                    });
                        } else {
                            Log.w("Debug", "null, signInWithEmail:failure", task.getException());
                            callback.onFirestoreComplete(false, "User does not exist");
                        }

                    } else {
                        Log.w("Debug", "signInWithEmail:failure", task.getException());
                        callback.onFirestoreComplete(false, "Invalid email or password. Please try again.");
                    }
                });
    }

    private String getWeekYearNumber(){
        LocalDate date = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int year = date.getYear();
        int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
        return String.valueOf(weekNumber) + String.valueOf(year).substring(2);
    }

}

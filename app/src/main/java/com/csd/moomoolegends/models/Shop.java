package com.csd.moomoolegends.models;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Shop extends FirestoreInstance{
    private FirebaseFirestore db = getFirestore();
    private static Shop instance;
    public static final Map<String, Number> oneRoll = new HashMap<>();
    public static final Map<String, Number> twoRolls = new HashMap<>();
    public static final Map<String, Number> threeRolls = new HashMap<>();

    private Shop() {
        super();
    }

    public static Shop getInstance() {
        if (instance == null) {
            instance = new Shop();
        }
        return instance;
    }

    public void initializeShop(OnFirestoreCompleteCallback callback){
        Log.d("Shop", "Initializing shop");
        db.collection("shop").document("shopPrices").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()){
                    Map<String, Object> shopPrices = task.getResult().getData();
                    for (Map.Entry<String, Object> entry : shopPrices.entrySet()){
                        String key = entry.getKey();
                        Map<String, Number> value = (Map<String, Number>) entry.getValue();
                        Log.d("Shop", "Key: " + key + " Value: " + value.toString());
                        switch (key){
                            case "1roll":
                                Log.d("Shop", "1roll");
                                oneRoll.putAll(value);
                                break;
                            case "2rolls":
                                Log.d("Shop", "2rolls");
                                twoRolls.putAll(value);
                                break;
                            case "3rolls":
                                Log.d("Shop", "3rolls");
                                threeRolls.putAll(value);
                                break;
                        }
                    }
                    callback.onFirestoreComplete(true, "Shop initialized successfully");
                } else {
                    callback.onFirestoreComplete(false, "Failed to get shop prices");
                }
            } else {
                callback.onFirestoreComplete(false, "Failed to get shop prices");
            }
        });
    }


}

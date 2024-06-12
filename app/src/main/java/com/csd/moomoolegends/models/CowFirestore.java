package com.csd.moomoolegends.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CowFirestore extends FirestoreInstance{
    private FirebaseFirestore db = getFirestore();
    private static CowFirestore instance;
    private static final ArrayList<Cow> listOfCows = new ArrayList<>();

    protected CowFirestore() {
        super();
    }

    public static CowFirestore getInstance() {
        if (instance == null) {
            instance = new CowFirestore();
        }
        return instance;
    }

    public void getAllCows(OnFirestoreCompleteCallback callback){
        listOfCows.clear();
        db.collection("cows").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String rarity = document.getString("rarity");
                    String imageName = document.getString("imageName");

                    if (name == null || rarity == null || imageName == null) {
                        System.out.println("One or more fields are null for document: " + document.getId());
                        continue;
                    }

                    listOfCows.add(new Cow(name, rarity, imageName));
                }
                callback.onFirestoreComplete(true, "All cows retrieved");
            } else {
                callback.onFirestoreComplete(false, "Failed to get cows");
            }
        });
    }

    public static ArrayList<Cow> getListOfCows() {
        return listOfCows;
    }
}

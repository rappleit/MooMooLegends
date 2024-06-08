package com.csd.moomoolegends.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserDataFirestore extends FirestoreInstance{

    private static UserDataFirestore instance = null;
    private FirebaseFirestore db  = FirestoreInstance.getInstance().getFirestore();
    private DocumentReference userDocument;

    private UserDataFirestore() {
    }

    public static UserDataFirestore getInstance(){
        if (instance == null){
            instance = new UserDataFirestore();
        }
        return instance;
    }

    public void setUserDocument(String username){
        userDocument = db.collection("users").document(username);
    }

    public synchronized void initializeUser(Map<String, Object> user){

    }

}

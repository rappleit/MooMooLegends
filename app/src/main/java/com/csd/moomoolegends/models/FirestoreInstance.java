package com.csd.moomoolegends.models;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreInstance {
    private static FirestoreInstance instance;
    private final FirebaseFirestore firestore;

    protected FirestoreInstance() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static FirestoreInstance getInstance() {
        if (instance == null) {
            instance = new FirestoreInstance();
        }
        return instance;
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }
}

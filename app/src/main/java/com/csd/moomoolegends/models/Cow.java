package com.csd.moomoolegends.models;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.firestore.DocumentReference;

public class Cow {
    private String name;
    private String rarity;
    private String imageName;
    private final DocumentReference documentRef;

    public Cow(String name, String rarity, String imageName, DocumentReference documentPath) {
        this.name = name;
        this.rarity = rarity;
        this.imageName = imageName;
        this.documentRef = documentPath;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public String getImageName() {
        return imageName;
    }

    public DocumentReference getDocumentRef() {
        return documentRef;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getDrawableId(Activity context, Cow cow){
        return context.getResources().getIdentifier(cow.getImageName(), "drawable", context.getPackageName());
    }
}

package com.csd.moomoolegends.models;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.firestore.DocumentReference;

public class Cow {
    private String name;
    private String rarity;
    private String imageName;

    public final static String DIAMOND_COW_NAME = "Diamond Cow";
    public final static String GOLD_COW_NAME = "Gold Cow";
    public final static String FIRE_COW_NAME = "Fire Cow";
    public final static String NATURE_COW_NAME = "Nature Cow";
    public final static String ICE_COW_NAME = "Ice Cow";
    public final static String REGULAR_COW_NAME = "Cow";

    public Cow(String name, String rarity, String imageName) {
        this.name = name;
        this.rarity = rarity;
        this.imageName = imageName;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getDrawableId(Activity context) {
        return context.getResources().getIdentifier(getImageName(), "drawable", context.getPackageName());
    }
}

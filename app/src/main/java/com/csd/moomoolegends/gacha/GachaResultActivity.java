package com.csd.moomoolegends.gacha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.models.Cow;
import com.csd.moomoolegends.models.CowFirestore;
import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class GachaResultActivity extends AppCompatActivity {

    private final static String LOG_TAG = "LOGCAT_GachaResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gacha_result);

        Intent intent = getIntent();
        final int rollsRemaining = intent.getIntExtra(GachaRollActivity.ROLLS_KEY, 0);
        final String cowName = intent.getStringExtra(GachaRollActivity.GACHA_KEY);

        ConstraintLayout layoutRoot = findViewById(R.id.layoutResultRoot);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewCowRoll);
        TextView textViewCow = (TextView) findViewById(R.id.textViewCow);
        TextView textViewRarity = (TextView) findViewById(R.id.textViewRarity);
        TextView textViewNewCow = (TextView) findViewById(R.id.textViewNewCow);

        Button btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setText(rollsRemaining > 0 ? "Continue (" + rollsRemaining + " roll" + (rollsRemaining == 1 ? "" : "s") + " remaining)" : "Back");
        btnContinue.setOnClickListener(view -> {
            Intent nextIntent = new Intent(this, rollsRemaining > 0 ? GachaRollActivity.class : ShopActivity.class);
            if (rollsRemaining > 0) {
                nextIntent.putExtra(GachaRollActivity.ROLLS_KEY, rollsRemaining);
            }
            startActivity(nextIntent);
        });

        boolean newCow = true;
        for (Cow cow : User.getUserCows()) {
            if (cow.getName().equals(cowName)) {
                newCow = false;
                break;
            }
        }

        // TODO add cow
        textViewCow.setText(Objects.requireNonNull(cowName).equals(Cow.REGULAR_COW_NAME) ? "Regular Cow" : cowName);

        CowFirestore.getInstance().getAllCows(new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success) {
                    Log.d(LOG_TAG, "Successfully retrieved all cows");
                    ArrayList<Cow> listOfCows = CowFirestore.getListOfCows();
                    for (Cow cow : listOfCows) {
                        if (cow.getName().equals(cowName)) {
                            User.addCow(cow, new OnFirestoreCompleteCallback() {
                                @Override
                                public void onFirestoreComplete(boolean success, String message) {
                                    if (success) {
                                        Log.d(LOG_TAG, "Successfully added cow: " + cowName);
                                    } else {
                                        Log.e(LOG_TAG, "Failed to add cow: " + cowName);
                                    }
                                }
                            });
                            break;
                        }
                    }
                } else {
                    Log.e(LOG_TAG, "Failed to retrieve all cows: " + message);
                }
            }
        });

        switch (cowName) {
            case Cow.NATURE_COW_NAME:
                layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.pink));
                imageView.setImageResource(R.drawable.nature_cow);
                textViewCow.setTextColor(ContextCompat.getColor(this, R.color.white));
                textViewRarity.setText(getString(R.string.rarity_rare));
                textViewRarity.setTextColor(ContextCompat.getColor(this, R.color.white));
                if (newCow) {
                    textViewNewCow.setTextColor(ContextCompat.getColor(this, R.color.very_light_green));
                }
                else {
                    textViewNewCow.setVisibility(View.GONE);
                }
                break;
            case Cow.FIRE_COW_NAME:
                layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.pink));
                imageView.setImageResource(R.drawable.fire_cow);
                textViewCow.setTextColor(ContextCompat.getColor(this, R.color.white));
                textViewRarity.setText(getString(R.string.rarity_rare));
                textViewRarity.setTextColor(ContextCompat.getColor(this, R.color.white));
                if (newCow) {
                    textViewNewCow.setTextColor(ContextCompat.getColor(this, R.color.very_light_green));
                }
                else {
                    textViewNewCow.setVisibility(View.GONE);
                }
                break;
            case Cow.ICE_COW_NAME:
                layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.pink));
                imageView.setImageResource(R.drawable.ice_cow);
                textViewCow.setTextColor(ContextCompat.getColor(this, R.color.white));
                textViewRarity.setText(getString(R.string.rarity_rare));
                textViewRarity.setTextColor(ContextCompat.getColor(this, R.color.white));
                if (newCow) {
                    textViewNewCow.setTextColor(ContextCompat.getColor(this, R.color.very_light_green));
                }
                else {
                    textViewNewCow.setVisibility(View.GONE);
                }
                break;
            case Cow.DIAMOND_COW_NAME:
                layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.purple));
                imageView.setImageResource(R.drawable.diamond_cow);
                textViewCow.setTextColor(ContextCompat.getColor(this, R.color.white));
                textViewRarity.setText(getString(R.string.rarity_epic));
                textViewRarity.setTextColor(ContextCompat.getColor(this, R.color.white));
                if (newCow) {
                    textViewNewCow.setTextColor(ContextCompat.getColor(this, R.color.gold));
                }
                else {
                    textViewNewCow.setVisibility(View.GONE);
                }
                break;
            case Cow.GOLD_COW_NAME:
                layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.gold));
                imageView.setImageResource(R.drawable.gold_cow);
                textViewRarity.setText(getString(R.string.rarity_legendary));
                if (newCow) {
                    textViewNewCow.setTextColor(ContextCompat.getColor(this, R.color.very_light_green));
                }
                else {
                    textViewNewCow.setVisibility(View.GONE);
                }
                break;
            case Cow.REGULAR_COW_NAME:
                textViewNewCow.setVisibility(View.GONE);
                break;
            default:
                Log.e(LOG_TAG, "Invalid cow name: " + cowName);
        }
    }
}
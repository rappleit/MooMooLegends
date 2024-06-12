package com.csd.moomoolegends.gacha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.models.Cow;

import java.util.Random;

public class GachaRollActivity extends AppCompatActivity {

    private final static float RARE_CHANCE = 0.2f;
    private final static float EPIC_CHANCE = 0.1f;
    private final static float LEGENDARY_CHANCE = 0.05f;

    public final static String GACHA_KEY = "gachaKey";
    public final static String ROLLS_KEY = "rollsKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gacha_roll);

        Intent shopIntent = getIntent();
        final int rollsRemaining = shopIntent.getIntExtra(ROLLS_KEY, 1);

        Random random = new Random();
        final float roll = random.nextFloat();

        new Handler().postDelayed(() -> {
            String result = Cow.REGULAR_COW_NAME;
            if (roll <= LEGENDARY_CHANCE) {
                result = Cow.GOLD_COW_NAME;
            }
            else if (roll <= EPIC_CHANCE + LEGENDARY_CHANCE) {
                result = Cow.DIAMOND_COW_NAME;
            }
            else if (roll <= RARE_CHANCE + EPIC_CHANCE + LEGENDARY_CHANCE) {
                result = new String[]{ Cow.FIRE_COW_NAME, Cow.ICE_COW_NAME, Cow.NATURE_COW_NAME }[random.nextInt(2)];
            }

            Intent intent = new Intent(this, GachaResultActivity.class);
            intent.putExtra(GACHA_KEY, result);
            intent.putExtra(ROLLS_KEY, rollsRemaining - 1);
            startActivity(intent);
        }, random.nextInt(3000) + 3000);
    }
}
package com.csd.moomoolegends.gacha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.home.HomeActivity;
import com.csd.moomoolegends.models.Cow;
import com.csd.moomoolegends.models.User;

import java.util.HashSet;
import java.util.Set;

public class ShopActivity extends AppCompatActivity {

    private ImageView imageViewNature, imageViewIce, imageViewFire, imageViewDiamond, imageViewGold;
    private TextView textViewNature, textViewIce, textViewFire, textViewDiamond, textViewGold;

    public final static int[] ROLL_PRICES = new int[]{ 100, 180, 260 };
    private final static String LOG_TAG = "LOGCAT_ShopActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        imageViewNature = (ImageView) findViewById(R.id.imageViewNatureCow);
        imageViewIce = (ImageView) findViewById(R.id.imageViewIceCow);
        imageViewFire = (ImageView) findViewById(R.id.imageViewFireCow);
        imageViewDiamond = (ImageView) findViewById(R.id.imagevIewDiamondCow);
        imageViewGold = (ImageView) findViewById(R.id.imageViewGoldCow);

        textViewNature = (TextView) findViewById(R.id.textViewNature);
        textViewIce = (TextView) findViewById(R.id.textViewIce);
        textViewFire = (TextView) findViewById(R.id.textViewFire);
        textViewDiamond = (TextView) findViewById(R.id.textViewDiamond);
        textViewGold = (TextView) findViewById(R.id.textViewGold);

        ((Button) findViewById(R.id.btnClose)).setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        });

        ConstraintLayout layoutRollOne = (ConstraintLayout) findViewById(R.id.layoutRollOne);
        ConstraintLayout layoutRollTwo = (ConstraintLayout) findViewById(R.id.layoutRollTwo);
        ConstraintLayout layoutRollThree = (ConstraintLayout) findViewById(R.id.layoutRollThree);
        int userCoins = User.getCoins();

        ((TextView) findViewById(R.id.textViewCoinShop)).setText(String.valueOf(userCoins));
        if (userCoins < ROLL_PRICES[2]) {
            layoutRollThree.setAlpha(0.7f);
            layoutRollThree.setClickable(false);
            if (userCoins < ROLL_PRICES[1]) {
                layoutRollTwo.setAlpha(0.7f);
                layoutRollTwo.setClickable(false);
                if (userCoins < ROLL_PRICES[0]) {
                    layoutRollOne.setAlpha(0.7f);
                    layoutRollOne.setClickable(false);
                }
            }
        }

        layoutRollOne.setOnClickListener(view -> {
            User.setCoins(userCoins - ROLL_PRICES[0]);
            Intent intent = new Intent(this, GachaRollActivity.class);
            intent.putExtra(GachaRollActivity.ROLLS_KEY, 1);
            startActivity(intent);
        });
        layoutRollTwo.setOnClickListener(view -> {
            User.setCoins(userCoins - ROLL_PRICES[1]);
            Intent intent = new Intent(this, GachaRollActivity.class);
            intent.putExtra(GachaRollActivity.ROLLS_KEY, 2);
            startActivity(intent);
        });
        layoutRollThree.setOnClickListener(view -> {
            User.setCoins(userCoins - ROLL_PRICES[2]);
            Intent intent = new Intent(this, GachaRollActivity.class);
            intent.putExtra(GachaRollActivity.ROLLS_KEY, 3);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // HashSet<String> cowTypes = new HashSet<>();
        for (Cow cow : User.getUserCows()) {
            switch (cow.getName()) {
                case Cow.DIAMOND_COW_NAME:
                    imageViewDiamond.setImageResource(R.drawable.diamond_cow);
                    textViewDiamond.setText(R.string.diamond_cow);
                    break;
                case Cow.FIRE_COW_NAME:
                    imageViewFire.setImageResource(R.drawable.fire_cow);
                    textViewFire.setText(R.string.fire_cow);
                    break;
                case Cow.GOLD_COW_NAME:
                    imageViewGold.setImageResource(R.drawable.gold_cow);
                    textViewGold.setText(R.string.gold_cow);
                    break;
                case Cow.ICE_COW_NAME:
                    imageViewIce.setImageResource(R.drawable.ice_cow);
                    textViewIce.setText(R.string.ice_cow);
                    break;
                case Cow.NATURE_COW_NAME:
                    imageViewNature.setImageResource(R.drawable.nature_cow);
                    textViewNature.setText(R.string.nature_cow);
            }
            //cowTypes.add(cow.getName());
        }

        /*((TextView) findViewById(R.id.textViewCowsOwned))
                .setText(getString(R.string.cows_owned_template)
                        .replace("X", String.valueOf(cowTypes.size())));*/
    }
}
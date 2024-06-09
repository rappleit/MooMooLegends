package com.csd.moomoolegends.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    private LinearProgressIndicator linearProgressMain;
    private LinearProgressIndicator linearProgressSolo;

    private final static int CARBON_THRESHOLD = 35;
    private final static int ANIM_DURATION_MS = 250;
    private int cowsAllowed = 10;
    // private final static int MENU_ITEMS_LAYOUT_MARGIN = 6;
    private final static String LOG_TAG = "LOGCAT_HomeActivity";

    // TODO: replace with user / room variables
    private final boolean inRoom = true;
    private final int roomPersons = 5;
    private final int currCarbonSolo = 20;
    private final int currCarbonRoom = 150;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Handle progress bar UIs
        linearProgressMain = (LinearProgressIndicator) findViewById(R.id.linearProgressMain);
        linearProgressSolo = (LinearProgressIndicator) findViewById(R.id.linearProgressSolo);

        // Toggle visibility of solo card view if in room
        if (inRoom) {
            CardView cardViewSolo = (CardView) findViewById(R.id.cardViewSolo);
            cardViewSolo.setVisibility(View.VISIBLE);
        }

        // Handle text views
        ((TextView) findViewById(R.id.textViewCoins)).setText("2200"); // TODO replace with user coins
        ((TextView) findViewById(R.id.textViewCows)).setText("5 cows"); // TODO replace with user cows
        TextView textViewCarbonMain = (TextView) findViewById(R.id.textViewCarbonMain);
        if (inRoom) {
            textViewCarbonMain.setText(currCarbonRoom + " kg / " + (CARBON_THRESHOLD * roomPersons) + " kg"); // TODO replace with room carbon
            ((TextView) findViewById(R.id.textViewLabelMain)).setText("TestingRoom's\nRoom Footprint"); // TODO replace with room name
            ((TextView) findViewById(R.id.textViewCarbonSolo)).setText(currCarbonSolo + " kg / " + CARBON_THRESHOLD + " kg"); // TODO replace with user carbon
            ((TextView) findViewById(R.id.textViewRoomBtn)).setText(R.string.view_room);
        }
        else {
            textViewCarbonMain.setText(currCarbonSolo + " kg / " + CARBON_THRESHOLD + " kg"); // TODO replace with user carbon
        }

        // Standardise linear layout widths
        LinearLayout layoutRecord = (LinearLayout) findViewById(R.id.layoutRecord);
        LinearLayout layoutShop = (LinearLayout) findViewById(R.id.layoutShop);
        LinearLayout layoutRecos = (LinearLayout) findViewById(R.id.layoutRecommendations);
        /*ViewGroup.LayoutParams layoutRecordParams = layoutRecord.getLayoutParams();
        ViewGroup.LayoutParams layoutShopParams = layoutShop.getLayoutParams();
        ViewGroup.LayoutParams layoutRecosParams = layoutRecos.getLayoutParams();
        int minWidth = Math.max(
                Math.max(
                        layoutRecordParams.width,
                        layoutShopParams.width
                ),
                layoutRecosParams.width
        );
        layoutRecord.setLayoutParams(new ViewGroup.LayoutParams(minWidth, layoutRecordParams.height));
        layoutShop.setLayoutParams(new ViewGroup.LayoutParams(minWidth, layoutShopParams.height));
        layoutRecos.setLayoutParams(new ViewGroup.LayoutParams(minWidth, layoutRecosParams.height));*/

        // Handle button clicks
        ((CardView) findViewById(R.id.cardViewRoomBtn)).setOnClickListener(view -> {
            // TODO call explicit intent to room activity
        });
        ((LinearLayout) findViewById(R.id.layoutLog)).setOnClickListener(view -> {
            // TODO call explicit intent to food logger activity
        });
        layoutRecord.setOnClickListener(view -> {
            // TODO call explicit intent to records activity
        });
        layoutShop.setOnClickListener(view -> {
            // TODO call explicit intent to shop activity
        });
        layoutRecos.setOnClickListener(view -> {
            // TODO call explicit intent to recommendations activity
        });

        // Handle menu click
        ImageButton imageBtnMenu = (ImageButton) findViewById(R.id.imageBtnMenu);
        imageBtnMenu.setOnClickListener(view -> {
            if (layoutRecord.getVisibility() == View.VISIBLE) {
                imageBtnMenu.setImageResource(R.drawable.baseline_menu_36);

                // Move buttons back to starting position
                ObjectAnimator translateRecord = ObjectAnimator.ofFloat(layoutRecord, "translationY", imageBtnMenu.getY());
                ObjectAnimator translateShop = ObjectAnimator.ofFloat(layoutShop, "translationY", imageBtnMenu.getY());
                ObjectAnimator translateRecos = ObjectAnimator.ofFloat(layoutRecos, "translationY", imageBtnMenu.getY());

                // Fade buttons out
                ValueAnimator fadeOutRecord = ObjectAnimator.ofFloat(layoutRecord, "alpha", 1f, 0f);
                ValueAnimator fadeOutShop = ObjectAnimator.ofFloat(layoutShop, "alpha", 1f, 0f);
                ValueAnimator fadeOutRecos = ObjectAnimator.ofFloat(layoutRecos, "alpha", 1f, 0f);

                // Set layout visibilities to View.GONE
                fadeOutRecord.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        layoutRecord.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {}
                });
                fadeOutShop.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        layoutShop.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {}
                });
                fadeOutRecos.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        layoutRecos.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {}

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {}
                });

                // Animate everything together
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(translateRecord, translateShop, translateRecos, fadeOutRecord, fadeOutShop, fadeOutRecos);
                animatorSet.setDuration(ANIM_DURATION_MS).start();
            }
            else {
                imageBtnMenu.setImageResource(R.drawable.baseline_close_36);

                // Set layout visibilities to View.VISIBLE
                layoutRecord.setVisibility(View.VISIBLE);
                layoutShop.setVisibility(View.VISIBLE);
                layoutRecos.setVisibility(View.VISIBLE);

                // Move buttons to respective positions
                // TODO: Somehow if using dynamic calculations, the first click will always glitch out
                /*float startY = imageBtnMenu.getY() + imageBtnMenu.getHeight() + dpToPx(MENU_ITEMS_LAYOUT_MARGIN);
                ObjectAnimator translateRecord = ObjectAnimator.ofFloat(layoutRecord, "translationY", startY);
                startY += layoutRecord.getHeight() + dpToPx(MENU_ITEMS_LAYOUT_MARGIN);
                ObjectAnimator translateShop = ObjectAnimator.ofFloat(layoutShop, "translationY", startY);
                startY += layoutShop.getHeight() + dpToPx(MENU_ITEMS_LAYOUT_MARGIN);
                ObjectAnimator translateRecos = ObjectAnimator.ofFloat(layoutRecos, "translationY", startY);*/
                ObjectAnimator translateRecord = ObjectAnimator.ofFloat(layoutRecord, "translationY", dpToPx(72));
                ObjectAnimator translateShop = ObjectAnimator.ofFloat(layoutShop, "translationY", dpToPx(120));
                ObjectAnimator translateRecos = ObjectAnimator.ofFloat(layoutRecos, "translationY", dpToPx(168));

                // Fade buttons in
                ValueAnimator fadeInRecord = ObjectAnimator.ofFloat(layoutRecord, "alpha", 0f, 1f);
                ValueAnimator fadeInShop = ObjectAnimator.ofFloat(layoutShop, "alpha", 0f, 1f);
                ValueAnimator fadeInRecos = ObjectAnimator.ofFloat(layoutRecos, "alpha", 0f, 1f);

                // Animate everything together
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(translateRecord, translateShop, translateRecos, fadeInRecord, fadeInShop, fadeInRecos);
                animatorSet.setDuration(ANIM_DURATION_MS).start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Handle linear progress bars
        if (inRoom) {
            linearProgressMain.setMax(CARBON_THRESHOLD * roomPersons);
            linearProgressMain.setProgress(currCarbonRoom, true);
            linearProgressSolo.setMax(CARBON_THRESHOLD);
            linearProgressSolo.setProgress(currCarbonSolo, true);
        }
        else {
            linearProgressMain.setMax(CARBON_THRESHOLD);
            linearProgressMain.setProgress(currCarbonSolo, true);
        }

        // Draw cows
        startAnimationFromBackgroundThread(R.drawable.cow, 1);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Reset progress bar animations so that onResume() can display them again
        linearProgressMain.setProgress(0, true);
        linearProgressSolo.setProgress(0, true);
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getApplicationContext().getResources().getDisplayMetrics()
        );
    }

    private void startAnimationFromBackgroundThread(int cowImage, int id) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Log.v(LOG_TAG, "Worker thread id:" + Thread.currentThread().getId());
            HomeActivity.this.runOnUiThread(() -> {
                Random random = new Random();
                Log.v(LOG_TAG, "Animation thread id:" + Thread.currentThread().getId());

                // Spawn a new cow
                ImageView imageView = new ImageView(HomeActivity.this);
                imageView.setId(id);
                imageView.setImageResource(cowImage);

                // Set initial layout parameters
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                imageView.setScaleX(0.7f);
                imageView.setScaleY(0.7f);
                imageView.setLayoutParams(layoutParams);

                // Add the cow to the layout
                ConstraintLayout rootLayout = findViewById(R.id.rootLayout);
                rootLayout.addView(imageView, 0);

                // Set the constraints programmatically
                DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
                int startX = random.nextInt(displayMetrics.widthPixels - imageView.getWidth()) - (displayMetrics.widthPixels - imageView.getWidth()) / 2,
                        startY = random.nextInt(displayMetrics.heightPixels - imageView.getHeight()) - (displayMetrics.heightPixels - imageView.getHeight()) / 2;
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(rootLayout);
                constraintSet.connect(id, ConstraintSet.TOP, rootLayout.getId(), ConstraintSet.TOP);
                constraintSet.connect(id, ConstraintSet.BOTTOM, rootLayout.getId(), ConstraintSet.BOTTOM);
                constraintSet.setMargin(id, ConstraintSet.START, startX);
                constraintSet.setMargin(id, ConstraintSet.TOP, startY);
                constraintSet.applyTo(rootLayout);

                // Animate the ImageView to move on-screen
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator translateX = ObjectAnimator.ofFloat(
                        imageView, "translationX",
                        startX, random.nextInt(displayMetrics.widthPixels - imageView.getWidth()) - (float) (displayMetrics.widthPixels - imageView.getWidth()) / 2
                );
                ObjectAnimator translateY = ObjectAnimator.ofFloat(
                        imageView, "translationY",
                        startY, random.nextInt(displayMetrics.heightPixels - imageView.getHeight()) - (float) (displayMetrics.heightPixels - imageView.getHeight()) / 2
                );
                ValueAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);
                animatorSet.playTogether(translateX, translateY, fadeIn);
                animatorSet.setDuration(3000);
                animatorSet.start();

                final Rect screen = new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
                Rect position = new Rect();
                imageView.getGlobalVisibleRect(position);

                // TODO Continue animating while image is on screen
                /*while (screen.intersect(position)) {

                }*/
            });
        });
    }
}
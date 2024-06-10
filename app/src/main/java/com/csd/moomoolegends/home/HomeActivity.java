package com.csd.moomoolegends.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csd.moomoolegends.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private LinearProgressIndicator linearProgressMain;
    private LinearProgressIndicator linearProgressSolo;

    private final static int CARBON_THRESHOLD = 35;
    private final static int ANIM_DURATION_MS = 250;
    private final static int COWS_ALLOWED = 10;
    // private final static int MENU_ITEMS_LAYOUT_MARGIN = 6;
    private final static String LOG_TAG = "LOGCAT_HomeActivity";

    // TODO: replace with user / room variables
    private final boolean inRoom = true;
    private final int numCows = 5;
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
        ((TextView) findViewById(R.id.textViewCows)).setText(numCows + " cows"); // TODO replace with user cows
        TextView textViewCarbonMain = (TextView) findViewById(R.id.textViewCarbonMain);
        if (inRoom) {
            textViewCarbonMain.setText(currCarbonRoom + " kg / " + (CARBON_THRESHOLD * roomPersons) + " kg"); // TODO replace with room carbon
            ((TextView) findViewById(R.id.textViewLabelMain)).setText("TestingRoom's\nRoom Footprint"); // TODO replace with room name
            ((TextView) findViewById(R.id.textViewCarbonSolo)).setText(currCarbonSolo + " kg / " + CARBON_THRESHOLD + " kg"); // TODO replace with user carbon
            ((TextView) findViewById(R.id.textViewRoomBtn)).setText(R.string.view_room);
        } else {
            textViewCarbonMain.setText(currCarbonSolo + " kg / " + CARBON_THRESHOLD + " kg"); // TODO replace with user carbon
        }

        // Handle button clicks
        LinearLayout layoutRecord = (LinearLayout) findViewById(R.id.layoutRecord);
        LinearLayout layoutShop = (LinearLayout) findViewById(R.id.layoutShop);
        LinearLayout layoutRecos = (LinearLayout) findViewById(R.id.layoutRecommendations);
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
                fadeOutRecord.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layoutRecord.setVisibility(View.GONE);
                    }
                });
                fadeOutShop.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layoutShop.setVisibility(View.GONE);
                    }
                });
                fadeOutRecos.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layoutRecos.setVisibility(View.GONE);
                    }
                });

                // Animate everything together
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(translateRecord, translateShop, translateRecos, fadeOutRecord, fadeOutShop, fadeOutRecos);
                animatorSet.setDuration(ANIM_DURATION_MS).start();
            } else {
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
                ObjectAnimator translateRecord = ObjectAnimator.ofFloat(layoutRecord, "translationY", 0);
                ObjectAnimator translateShop = ObjectAnimator.ofFloat(layoutShop, "translationY", dpToPx(48));
                ObjectAnimator translateRecos = ObjectAnimator.ofFloat(layoutRecos, "translationY", dpToPx(96));

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
        } else {
            linearProgressMain.setMax(CARBON_THRESHOLD);
            linearProgressMain.setProgress(currCarbonSolo, true);
        }

        // Draw cows
        for (int cow = 1; cow <= Math.min(COWS_ALLOWED, numCows); cow++) {
            startAnimationFromBackgroundThread(R.drawable.cow, cow);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Reset progress bar animations so that onResume() can display them again
        linearProgressMain.setProgress(0, true);
        linearProgressSolo.setProgress(0, true);
    }

    private void startAnimationFromBackgroundThread(int cowImage, int id) {
        // Spawn a new cow
        ImageView imageView = new ImageView(HomeActivity.this);
        imageView.setId(id);
        imageView.setImageResource(cowImage);

        // Set initial layout parameters
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        imageView.setScaleX(0.6f);
        imageView.setScaleY(0.6f);
        imageView.setLayoutParams(layoutParams);

        // Add the cow to the layout
        ConstraintLayout cowLayout = findViewById(R.id.cowLayout);
        cowLayout.addView(imageView, 0);

        // Set the constraints programmatically
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(cowLayout);
        constraintSet.connect(id, ConstraintSet.TOP, cowLayout.getId(), ConstraintSet.TOP);
        constraintSet.connect(id, ConstraintSet.BOTTOM, cowLayout.getId(), ConstraintSet.BOTTOM);
        constraintSet.setMargin(id, ConstraintSet.START, 0);
        constraintSet.setMargin(id, ConstraintSet.TOP, 0);
        constraintSet.applyTo(cowLayout);

        // Add animation as view tree layout listener
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Create animation update listener to re-draw cows as needed
                ValueAnimator.AnimatorUpdateListener animationUpdateListener = valueAnimator -> {
                    int[] currLocation = new int[2];
                    imageView.getLocationOnScreen(currLocation);
                    Rect currPosition = new Rect(
                            currLocation[0], currLocation[1],
                            currLocation[0] + imageView.getWidth(),
                            currLocation[1] + imageView.getHeight()
                    );
                    for (int i = 0; i < cowLayout.getChildCount(); i++) {
                        int[] childLocation = new int[2];
                        ImageView childView = (ImageView) cowLayout.getChildAt(i);
                        if (childView.getId() == imageView.getId()) {
                            continue;
                        }
                        childView.getLocationOnScreen(childLocation);
                        Rect childPosition = new Rect(
                                childLocation[0], childLocation[1],
                                childLocation[0] + imageView.getWidth(),
                                childLocation[1] + imageView.getHeight()
                        );
                        if (currPosition.intersect(childPosition) && currLocation[1] < childLocation[1]) {
                            cowLayout.removeView(imageView);
                            cowLayout.addView(imageView, 0);
                        }
                    }
                };

                // Animate the cow to move on-screen
                AnimatorSet animatorSetStart = new AnimatorSet();
                ObjectAnimator translateXStart = ObjectAnimator.ofFloat(imageView, "translationX", getRandomX(imageView.getWidth()), getRandomX(imageView.getWidth()));
                ObjectAnimator translateYStart = ObjectAnimator.ofFloat(imageView, "translationY", getRandomY(imageView.getHeight()), getRandomY(imageView.getHeight()));
                translateXStart.addUpdateListener(animationUpdateListener);
                translateYStart.addUpdateListener(animationUpdateListener);
                ValueAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f);

                fadeIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                        executorService.scheduleAtFixedRate(() ->
                            HomeActivity.this.runOnUiThread(() -> {
                                AnimatorSet animatorSetRun = new AnimatorSet();

                                // Check whether to move off-screen
                                if (new Random().nextFloat() > 0.9) {
                                    // Log.d(LOG_TAG, imageView.getId() + " end");

                                    // Re-draw cow behind all other cows
                                    cowLayout.removeView(imageView);
                                    cowLayout.addView(imageView, 0);

                                    // Animate cow off-screen
                                    ObjectAnimator translateXEnd = ObjectAnimator.ofFloat(imageView, "translationX", -100f);
                                    ValueAnimator fadeOut = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);
                                    fadeOut.addListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            cowLayout.removeView(imageView);
                                            executorService.shutdownNow();
                                            startAnimationFromBackgroundThread(R.drawable.cow, id);
                                        }
                                    });
                                    animatorSetRun.playTogether(translateXEnd, fadeOut);
                                }
                                else {
                                    // Log.d(LOG_TAG, imageView.getId() + " continue");

                                    // Continue moving cow on-screen
                                    ObjectAnimator translateXRun = ObjectAnimator.ofFloat(imageView, "translationX", getRandomX(imageView.getWidth()));
                                    ObjectAnimator translateYRun = ObjectAnimator.ofFloat(imageView, "translationY", getRandomY(imageView.getHeight()));
                                    translateXRun.addUpdateListener(animationUpdateListener);
                                    translateYRun.addUpdateListener(animationUpdateListener);
                                    animatorSetRun.playTogether(translateXRun, translateYRun);
                                }
                                animatorSetRun.setDuration(new Random().nextInt(3000) + 10000);
                                animatorSetRun.start();
                            }),
                            new Random().nextInt(10) + 10,
                            new Random().nextInt(10) + 15,
                            TimeUnit.SECONDS
                        );
                    }
                });

                animatorSetStart.playTogether(translateXStart, translateYStart, fadeIn);
                animatorSetStart.setDuration(new Random().nextInt(3000) + 10000);
                animatorSetStart.start();
            }
        });
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getApplicationContext().getResources().getDisplayMetrics()
        );
    }

    private int getRandomX(int viewWidth) {
        return new Random().nextInt(Resources.getSystem().getDisplayMetrics().widthPixels - viewWidth);
    }

    private int getRandomY(int viewHeight) {
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        return new Random().nextInt(screenHeight - viewHeight) - (screenHeight - viewHeight) / 2;
    }
}
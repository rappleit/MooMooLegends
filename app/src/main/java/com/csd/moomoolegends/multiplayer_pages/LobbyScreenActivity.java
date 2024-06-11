package com.csd.moomoolegends.multiplayer_pages;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.csd.moomoolegends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LobbyScreenActivity extends AppCompatActivity {

    ImageButton backButton;
    TextView numberOfPlayers, player1, player2, player3, player4, player5, nextChallengeCountdownText, timeToNextChallenge;
    TextView currentCoins;

    // List to keep track of the users
    List<String> users = new ArrayList<>();

    // Firestore and FirebaseAuth instances
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_screen);

        // Initialize all variables
        backButton = (ImageButton) findViewById(R.id.backButton);
        numberOfPlayers = (TextView) findViewById(R.id.number_of_players);
        player1 = (TextView) findViewById(R.id.player_1);
        player2 = (TextView) findViewById(R.id.player_2);
        player3 = (TextView) findViewById(R.id.player_3);
        player4 = (TextView) findViewById(R.id.player_4);
        player5 = (TextView) findViewById(R.id.player_5);
        nextChallengeCountdownText = (TextView) findViewById(R.id.next_challenge_countdown_text);
        timeToNextChallenge = (TextView) findViewById(R.id.time_to_next_challenge);
        currentCoins = (TextView) findViewById(R.id.Current_coins);

        // Fetch the current user's coins from Firestore
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int coins = Objects.requireNonNull(document.getLong("coins")).intValue();
                        currentCoins.setText(String.valueOf(coins));
                    }
                }
            }
        });

        // Initialize the host (player 1)
        String hostUsername = mAuth.getCurrentUser().getDisplayName();
        users.add(hostUsername);
        player1.setText(hostUsername);

        // Initialize the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go back to HomeActivity or the parent activity
                Intent intent = new Intent(LobbyScreenActivity.this, MultiHomePageActivity.class);
                startActivity(intent);
                finish(); // Finish this activity
            }
        });

        // Update the player slots
        updatePlayerSlots();
    }

    private void updatePlayerSlots() {
        // Clear all player slots
        player1.setText("");
        player2.setText("");
        player3.setText("");
        player4.setText("");
        player5.setText("");

        // Update the player slots based on the order of the users in the list
        if (!users.isEmpty()) {
            player1.setText(users.get(0));
        }
        if (users.size() > 1) {
            player2.setText(users.get(1));
        }
        if (users.size() > 2) {
            player3.setText(users.get(2));
        }
        if (users.size() > 3) {
            player4.setText(users.get(3));
        }
        if (users.size() > 4) {
            player5.setText(users.get(4));
        }

        // Update the number of players
        numberOfPlayers.setText(String.valueOf(users.size()));
    }

    // Call this method whenever a new player joins the room
    public void onPlayerJoin(String username) {
        users.add(username);
        updatePlayerSlots();
    }
}
package com.csd.moomoolegends.multiplayer_pages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.models.OnRoomListenerChange;
import com.csd.moomoolegends.models.RoomFirestore;
import com.csd.moomoolegends.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LobbyScreenActivity extends AppCompatActivity implements OnRoomListenerChange {

    ImageButton backButton;
    Button startButton;
    TextView numberOfPlayers, player1, player2, player3, player4, player5, nextChallengeCountdownText, timeToNextChallenge;
    TextView currentCoins;
    private TextView roomCode;
    private TextView roomName;
    private int currentSize;
    String roomOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lobby_screen);
        RoomFirestore.setOnRoomListenerChange(this);
        // Initialize all variables
        backButton = findViewById(R.id.backButton);
        startButton = findViewById(R.id.startGame);
        numberOfPlayers = findViewById(R.id.numPlayers);
        player1 =  findViewById(R.id.player1);
        player2 =  findViewById(R.id.player2);
        player3 =  findViewById(R.id.player3);
        player4 =  findViewById(R.id.player4);
        player5 =  findViewById(R.id.player5);
        // nextChallengeCountdownText = (TextView) findViewById(R.id.next_challenge_countdown_text);
        // timeToNextChallenge = (TextView) findViewById(R.id.time_to_next_challenge);
        currentCoins = findViewById(R.id.coins);
        roomCode = findViewById(R.id.roomCode);
        roomName = findViewById(R.id.roomName);

        numberOfPlayers.setText(String.valueOf(User.getRoom().getRoomCurrentSize()));
        currentCoins.setText(String.valueOf(User.getCoins()));

        User.getRoom().getRoomOwner().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    DocumentSnapshot user = task.getResult();
                    roomOwner = String.valueOf(user.get("username"));
                    roomCode.setText(String.valueOf(User.getRoom().getRoomCode()));
                    roomName.setText(String.valueOf(User.getRoom().getRoomName()));
                    try {
                        updatePlayerSlots();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private synchronized void updatePlayerSlots() throws InterruptedException {
        // Clear all player slots
        CountDownLatch latch = new CountDownLatch(User.getRoom().getRoomCurrentSize());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        currentSize = User.getRoom().getRoomCurrentSize();
        Log.d("Debug", User.getRoom().toString());
        player1.setText(roomOwner);
        player2.setVisibility(View.VISIBLE);
        player3.setVisibility(View.VISIBLE);
        player4.setVisibility(View.VISIBLE);
        player5.setVisibility(View.VISIBLE);

        Log.d("Debug", player1.getText().toString());

        final int[] i = {1};

        executor.execute(() -> {
            for (DocumentReference userRef:User.getRoom().getRoomMembers()){
                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            DocumentSnapshot user = task.getResult();
                            Log.d("Debug","New username: " + user.get("username"));
                            if (user.exists() && !Objects.equals(user.get("username"), roomOwner)){
                                i[0] += 1;
                                Log.d("Debug", "Loop " + i[0]);
                                switch (i[0]) {
                                    case 2:
                                        player2.setText(user.get("username").toString());
                                        break;
                                    case 3:
                                        player3.setText(user.get("username").toString());
                                        break;
                                    case 4:
                                        player4.setText(user.get("username").toString());
                                        break;
                                    case 5:
                                        player5.setText(user.get("username").toString());
                                        break;
                                }
                            }
                            latch.countDown();
                        } else {
                            latch.countDown();
                        }
                    }
                });
            }
        });


        executor.execute(() -> {
            try {
                latch.await();
                updateRestNames(i[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateRestNames(int players){
        int emptySlots = User.getRoom().getRoomMaxSize() - players;
        Log.d("Debug", emptySlots + " empty");
        switch (emptySlots){
            case 4:
                player2.setVisibility(View.INVISIBLE);
                player3.setVisibility(View.INVISIBLE);
                player4.setVisibility(View.INVISIBLE);
                player5.setVisibility(View.INVISIBLE);
                break;
            case 3:
                player3.setVisibility(View.INVISIBLE);
                player4.setVisibility(View.INVISIBLE);
                player5.setVisibility(View.INVISIBLE);
                break;
            case 2:
                player4.setVisibility(View.INVISIBLE);
                player5.setVisibility(View.INVISIBLE);
                break;
            case 1:
                player5.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

        // Update the number of players
        String newNumber = "Number of players: " + currentSize;
        numberOfPlayers.setText(newNumber);
        Log.d("Debug", currentSize + "");
    }

    @Override
    public void onChange() {
        numberOfPlayers.setText(String.valueOf(User.getRoom().getRoomCurrentSize()));
        currentSize = User.getRoom().getRoomCurrentSize();

        try{
            updatePlayerSlots();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
package com.csd.moomoolegends.multiplayer_pages;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.csd.moomoolegends.MainActivity;
import com.csd.moomoolegends.R;
import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.RoomFirestore;
import com.csd.moomoolegends.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class JoinPrivateRoomActivity extends AppCompatActivity {

    ImageButton backButton;
    ImageButton joinRoomButton;
    EditText roomCodeInput;
    TextView Current_coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_private_room);

        backButton = (ImageButton) findViewById(R.id.backButton);
        joinRoomButton = (ImageButton) findViewById(R.id.join_room_button);
        roomCodeInput = (EditText) findViewById(R.id.room_code_input);
        Current_coins = (TextView) findViewById(R.id.Current_coins);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinPrivateRoomActivity.this, MultiHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (roomCodeInput.getText().toString().isEmpty() || roomCodeInput.getText().toString().length() != 5 || !roomCodeInput.getText().toString().matches("[0-9]+")) {
                    roomCodeInput.setError("Please enter a valid room code.");
                    Log.d("Room Code Input", "Invalid room code entered.");
                    return;
                }
                else {
                    String roomCode = roomCodeInput.getText().toString();

                    if (User.getRoomCode() != null) {
                        Log.d("Debug", "User is already in a room.");
                        return;
                    }

                    // Check if the room exists before attempting to join
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("rooms").document(roomCode);
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                // If the room exists, attempt to join
                                RoomFirestore.getInstance().joinRoom(roomCode, new OnFirestoreCompleteCallback() {
                                    @Override
                                    public void onFirestoreComplete(boolean success, String message) {
                                        if(success){
                                            Log.d("Debug", message);
                                            Intent intent = new Intent(JoinPrivateRoomActivity.this, LobbyScreenActivity.class);
                                            intent.putExtra("roomCode", roomCode);
                                            intent.putExtra("UserName", User.getUsername());
                                            Log.d("Debug", "New user joining roomcode: " + User.getRoomCode());
                                            startActivity(intent);
                                        } else {
                                            Log.d("Debug", message);
                                        }
                                    }
                                });
                            } else {
                                // If the room does not exist, display an error message
                                roomCodeInput.setError("The room does not exist.");
                                Log.d("Debug", "The room does not exist.");
                            }
                        } else {
                            Log.d("Debug", "Failed to check room code.");
                        }
                    });
                }
            }
        });
    }
}
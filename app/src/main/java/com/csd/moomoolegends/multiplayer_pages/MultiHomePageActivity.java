package com.csd.moomoolegends.multiplayer_pages;

import android.app.Activity;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.adaptors.PublicRoomViewAdapter;
import com.csd.moomoolegends.home.HomeActivity;
import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.RoomFirestore;
import com.csd.moomoolegends.models.User;

public class MultiHomePageActivity extends AppCompatActivity {

    ImageButton backButton;
    ImageButton createRoomButton;
    ImageButton joinPrivateRoomButton;
    View joinPublicRoom;
    TextView current_coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_home_page);

        // Declare all variables at the top
        backButton = (ImageButton) findViewById(R.id.backButton);
        createRoomButton = (ImageButton) findViewById(R.id.create_room);
        joinPrivateRoomButton = (ImageButton) findViewById(R.id.join_private_room);
        joinPublicRoom = findViewById(R.id.join_public_room);
        current_coins = (TextView) findViewById(R.id.Current_coins);

        current_coins.setText(String.valueOf(User.getCoins()));

        // Initialize the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go back to MainActivity or the parent activity
                Log.d("Debug", "Back Button Clicked");
                Intent intent = new Intent(MultiHomePageActivity.this, HomeActivity.class);
                Log.d("Debug", "Starting Home Activity");
                startActivity(intent);
                finish(); // Finish this activity
            }
        });

        // Initialize the create room button
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (User.getInRoom()){
                    Intent intent = new Intent(MultiHomePageActivity.this, LobbyScreenActivity.class);
                    intent.putExtra("alreadyInRoom", true);
                    startActivity(intent);
                } else {
                    // Call the createRoom method from RoomFirestore
                    RoomFirestore.getInstance().createRoom(new OnFirestoreCompleteCallback() {
                        @Override
                        public void onFirestoreComplete(boolean success, String message) {
                            if (success) {
                                // If the room is created successfully, navigate to the LobbyScreenActivity
                                Log.d("Debug", "Room Created in Firestore");
                                Intent intent = new Intent(MultiHomePageActivity.this, LobbyScreenActivity.class);
                                intent.putExtra("alreadyInRoom", false);
                                startActivity(intent);
                                Log.d("Debug", "New user creating roomcode: " + User.getRoomCode());
                                finish();
                            } else {
                                // Handle the error here
                                Log.d("Debug", "Failed to create room, please try again.");
                            }
                        }
                    });
                }
            }
        });

        // Initialize the join private room button
        joinPrivateRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to JoinPrivateRoomActivity
                Log.d("Debug", "Join Private Room Button Clicked");
                Intent intent = new Intent(MultiHomePageActivity.this, JoinPrivateRoomActivity.class);
                startActivity(intent);
                Log.d("Debug", "Join Private Room Activity Started");
                finish();
            }
        });

        // Initialize the join public room button
        joinPublicRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to JoinPublicRoomActivity
                Log.d("Debug", "Join Public Room Button Clicked");
                RoomFirestore.getInstance().getAllPublicRooms(new OnFirestoreCompleteCallback() {
                    @Override
                    public void onFirestoreComplete(boolean success, String message) {
                        if (success){
                            Log.d("Debug", message);
                            Log.d("Debug", RoomFirestore.getInstance().publicRooms.toString());
                            Intent intent = new Intent(MultiHomePageActivity.this, ViewPublicRoomsActivity.class);
                            startActivity(intent);
                            Log.d("Debug", "Join Public Room Activity Started");
                            finish();
                        } else {
                            Log.d("Debug", message);
                        }
                    }
                });
            }
        });

        if (User.getInRoom()){
            joinPrivateRoomButton.setEnabled(false);
            joinPublicRoom.setEnabled(false);
        } else {
            joinPrivateRoomButton.setEnabled(true);
            joinPublicRoom.setEnabled(true);
        }
    }

}
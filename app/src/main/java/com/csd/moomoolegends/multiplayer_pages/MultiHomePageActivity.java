package com.csd.moomoolegends.multiplayer_pages;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Intent;

import com.csd.moomoolegends.R;
import com.csd.moomoolegends.home.HomeActivity;

public class MultiHomePageActivity extends Activity {

    ImageButton backButton;
    ImageButton createRoomButton;
    ImageButton joinPrivateRoomButton;
    View joinPublicRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiplayer_home_page);

        // Initialize the back button
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go back to MainActivity or the parent activity
                Intent intent = new Intent(MultiHomePageActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Finish this activity
            }
        });

        // Initialize the create room button
        createRoomButton = (ImageButton) findViewById(R.id.create_room);
        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to CreateRoomActivity
                Intent intent = new Intent(MultiHomePageActivity.this, CreateRoomActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the join private room button
        joinPrivateRoomButton = (ImageButton) findViewById(R.id.join_private_room);
        joinPrivateRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to JoinPrivateRoomActivity
                Intent intent = new Intent(MultiHomePageActivity.this, JoinPrivateRoomActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the join public room button
        joinPublicRoom = findViewById(R.id.join_public_room);
        joinPublicRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to JoinPublicRoomActivity
                Intent intent = new Intent(MultiHomePageActivity.this, JoinPublicRoomActivity.class);
                startActivity(intent);
            }
        });
    }
}
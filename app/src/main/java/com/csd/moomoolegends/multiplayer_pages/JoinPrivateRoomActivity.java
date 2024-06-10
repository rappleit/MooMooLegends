package com.csd.moomoolegends.multiplayer_pages;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.Intent;
import android.widget.TextView;

import com.csd.moomoolegends.MainActivity;
import com.csd.moomoolegends.R;

public class JoinPrivateRoomActivity extends Activity {

    ImageButton backButton;
    ImageButton joinRoomButton;
    EditText roomCodeInput;

    TextView Current_coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_private_room); // Make sure to replace 'your_layout_name' with the actual name of your XML layout file

        // Initialize the back button
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go back to MainActivity or the parent activity
                Intent intent = new Intent(JoinPrivateRoomActivity.this, MultiHomePageActivity.class);
                startActivity(intent);
                finish(); // Finish this activity
            }
        });

        // Initialize the join room button
        joinRoomButton = (ImageButton) findViewById(R.id.join_room_button);
        roomCodeInput = (EditText) findViewById(R.id.room_code_input);
        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to go to JoinRoomActivity
                if (roomCodeInput.getText().toString().isEmpty() || roomCodeInput.getText().toString().length() != 5 || !roomCodeInput.getText().toString().matches("[0-9]+")) {
                    // Show an error message if the room code is empty
                    roomCodeInput.setError("Please enter a valid room code.");
                    Log.d("Room Code Input", "Invalid room code entered.");
                    return;
                }
                else {
                    Intent intent = new Intent(JoinPrivateRoomActivity.this, MainActivity.class);
                    // Pass the room code to the next activity
                    //TODO: Implement this by Linking sending the code to firestore and pulling the room data and redirecting to the room

                }
            }
        });

        Current_coins = (TextView) findViewById(R.id.Current_coins);
        Current_coins.setText("1");
    }
}

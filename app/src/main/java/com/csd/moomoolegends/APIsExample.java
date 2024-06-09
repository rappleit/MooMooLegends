package com.csd.moomoolegends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatToggleButton;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.RoomFirestore;
import com.csd.moomoolegends.models.SignUpLoginFirestore;
import com.csd.moomoolegends.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class APIsExample extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText usernameInput;
    private TextInputEditText roomCodeInput;
    private AppCompatButton login;
    private AppCompatButton signup;
    private AppCompatButton createRoom;
    private AppCompatToggleButton publicOrPrivateRoom;
    private AppCompatButton joinRoom;
    private AppCompatButton leaveRoom;
    private FirebaseAuth mAuth;
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FirebaseUser currentUser = mAuth.getCurrentUser(); should be called in the onStart function of the Activity (seen below)
        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        usernameInput = findViewById(R.id.username);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);


        // API call for logging in
        login.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();


            // Login function params:
            // activity: current activity
            // email: String email input
            // password: String password input
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            SignUpLoginFirestore.getInstance().login(activity, email, password, new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if(success){
                        //Login successful
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Login successful"

                    } else {
                        //Login failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Invalid email or password. Please try again." or "User does not exist"
                    }
                }
            });
        });


        // API call for signing up
        signup.setOnClickListener(v -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String username = usernameInput.getText().toString();

            // SignUp function params:
            // activity: current activity
            // username: String username input
            // email: String email input
            // password: String password input
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            SignUpLoginFirestore.getInstance().signUp(activity, username, email, password, new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if(success){
                        //Signup successful
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "User registered successfully"
                        Log.d("Debug", String.valueOf(User.getUserCows()));
                    } else {
                        //Signup failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Username taken, please choose another one." or "Failed to register user, please try again."
                    }
                }
            });
        });


        // API call for creating room
        createRoom = findViewById(R.id.createRoom);
        createRoom.setOnClickListener(v -> {
            // Create room function params:
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            RoomFirestore.getInstance().createRoom(new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if(success){
                        //Room created successfully
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Room created successfully"
                        Log.d("Debug", "New user creating roomcode: " + User.getRoomCode());
                    } else {
                        //Room creation failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Failed to create room, please try again."
                    }
                }
            });
        });


        // Toggle for setting room privacy
        publicOrPrivateRoom = findViewById(R.id.privateOrPublic);
        publicOrPrivateRoom.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Set room privacy function params:
            // roomCode: String room code
            // isChecked: boolean for room privacy
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            RoomFirestore.getInstance().toggleRoomPrivacy(User.getRoomCode(), isChecked, new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if(success){
                        //Room privacy set successfully
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Room privacy set successfully"
                        Log.d("Debug", String.valueOf(User.getRoom().getRoomIsPrivate()));
                    } else {
                        //Room privacy set failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Failed to set room privacy, please try again."
                    }
                }
            });
        });

        // API call for joining room
        joinRoom = findViewById(R.id.joinRoom);
        roomCodeInput = findViewById(R.id.roomCode);
        joinRoom.setOnClickListener(v -> {
            String roomCode = roomCodeInput.getText().toString();

            // Join room function params:
            // roomCode: String room code
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            RoomFirestore.getInstance().joinRoom(roomCode, new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if(success){
                        //Room joined successfully
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Room joined successfully"
                        Log.d("Debug", "New user joining roomcode: " + User.getRoomCode());
                    } else {
                        //Room join failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Room does not exist" or "Failed to join room"
                    }
                }
            });
        });


        // API call for leaving room
        leaveRoom = findViewById(R.id.leaveRoom);
        leaveRoom.setOnClickListener(v -> {
            // Leave room function params:
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            RoomFirestore.getInstance().leaveRoom(new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if(success){
                        //Room left successfully
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Room left successfully"
                        Log.d("Debug", "New user leaving roomcode: " + User.getRoomCode());
                    } else {
                        //Room leave failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Failed to leave room"
                    }
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.d("Debug", "account not found");
        }
    }
}
package com.csd.moomoolegends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatToggleButton;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.csd.moomoolegends.models.Cow;
import com.csd.moomoolegends.models.CowFirestore;
import com.csd.moomoolegends.models.OnCowRemoval;
import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.RoomFirestore;
import com.csd.moomoolegends.models.SignUpLoginFirestore;
import com.csd.moomoolegends.models.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

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
    private AppCompatButton stopListener;
    private AppCompatButton logout;
    private AppCompatButton getPublicRooms;
    private AppCompatButton startCountdown;
    private AppCompatButton addCow;
    private AppCompatButton removeCow;
    private AppCompatButton removeRandomCow;
    private AppCompatButton getAllCows;
    private AppCompatImageView cowImage;
    private AppCompatTextView countdown;
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


        // API for logging out
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(v -> {
            // Logout function params:
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            SignUpLoginFirestore.getInstance().logOut();
            Log.d("Debug", "User logged out");
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

        // API call for stopping current room listener
        stopListener = findViewById(R.id.stopListener);
        stopListener.setOnClickListener(v -> {
            // Stop listener function params:
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            RoomFirestore.getInstance().stopRoomListener();
        });

        // API call for getting all public rooms
        // Should implement so that it runs the function on refresh as I did not attach a listener to it
        getPublicRooms = findViewById(R.id.publicRooms);
        getPublicRooms.setOnClickListener(v -> {
            // Get public rooms function params:
            // callback: OnFirestoreCompleteCallback for handling onSuccess or onFailure

            RoomFirestore.getInstance().getAllPublicRooms(new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if(success){
                        //Public rooms retrieved successfully
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Public rooms retrieved successfully"
                        for (int i = 0; i < RoomFirestore.getInstance().publicRooms.size(); i++) {
                            Log.d("Debug", "Room Code: " + RoomFirestore.getInstance().publicRooms.get(i).getRoomCode());
                            Log.d("Debug", "Room Name: " + RoomFirestore.getInstance().publicRooms.get(i).getRoomName());
                            Log.d("Debug", "Room Current Size: " + RoomFirestore.getInstance().publicRooms.get(i).getRoomCurrentSize());
                        }
                    } else {
                        //Public rooms retrieval failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Failed to retrieve public rooms"
                    }
                }
            });
        });

        // API call for starting countdown
        startCountdown = findViewById(R.id.startCountdown);
        countdown = findViewById(R.id.countdown);
        startCountdown.setOnClickListener(v -> {
            // Start countdown
            Log.d("Debug", User.getRoom().getRoomCode());
            if (User.getRoom() != null){
                User.getRoom().startCountdown(countdown);
            } else {
                countdown.setText("No room joined");
            }
        });

        getAllCows = findViewById(R.id.getAllCows);
        cowImage = findViewById(R.id.cowImage);
        getAllCows.setOnClickListener(v -> {
            CowFirestore.getInstance().getAllCows(new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if(success){
                        //Cows retrieved successfully
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Cows retrieved successfully"
                        Log.d("Debug", CowFirestore.getListOfCows().toString());
                        int drawableId = getResources().getIdentifier(CowFirestore.getListOfCows().get(0).getImageName(), "drawable", getPackageName());
                        if (drawableId != 0){
                            Log.d("Debug", "Drawable found");
                            cowImage.setImageResource(drawableId);
                        } else {
                            Log.d("Debug", "Drawable not found");
                        }
                    } else {
                        //Cows retrieval failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Failed to retrieve cows"
                    }
                }
            });
        });

        addCow = findViewById(R.id.addCow);
        addCow.setOnClickListener(v -> {
            Random rand = new Random();
            int randomIndex = rand.nextInt(CowFirestore.getListOfCows().size());
            Cow cow = CowFirestore.getListOfCows().get(randomIndex);
            User.addCow(cow, new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if (success) {
                        //Cow added successfully
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Cow added"
                        Log.d("Debug", User.getUserCows().toString());
                    } else {
                        //Cow add failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Failed to add cow"
                    }
                }
            });
        });

        removeCow = findViewById(R.id.removeCow);
        removeCow.setOnClickListener(v -> {
            Cow cow = User.getUserCows().get(0);
            User.removeCow(cow, new OnFirestoreCompleteCallback() {
                @Override
                public void onFirestoreComplete(boolean success, String message) {
                    if (success) {
                        //Cow removed successfully
                        Log.d("Debug", message);
                        //Update UI/Go to next activity
                        //Messages are: "Cow removed"
                        Log.d("Debug", User.getUserCows().toString());
                    } else {
                        //Cow remove failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Failed to remove cow"
                    }
                }
            });
        });

        removeRandomCow = findViewById(R.id.removeRandomCow);
        removeRandomCow.setOnClickListener(v -> {
            User.removeRandomCow(new OnCowRemoval() {
                @Override
                public void onCowRemoved(boolean success, Cow cow) {
                    if (success) {
                        //Cow removed successfully
                        Log.d("Debug", "Cow removed");
                        //Update UI/Go to next activity
                        //Messages are: "Cow removed"
                        Log.d("Debug", "Cow removed: " + cow.getName());
                        Log.d("Debug", User.getUserCows().toString());
                    } else {
                        //Cow remove failed
                        Log.d("Debug", "Failed to remove cow");
                        //End loading screen
                        //Messages are: "Failed to remove cow"
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
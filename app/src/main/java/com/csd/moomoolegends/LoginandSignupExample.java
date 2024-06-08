package com.csd.moomoolegends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.SignUpLoginFirestore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginandSignupExample extends AppCompatActivity {

    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText usernameInput;
    private AppCompatButton login;
    private AppCompatButton signup;
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
                    } else {
                        //Signup failed
                        Log.d("Debug", message);
                        //End loading screen
                        //Messages are: "Username taken, please choose another one." or "Failed to register user, please try again."
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
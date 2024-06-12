package com.csd.moomoolegends;

import com.csd.moomoolegends.home.HomeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.csd.moomoolegends.home.HomeActivity;
import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.SignUpLoginFirestore;
import com.csd.moomoolegends.models.UserFirestore;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout usernameText;
    private TextInputLayout passwordText;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        usernameText = findViewById(R.id.sign_in_username);
        passwordText = findViewById(R.id.sign_in_password);
        ImageButton signIn=findViewById(R.id.sign_in_butt);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameText.getEditText().getText().toString().trim();
                String password = passwordText.getEditText().getText().toString().trim();
                Log.d("login", username+password);
                SignUpLoginFirestore.getInstance().login(LoginActivity.this, username, password, new OnFirestoreCompleteCallback() {
                    @Override
                    public void onFirestoreComplete(boolean success, String message) {
                        if (success){
                            Log.d("Login", message);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("Login", message);
                        }
                    }
                });
            }
        });

    }
}

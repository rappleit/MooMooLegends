package com.csd.moomoolegends;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.SignUpLoginFirestore;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout usernameText;
    private TextInputLayout emailText;
    private TextInputLayout passwordText;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);
        usernameText = findViewById(R.id.register_username);
        emailText = findViewById(R.id.register_email);
        passwordText = findViewById(R.id.register_password_confirm);
        ImageButton register = findViewById(R.id.register_butt);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameText.getEditText().getText().toString().trim();
                String password = passwordText.getEditText().getText().toString().trim();
                String email = emailText.getEditText().getText().toString().trim();

                Log.d("register", username+password+email);
                SignUpLoginFirestore.getInstance().signUp(RegisterActivity.this, username, email, password, new OnFirestoreCompleteCallback() {
                    @Override
                    public void onFirestoreComplete(boolean success, String message) {
                        if (success){
                            Log.d("register", message);
                        } else {
                            Log.d("register", message);
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}

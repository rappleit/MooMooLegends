package com.csd.moomoolegends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.csd.moomoolegends.multiplayer_pages.JoinPrivateRoomActivity;
import com.csd.moomoolegends.multiplayer_pages.MultiHomePageActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MainActivity.this, MultiHomePageActivity.class);
        startActivity(intent);
    }


}
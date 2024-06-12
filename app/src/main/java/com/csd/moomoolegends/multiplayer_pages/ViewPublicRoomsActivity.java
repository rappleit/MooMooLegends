package com.csd.moomoolegends.multiplayer_pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.csd.moomoolegends.R;
import com.csd.moomoolegends.adaptors.PublicRoomViewAdapter;
import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.Room;
import com.csd.moomoolegends.models.RoomFirestore;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ViewPublicRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_public_rooms);

        recyclerView = findViewById(R.id.public_rooms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("Debug", RoomFirestore.getInstance().publicRooms.get(0).getRoomName());
        recyclerView.setAdapter(new PublicRoomViewAdapter(RoomFirestore.getInstance().publicRooms));

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Debug", "Back button clicked.");
                Intent intent = new Intent(ViewPublicRoomsActivity.this, MultiHomePageActivity.class);
                startActivity(intent);
                Log.d("Debug", "Navigating back to MultiHomePageActivity.");
                finish();
            }
        });
    }
}
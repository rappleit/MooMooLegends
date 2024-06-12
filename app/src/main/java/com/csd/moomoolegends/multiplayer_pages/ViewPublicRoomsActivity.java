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
import com.csd.moomoolegends.models.Room;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ViewPublicRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton backButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_public_rooms);

        recyclerView = findViewById(R.id.public_rooms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        fetchPublicRooms();
    }

    private void fetchPublicRooms() {
        // Fetch the list of public rooms from Firebase
        db.collection("rooms").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Room> rooms = task.getResult().toObjects(Room.class);
                Log.d("Debug", "Successfully got rooms, Rooms: " + rooms.toString());
                // Set the adapter for the RecyclerView
                recyclerView.setAdapter(new PublicRoomViewAdapter(rooms));
            } else {
                Log.e("ViewPublicRoomsActivity", "Error getting documents: ", task.getException());
            }
        });
    }
}
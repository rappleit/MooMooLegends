package com.csd.moomoolegends.adaptors;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.csd.moomoolegends.R;
import com.csd.moomoolegends.models.OnFirestoreCompleteCallback;
import com.csd.moomoolegends.models.Room;
import com.csd.moomoolegends.models.RoomFirestore;
import com.csd.moomoolegends.multiplayer_pages.LobbyScreenActivity;

import java.util.List;

public class PublicRoomViewAdapter extends RecyclerView.Adapter<PublicRoomViewAdapter.ViewHolder> {

    private final List<Room> rooms;

    public PublicRoomViewAdapter(List<Room> rooms) {
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.bind(room.getRoomName(), room.getRoomCurrentSize(), room.getRoomCode());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView roomNameTextView;
        private final TextView numberOfPlayersTextView;
        private final CardView joinRoom;

        public ViewHolder(View view) {
            super(view);

            this.roomNameTextView = view.findViewById(R.id.textViewRoomName);
            this.numberOfPlayersTextView = view.findViewById(R.id.textViewNumPlayers);
            this.joinRoom = view.findViewById(R.id.cardView);
        }

        @SuppressLint("SetTextI18n")
        public void bind(String roomName, int currSize, String roomCode) {
            this.roomNameTextView.setText(roomName);
            this.numberOfPlayersTextView.setText(currSize + "/5"); // assuming maximum of 5 players
            this.joinRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoomFirestore.getInstance().joinRoom(roomCode, new OnFirestoreCompleteCallback() {
                        @Override
                        public void onFirestoreComplete(boolean success, String message) {
                            if (success) {
                                Log.d("Debug", message);
                                Intent intent = new Intent(v.getContext(), LobbyScreenActivity.class);
                                v.getContext().startActivity(intent);
                            } else {
                                Log.d("Debug", message);
                                Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}
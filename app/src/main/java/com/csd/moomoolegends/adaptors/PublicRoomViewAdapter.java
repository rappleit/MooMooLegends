package com.csd.moomoolegends.adaptors;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.csd.moomoolegends.R;
import com.csd.moomoolegends.models.Room;

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
        holder.bind(room.getRoomName(), room.getRoomCurrentSize());
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView roomNameTextView;
        private final TextView numberOfPlayersTextView;

        public ViewHolder(View view) {
            super(view);

            this.roomNameTextView = view.findViewById(R.id.textViewRoomName);
            this.numberOfPlayersTextView = view.findViewById(R.id.textViewNumPlayers);
        }

        @SuppressLint("SetTextI18n")
        public void bind(String roomName, int currSize) {
            this.roomNameTextView.setText(roomName);
            this.numberOfPlayersTextView.setText(currSize + "/5"); // assuming maximum of 5 players
        }
    }
}
package com.csd.moomoolegends.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.csd.moomoolegends.R;
import com.csd.moomoolegends.models.Room;

import java.util.List;

public class PublicRoomViewAdapter extends RecyclerView.Adapter<PublicRoomViewAdapter.ViewHolder> {

    private List<Room> rooms;

    public PublicRoomViewAdapter(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.roomNameTextView.setText(room.getRoomName());
        holder.numberOfPlayersTextView.setText(room.getRoomCurrentSize() + "/5"); // assuming maximum of 5 players
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView roomNameTextView;
        public TextView numberOfPlayersTextView;

        public ViewHolder(View view) {
            super(view);
            roomNameTextView = view.findViewById(R.id.room_name);
            numberOfPlayersTextView = view.findViewById(R.id.number_of_players);
        }
    }
}
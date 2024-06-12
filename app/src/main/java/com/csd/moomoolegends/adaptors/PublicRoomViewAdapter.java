package com.csd.moomoolegends.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.csd.moomoolegends.R;
import java.util.List;

public class PublicRoomViewAdapter extends RecyclerView.Adapter<PublicRoomViewAdapter.ViewHolder> {

    private List<String> roomNames; // replace with your actual data type

    public PublicRoomViewAdapter(List<String> roomNames) {
        this.roomNames = roomNames;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String roomName = roomNames.get(position);
        holder.roomNameTextView.setText(roomName);
    }

    @Override
    public int getItemCount() {
        return roomNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView roomNameTextView;

        public ViewHolder(View view) {
            super(view);
            roomNameTextView = view.findViewById(R.id.room_name);
        }
    }
}
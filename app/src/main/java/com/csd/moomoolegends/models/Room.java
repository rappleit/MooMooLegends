package com.csd.moomoolegends.models;

import android.os.CountDownTimer;

import androidx.appcompat.widget.AppCompatTextView;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Room {
    private String roomCode;
    private String roomName;
    private DocumentReference roomOwner;
    private float roomCarbonFootprint;
    private float roomWeeklyThreshold;
    private Date startDate;
    private Date endDate;
    private final int roomMaxSize = 5;
    private int roomCurrentSize;
    private ArrayList<DocumentReference> roomMembers;
    private boolean roomIsPrivate;
    private boolean roomIsFull;

    private Room(String roomCode, String roomName, DocumentReference roomOwner, float roomCarbonFootprint, float roomWeeklyThreshold, Date startDate, Date endDate, int roomCurrentSize, ArrayList<DocumentReference> roomMembers, boolean roomIsPrivate, boolean roomIsFull) {
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.roomOwner = roomOwner;
        this.roomCarbonFootprint = roomCarbonFootprint;
        this.roomWeeklyThreshold = roomWeeklyThreshold;
        this.startDate = startDate;
        this.endDate = endDate;
        this.roomCurrentSize = roomCurrentSize;
        this.roomMembers = roomMembers;
        this.roomIsPrivate = roomIsPrivate;
        this.roomIsFull = roomIsFull;
    }

    public Room(String roomCode, String roomName, int roomCurrentSize) {
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.roomCurrentSize = roomCurrentSize;
    }

    public Room(){};

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public DocumentReference getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(DocumentReference roomOwner) {
        this.roomOwner = roomOwner;
    }

    public int getRoomCurrentSize() {
        return roomCurrentSize;
    }

    public void setRoomCurrentSize(int roomCurrentSize) {
        this.roomCurrentSize = roomCurrentSize;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public float getRoomCarbonFootprint() {
        return roomCarbonFootprint;
    }

    public void setRoomCarbonFootprint(float roomCarbonFootprint) {
        this.roomCarbonFootprint = roomCarbonFootprint;
    }

    public float getRoomWeeklyThreshold() {
        return roomWeeklyThreshold;
    }

    public void setRoomWeeklyThreshold(float roomWeeklyThreshold) {
        this.roomWeeklyThreshold = roomWeeklyThreshold;
    }

    public ArrayList<DocumentReference> getRoomMembers() {
        return roomMembers;
    }

    public void setRoomMembers(ArrayList<DocumentReference> roomMembers) {
        this.roomMembers = roomMembers;
    }

    public boolean getRoomIsFull() {
        return roomIsFull;
    }

    public void setRoomIsFull(boolean roomIsFull) {
        this.roomIsFull = roomIsFull;
    }

    public int getRoomMaxSize() {
        return roomMaxSize;
    }

    public void setRoomIsPrivate(boolean roomIsPrivate) {
        this.roomIsPrivate = roomIsPrivate;
    }

    public boolean getRoomIsPrivate() {
        return roomIsPrivate;
    }

    public String getDifferenceString() {
        long diffInMillies = Math.abs(endDate.getTime() - new Date().getTime());
        long days = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        diffInMillies -= TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);
        long hours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        diffInMillies -= TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);
        long minutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return days + " days " + hours + " hours " + minutes + " minutes";
    }

    public long getTimeDifference() {
        return Math.abs(endDate.getTime() - new Date().getTime());
    }

    public void startCountdown(AppCompatTextView countdown) {
        new CountDownTimer(User.getRoom().getTimeDifference(), 60000) { // Update every minute

            public void onTick(long millisUntilFinished) {
                countdown.setText(getDifferenceString());
            }

            public void onFinish() {
                countdown.setText("done!");
            }
        }.start();
    }

    public static class RoomBuilder {
        private String roomCode;
        private String roomName;
        private DocumentReference roomOwner;
        private float roomCarbonFootprint;
        private float roomWeeklyThreshold;
        private Date startDate;
        private Date endDate;
        private int roomCurrentSize;
        private ArrayList<DocumentReference> roomMembers;
        private boolean roomIsPrivate;
        private boolean roomIsFull;

        public RoomBuilder() {}

        public RoomBuilder setRoomCode(String roomCode) {
            this.roomCode = roomCode;
            return this;
        }

        public RoomBuilder setRoomName(String roomName) {
            this.roomName = roomName;
            return this;
        }

        public RoomBuilder setRoomOwner(DocumentReference roomOwner) {
            this.roomOwner = roomOwner;
            return this;
        }

        public RoomBuilder setRoomCarbonFootprint(float roomCarbonFootprint) {
            this.roomCarbonFootprint = roomCarbonFootprint;
            return this;
        }

        public RoomBuilder setRoomWeeklyThreshold(float roomWeeklyThreshold) {
            this.roomWeeklyThreshold = roomWeeklyThreshold;
            return this;
        }

        public RoomBuilder setStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public RoomBuilder setEndDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public RoomBuilder setRoomCurrentSize(int roomCurrentSize) {
            this.roomCurrentSize = roomCurrentSize;
            return this;
        }

        public RoomBuilder setRoomMembers(ArrayList<DocumentReference> roomMembers) {
            this.roomMembers = roomMembers;
            return this;
        }

        public RoomBuilder setRoomIsPrivate(boolean roomIsPrivate) {
            this.roomIsPrivate = roomIsPrivate;
            return this;
        }

        public RoomBuilder setRoomIsFull(boolean roomIsFull) {
            this.roomIsFull = roomIsFull;
            return this;
        }

        public Room build() {
            return new Room(roomCode, roomName, roomOwner, roomCarbonFootprint, roomWeeklyThreshold, startDate, endDate, roomCurrentSize, roomMembers, roomIsPrivate, roomIsFull);
        }
    }
}

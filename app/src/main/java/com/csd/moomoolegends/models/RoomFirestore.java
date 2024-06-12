package com.csd.moomoolegends.models;

import android.util.Log;

import com.csd.moomoolegends.multiplayer_pages.LobbyScreenActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RoomFirestore extends FirestoreInstance{
    private FirebaseFirestore db = getFirestore();
    private static RoomFirestore instance = null;
    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private ListenerRegistration roomListener;
    public ArrayList<Room> publicRooms = new ArrayList<>();
    private static OnRoomListenerChange onRoomListenerChange;


    public RoomFirestore() {
    }

    public static RoomFirestore getInstance(){
        if (instance == null){
            instance = new RoomFirestore();
        }
        return instance;
    }

    public static void setOnRoomListenerChange(OnRoomListenerChange listener){
        RoomFirestore.onRoomListenerChange = listener;
    }

    public static void unregisterRoomListener(){
        RoomFirestore.onRoomListenerChange = null;
    }

    private String generateRandomCode(){
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            builder.append(ALPHANUMERIC_STRING.charAt(random.nextInt(ALPHANUMERIC_STRING.length())));
        }
        return builder.toString();
    }

    public void createRoom(OnFirestoreCompleteCallback callback){

        String roomCode = generateRandomCode();
        DocumentReference docRef = db.collection("rooms").document(roomCode);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // If the room code already exists, recursively call the method to generate a new one
                    createRoom(callback);
                } else {
                    // If the room code does not exist, create the room
                    Room room = new Room.RoomBuilder().setRoomCode(roomCode)
                                    .setRoomName(User.getUsername() + " Room")
                                    .setRoomOwner(User.getUserDoc())
                                    .setRoomCarbonFootprint((float) 0)
                                    .setRoomWeeklyThreshold(User.getWeeklyThreshold())
                                    .setRoomCurrentSize(1)
                                    .setRoomIsFull(false)
                                    .setRoomIsPrivate(false)
                                    .setRoomMembers(new ArrayList<>(Collections.singletonList(User.getUserDoc())))
                                    .build();

                    docRef.set(room)
                            .addOnSuccessListener(aVoid -> {
                                updateUserDoc(roomCode, callback);
                            })
                            .addOnFailureListener(e -> {
                                callback.onFirestoreComplete(false, "Failed to create room");
                            });
                }
            } else {
                callback.onFirestoreComplete(false, "Failed to check room code");
            }
        });
    }

    public void startRoom(OnFirestoreCompleteCallback callback){
        DocumentReference docRef = db.collection("rooms").document(User.getRoomCode());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Date startDate = new Date();
                Date endDate = addOneWeek(startDate);

                Map<String, Object> dateMap = new HashMap<>();
                dateMap.put("startDate", startDate);
                dateMap.put("endDate", endDate);

                if (User.getRoom() != null){

                    docRef.update("roomIsFull", true).addOnSuccessListener(aVoid -> {
                        docRef.update(dateMap).addOnSuccessListener(aVoid1 -> {
                            User.getRoom().setRoomIsFull(true);
                            User.getRoom().setStartDate(startDate);
                            User.getRoom().setEndDate(endDate);
                            callback.onFirestoreComplete(true, "Room started successfully");
                        }).addOnFailureListener(e -> {
                            callback.onFirestoreComplete(false, "Failed to start room");
                        });
                    }).addOnFailureListener(e -> {
                        callback.onFirestoreComplete(false, "Failed to start room");
                    });
                }
            } else {
                callback.onFirestoreComplete(false, "Room does not exist");
            }
        });
    }

    public void joinRoom(String roomCode, OnFirestoreCompleteCallback callback){
        DocumentReference docRef = db.collection("rooms").document(roomCode);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Room room = task.getResult().toObject(Room.class);
                    if (room != null && !room.getRoomIsFull()){
                        room.getRoomMembers().add(User.getUserDoc());
                        room.setRoomCurrentSize(room.getRoomCurrentSize() + 1);

                        if (room.getRoomCurrentSize() == room.getRoomMaxSize()) {
                            room.setRoomIsFull(true);
                            docRef.update("roomIsFull", true).addOnFailureListener(e -> {
                                Log.d("Debug", "Failed to update roomIsFull in Firestore");
                                callback.onFirestoreComplete(false, "Failed to join room");
                            });
                        }

                        docRef.update("roomMembers", room.getRoomMembers()).addOnSuccessListener(aVoid -> {
                            docRef.update("roomCurrentSize", room.getRoomCurrentSize()).addOnSuccessListener(aVoid1 -> {
                                updateUserDoc(roomCode, callback);
                            }).addOnFailureListener(e -> {
                                Log.d("Debug", "Failed to update roomCurrentSize in Firestore");
                                callback.onFirestoreComplete(false, "Failed to join room");
                            });
                        }).addOnFailureListener(e -> {
                            Log.d("Debug", "Failed to update roomMembers in Firestore");
                            callback.onFirestoreComplete(false, "Failed to join room");
                        });
                    } else {
                        callback.onFirestoreComplete(false, "Room already started");
                    }
                } else {
                    callback.onFirestoreComplete(false, "Room does not exist");
                }
            } else {
                callback.onFirestoreComplete(false, "Failed to check room code");
            }
        });
    }

    public void leaveRoom(OnFirestoreCompleteCallback callback){
        DocumentReference docRef = db.collection("rooms").document(User.getRoomCode());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Room room = User.getRoom();
                if (room != null){
                    handleRoomLeave(room, docRef, callback);
                }
            } else {
                callback.onFirestoreComplete(false, "Room does not exist");
            }
        });
    }

    private void handleRoomLeave(Room room, DocumentReference docRef, OnFirestoreCompleteCallback callback) {
        room.getRoomMembers().remove(User.getUserDoc());
        room.setRoomCurrentSize(room.getRoomCurrentSize() - 1);
        stopRoomListener();

        if (room.getRoomCurrentSize() == 0) {
            deleteRoom(docRef, callback);
        } else {
            updateRoom(room, docRef, callback);
        }
    }

    private void deleteRoom(DocumentReference docRef, OnFirestoreCompleteCallback callback) {
        docRef.delete().addOnSuccessListener(aVoid -> {
            resetUserRoom(callback);
        }).addOnFailureListener(e -> {
            callback.onFirestoreComplete(false, "Failed to delete room in Firestore");
        });
    }

    private void updateRoom(Room room, DocumentReference docRef, OnFirestoreCompleteCallback callback) {

        if (room.getRoomOwner().getId().equals(User.getUserId())) {
            room.setRoomOwner(room.getRoomMembers().get(0));
            docRef.update("roomOwner", room.getRoomOwner()).addOnFailureListener(e -> {
                callback.onFirestoreComplete(false, "Failed to update roomOwner in Firestore");
            });
        }

        docRef.update("roomMembers", room.getRoomMembers()).addOnSuccessListener(aVoid -> {
            docRef.update("roomCurrentSize", room.getRoomCurrentSize()).addOnSuccessListener(aVoid1 -> {
                resetUserRoom(callback);
            }).addOnFailureListener(e -> {
                callback.onFirestoreComplete(false, "Failed to update roomCurrentSize in Firestore");
            });
        }).addOnFailureListener(e -> {
            callback.onFirestoreComplete(false, "Failed to update roomMembers in Firestore");
        });
    }

    private void resetUserRoom(OnFirestoreCompleteCallback callback) {
        UserFirestore.getInstance().editRoomCode(User.getUserId(), "", new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success) {
                    User.setRoom(null);
                    User.setInRoom(false);
                    User.setRoomCode(null);
                    callback.onFirestoreComplete(true, "Room left successfully");
                } else {
                    initializeRoomListener(User.getRoomCode(), new OnFirestoreCompleteCallback() {
                        @Override
                        public void onFirestoreComplete(boolean success, String message) {
                            Log.d("Debug", message);
                            callback.onFirestoreComplete(false, "Failed to leave room");
                        }
                    });
                }
            }
        });
    }

    private void updateUserDoc(String roomCode, OnFirestoreCompleteCallback callback){
        stopRoomListener();
        UserFirestore.getInstance().editRoomCode(User.getUserId(), roomCode, new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success) {
                    Log.d("Debug", message);
                    initializeRoomListener(roomCode, new OnFirestoreCompleteCallback() {
                        @Override
                        public void onFirestoreComplete(boolean success, String message) {
                            if (success){
                                Log.d("Debug", message);
                                User.setInRoom(true);
                                User.setRoomCode(roomCode);
                                callback.onFirestoreComplete(true, "Room created/joined successfully");
                            } else {
                                Log.d("Debug", message);
                                callback.onFirestoreComplete(false, "Failed to create/join room.");
                            }
                        }
                    });
                } else {
                    Log.d("Debug", message);
                    callback.onFirestoreComplete(false, "Failed to create/join room.");
                }
            }
        });
    }

    public void toggleRoomPrivacy(String roomCode, boolean isPrivate, OnFirestoreCompleteCallback callback){
        stopRoomListener();
        db.collection("rooms").document(roomCode).update("roomIsPrivate", isPrivate)
                .addOnSuccessListener(aVoid -> {
                    User.getRoom().setRoomIsPrivate(isPrivate);
                    callback.onFirestoreComplete(true, "Room privacy updated successfully");
                })
                .addOnFailureListener(e -> {
                    callback.onFirestoreComplete(false, "Failed to update room privacy");
                });
        initializeRoomListener(roomCode, (success, message) -> Log.d("Debug", message));
    }

    public void getAllPublicRooms(OnFirestoreCompleteCallback callback){
        db.collection("rooms").whereEqualTo("roomIsPrivate", false).whereEqualTo("roomIsFull", false).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        publicRooms.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String roomCode = document.getId();
                            Number roomSize = (Number) document.get("roomCurrentSize");
                            if (roomSize == null) {
                                continue;
                            }
                            int roomCurrentSize = roomSize.intValue();
                            String roomName = document.getString("roomName");
                            Room room = new Room(roomCode, roomName, roomCurrentSize);
                            publicRooms.add(room);
                        }
                        Log.d("Debug", publicRooms.toString());
                        callback.onFirestoreComplete(true, "Public rooms retrieved successfully");
                    } else {
                        callback.onFirestoreComplete(false, "Failed to retrieve public rooms");
                    }
                });
    }

    public Date addOneWeek(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 7); // adding 7 days
        return calendar.getTime(); // new date
    }

     public void initializeRoomListener(String roomCode, OnFirestoreCompleteCallback callback) {
         Log.d("Debug", "Initializing room listener");
         roomListener = db.collection("rooms").document(roomCode).addSnapshotListener((value, error) -> {
             if (error != null) {
                 Log.w("Debug", "Listen failed.", error);
                 callback.onFirestoreComplete(false, "Failed to listen for room members");
                 return;
             }

             if (value != null && value.exists()) {
                 Room room = value.toObject(Room.class);
                 User.setRoom(room);
                 Log.d("Debug", "Listen started");
                 if (onRoomListenerChange != null){
                     onRoomListenerChange.onChange();
                 }
                 callback.onFirestoreComplete(true, "Room listen successful");
             } else {
                 Log.d("Debug", "Current data: null");
                 callback.onFirestoreComplete(false, "Room does not exist");
             }
         });
     }

     public void stopRoomListener(){
        if (roomListener != null){
            roomListener.remove();
            Log.d("Debug", "Listen stopped");
            roomListener = null;
        }
     }

}

package com.csd.moomoolegends.models;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class RoomFirestore extends FirestoreInstance{
    private FirebaseFirestore db = getFirestore();
    private static RoomFirestore instance = null;
    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    public RoomFirestore() {
    }

    public static RoomFirestore getInstance(){
        if (instance == null){
            instance = new RoomFirestore();
        }
        return instance;
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
                                    .setStartDate(new Date())
                                    .setEndDate(addOneWeek(new Date()))
                                    .build();

                    docRef.set(room)
                            .addOnSuccessListener(aVoid -> {
                                updateUserDoc(room, roomCode, callback);
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

    public void joinRoom(String roomCode, OnFirestoreCompleteCallback callback){
        DocumentReference docRef = db.collection("rooms").document(roomCode);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Room room = task.getResult().toObject(Room.class);
                    if (room != null){
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
                                updateUserDoc(room, roomCode, callback);
                            }).addOnFailureListener(e -> {
                                Log.d("Debug", "Failed to update roomCurrentSize in Firestore");
                                callback.onFirestoreComplete(false, "Failed to join room");
                            });
                        }).addOnFailureListener(e -> {
                            Log.d("Debug", "Failed to update roomMembers in Firestore");
                            callback.onFirestoreComplete(false, "Failed to join room");
                        });
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
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    Room room = User.getRoom();
                    if (room != null){
                        room.getRoomMembers().remove(User.getUserDoc());
                        room.setRoomCurrentSize(room.getRoomCurrentSize() - 1);

                        if (room.getRoomCurrentSize() == 0) {
                            docRef.delete().addOnSuccessListener(aVoid -> {
                                User.setRoom(null);
                                User.setInRoom(false);
                                User.setRoomCode(null);
                                callback.onFirestoreComplete(true, "Room deleted successfully");
                            }).addOnFailureListener(e -> {
                                Log.d("Debug", "Failed to delete room in Firestore");
                                callback.onFirestoreComplete(false, "Failed to leave room");
                            });
                        } else {
                            if (room.getRoomOwner().getId().equals(User.getUserId())) {
                                room.setRoomOwner(room.getRoomMembers().get(0));
                                docRef.update("roomOwner", room.getRoomOwner()).addOnFailureListener(e -> {
                                    Log.d("Debug", "Failed to update roomOwner in Firestore");
                                    callback.onFirestoreComplete(false, "Failed to leave room");
                                });
                            }

                            docRef.update("roomMembers", room.getRoomMembers()).addOnSuccessListener(aVoid -> {
                                docRef.update("roomCurrentSize", room.getRoomCurrentSize()).addOnSuccessListener(aVoid1 -> {
                                    UserFirestore.getInstance().editRoomCode(User.getUserId(), "", new OnFirestoreCompleteCallback() {
                                        @Override
                                        public void onFirestoreComplete(boolean success, String message) {
                                            if (success) {
                                                Log.d("Debug", message);
                                                User.setRoom(null);
                                                User.setInRoom(false);
                                                User.setRoomCode(null);
                                                callback.onFirestoreComplete(true, "Room left successfully");
                                            } else {
                                                Log.d("Debug", message);
                                                callback.onFirestoreComplete(false, "Failed to leave room");
                                            }
                                        }
                                    });
                                }).addOnFailureListener(e -> {
                                    Log.d("Debug", "Failed to update roomCurrentSize in Firestore");
                                    callback.onFirestoreComplete(false, "Failed to leave room");
                                });
                            }).addOnFailureListener(e -> {
                                Log.d("Debug", "Failed to update roomMembers in Firestore");
                                callback.onFirestoreComplete(false, "Failed to leave room");
                            });
                        }
                    }
                } else {
                    callback.onFirestoreComplete(false, "Room does not exist");
                }
            } else {
                callback.onFirestoreComplete(false, "Failed to check room code");
            }
        });
    }

    private void updateUserDoc(Room room, String roomCode, OnFirestoreCompleteCallback callback){
        UserFirestore.getInstance().editRoomCode(User.getUserId(), roomCode, new OnFirestoreCompleteCallback() {
            @Override
            public void onFirestoreComplete(boolean success, String message) {
                if (success) {
                    Log.d("Debug", message);
                    User.setRoom(room);
                    User.setInRoom(true);
                    User.setRoomCode(roomCode);
                    callback.onFirestoreComplete(true, "Room created/joined successfully");
                } else {
                    Log.d("Debug", message);
                    callback.onFirestoreComplete(false, "Failed to create/join room.");
                }
            }
        });
    }

    public void toggleRoomPrivacy(String roomCode, boolean isPrivate, OnFirestoreCompleteCallback callback){
        db.collection("rooms").document(roomCode).update("roomIsPrivate", isPrivate)
                .addOnSuccessListener(aVoid -> {
                    User.getRoom().setRoomIsPrivate(isPrivate);
                    callback.onFirestoreComplete(true, "Room privacy updated successfully");
                })
                .addOnFailureListener(e -> {
                    callback.onFirestoreComplete(false, "Failed to update room privacy");
                });
    }

    public void getRoom(String roomCode, OnFirestoreCompleteCallback callback){
        db.collection("rooms").document(roomCode).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        Room room = documentSnapshot.toObject(Room.class);
                        User.setRoom(room);
                        callback.onFirestoreComplete(true, "Room retrieved successfully");
                    }else{
                        callback.onFirestoreComplete(false, "Room does not exist");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFirestoreComplete(false, "Failed to retrieve room");
                });
    }

    public Date addOneWeek(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 7); // adding 7 days
        return calendar.getTime(); // new date
    }


}

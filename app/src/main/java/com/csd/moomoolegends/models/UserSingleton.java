package com.csd.moomoolegends.models;

public class UserSingleton {
    private static UserSingleton instance = null;
    private User user;

    private UserSingleton() {
    }

    public static UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

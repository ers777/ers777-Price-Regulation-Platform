package com.example.android_mas.users;

public class User {
    public String uid,username, profileImage;

    public User(String username, String profileImage,String uid) {
        this.username = username;
        this.uid = uid;
        this.profileImage = profileImage;
    }
}

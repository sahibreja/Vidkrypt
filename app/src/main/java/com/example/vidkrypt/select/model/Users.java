package com.example.vidkrypt.select.model;

public class Users {
    private String userId;
    private String userName;
    private String userEmail;
    private String userPass;
    private String userProfileImage;

    public Users() {
    }

    public Users(String userId, String userName, String userEmail, String userPass, String userProfileImage) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPass = userPass;
        this.userProfileImage = userProfileImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }
}

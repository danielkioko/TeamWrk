package com.versusdeveloper.blogr.Accounts;

public class User {

    private String userId;
    private String displayName;
    private String email;
    private String image;

    public User(String uid, String displayName, String email) {
    }

    public User(String userId, String displayName, String email, String image) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

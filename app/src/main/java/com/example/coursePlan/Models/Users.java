package com.example.coursePlan.Models;

public class Users {

    String name,status,thumb_image,userId,stts;


    public Users() {
    }

    public Users(String name, String thumb_image, String userId) {
        this.name = name;
        this.thumb_image = thumb_image;
        this.userId = userId;
    }

    public String getStts() {
        return stts;
    }

    public void setStts(String stts) {
        this.stts = stts;
    }

    public Users(String name, String thumb_image, String userId, String stts) {
        this.name = name;
        this.thumb_image = thumb_image;
        this.userId = userId;
        this.stts = stts;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}

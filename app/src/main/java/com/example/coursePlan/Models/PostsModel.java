package com.example.coursePlan.Models;

public class PostsModel {

    String questionId;
    String ownerId;
    String questionTime;
    String title;
    String description;
    String ownerName;
    String ownerProfile;
    String answerId;
    String status,likes;

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public PostsModel(String questionId, String ownerId, String questionTime, String title, String description) {
        this.questionId = questionId;
        this.ownerId = ownerId;
        this.questionTime = questionTime;
        this.title = title;
        this.description = description;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public PostsModel() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PostsModel(String questionId, String ownerId, String questionTime, String title, String description, String ownerName, String ownerProfile) {
        this.questionId = questionId;
        this.ownerId = ownerId;
        this.questionTime = questionTime;
        this.title = title;
        this.description = description;
        this.ownerName = ownerName;
        this.ownerProfile = ownerProfile;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerProfile() {
        return ownerProfile;
    }

    public void setOwnerProfile(String ownerProfile) {
        this.ownerProfile = ownerProfile;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(String questionTime) {
        this.questionTime = questionTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

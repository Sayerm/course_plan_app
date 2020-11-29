package com.example.coursePlan.Models;

public class ResourceModel {

    String userId,postId,description,title,time,fileCount,status,subject;

    public ResourceModel() {
    }

    public ResourceModel(String userId, String postId, String description, String title, String time, String fileCount) {
        this.userId = userId;
        this.postId = postId;
        this.description = description;
        this.title = title;
        this.time = time;
        this.fileCount = fileCount;
    }

    public ResourceModel(String userId, String postId, String description, String title, String time, String fileCount, String subject) {
        this.userId = userId;
        this.postId = postId;
        this.description = description;
        this.title = title;
        this.time = time;
        this.fileCount = fileCount;
        this.subject=subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFileCount() {
        return fileCount;
    }

    public void setFileCount(String fileCount) {
        this.fileCount = fileCount;
    }
}

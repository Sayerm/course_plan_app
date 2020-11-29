package com.example.coursePlan.Models;

public class MyAnsModel {

    String questionId;
    String questionOwnerId;
    String ansId;
    String ansOwnerId;
    String questionTime;
    String description;
    String ownerName;
    String ownerProfile;
    String questionCount;
    String answer;
    String title;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public MyAnsModel(String questionId, String questionOwnerId, String ansId, String ansOwnerId, String questionTime, String description, String title) {
        this.questionId = questionId;
        this.questionOwnerId = questionOwnerId;
        this.ansId = ansId;
        this.ansOwnerId = ansOwnerId;
        this.questionTime = questionTime;
        this.description = description;
        this.title = title;
    }

    public String getQuestionOwnerId() {
        return questionOwnerId;
    }

    public void setQuestionOwnerId(String questionOwnerId) {
        this.questionOwnerId = questionOwnerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnsId() {
        return ansId;
    }

    public void setAnsId(String ansId) {
        this.ansId = ansId;
    }

    public MyAnsModel(String questionId, String questionOwnerId, String ansId, String ansOwnerId, String questionTime, String description) {
        this.questionId = questionId;
        this.questionOwnerId = questionOwnerId;
        this.ansId = ansId;
        this.ansOwnerId = ansOwnerId;
        this.questionTime = questionTime;
        this.description = description;
    }

    public String getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(String questionCount) {
        this.questionCount = questionCount;
    }

    public MyAnsModel(String questionId, String ansOwnerId, String questionTime, String description) {
        this.questionId = questionId;
        this.ansOwnerId = ansOwnerId;
        this.questionTime = questionTime;
        this.description = description;
    }

    public MyAnsModel() {
    }

    public MyAnsModel(String questionId, String ansOwnerId, String questionTime, String description, String ownerProfile) {
        this.questionId = questionId;
        this.ansOwnerId = ansOwnerId;
        this.questionTime = questionTime;
        this.description = description;
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

    public String getAnsOwnerId() {
        return ansOwnerId;
    }

    public void setAnsOwnerId(String ansOwnerId) {
        this.ansOwnerId = ansOwnerId;
    }

    public String getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(String questionTime) {
        this.questionTime = questionTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

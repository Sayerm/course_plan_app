package com.example.coursePlan.Models;

public class AnswersModel {
    String answerId,answerOwnerId,questionId,questionOwnerId,answerTime,description,images,likes;

    public AnswersModel(String answerId, String answerOwnerId, String questionId, String questionOwnerId, String answerTime, String description, String images, String likes) {
        this.answerId = answerId;
        this.answerOwnerId = answerOwnerId;
        this.questionId = questionId;
        this.questionOwnerId = questionOwnerId;
        this.answerTime = answerTime;
        this.description = description;
        this.images = images;
        this.likes = likes;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getAnswerOwnerId() {
        return answerOwnerId;
    }

    public void setAnswerOwnerId(String answerOwnerId) {
        this.answerOwnerId = answerOwnerId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionOwnerId() {
        return questionOwnerId;
    }

    public void setQuestionOwnerId(String questionOwnerId) {
        this.questionOwnerId = questionOwnerId;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}

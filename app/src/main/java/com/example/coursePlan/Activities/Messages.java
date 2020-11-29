package com.example.coursePlan.Activities;

public class Messages {

    String message,type,from;
    String time;
    String seen;

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public Messages(String message, String from, String time, String seen) {
        this.message = message;
        this.from = from;
        this.time = time;
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}

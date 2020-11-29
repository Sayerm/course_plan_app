package com.example.coursePlan.Activities;

public class Conversation {

    String seen;
    long lastSeen;
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Conversation(String seen, long lastSeen, String key) {
        this.seen = seen;
        this.lastSeen = lastSeen;
        this.key = key;
    }

    public Conversation() {
    }

    public Conversation(String seen, long lastSeen) {
        this.seen = seen;
        this.lastSeen = lastSeen;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}

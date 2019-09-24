package com.selfcoders.matterbukkit.matterbridgeapi;

public class Message {
    private String event;
    private String text;
    private String username;

    public Message(String username, String text) {
        this.username = username;
        this.text = text;
    }

    public String getEvent() {
        return event;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }
}

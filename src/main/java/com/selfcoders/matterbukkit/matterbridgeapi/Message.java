package com.selfcoders.matterbukkit.matterbridgeapi;

public class Message {
    private String event;
    private String text;
    private String username;
    private String avatar;

    public Message(String username, String text, String avatar) {
        this.username = username;
        this.text = text;
        this.avatar = avatar;
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

    public String getAvatar() {
        return avatar;
    }
}

package com.selfcoders.matterbukkit;

public class Message {
    private String event;
    private String text;
    private String username;
    private String avatar;
    private String gateway;

    public Message(String username, String text, String avatar, String gateway) {
        this.username = username;
        this.text = text;
        this.avatar = avatar;
        this.gateway = gateway;
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

    public String getGateway() {
        return gateway;
    }
}

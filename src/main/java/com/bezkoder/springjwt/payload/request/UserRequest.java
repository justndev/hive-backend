package com.bezkoder.springjwt.payload.request;

public class UserRequest {

    private String username;

    private Long sender;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }
}

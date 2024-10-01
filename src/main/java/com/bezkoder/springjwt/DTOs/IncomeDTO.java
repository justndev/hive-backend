package com.bezkoder.springjwt.DTOs;

public class IncomeDTO {
    String time;
    String text;
    Long id;
    String username;

    public IncomeDTO(Long id, String text, String time, String username) {
        this.id = id;
        this.text = text;
        this.time = time;
        this.username = username;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
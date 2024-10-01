package com.bezkoder.springjwt.models;

import jakarta.persistence.*;


@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    private Long sender;

    private Long receiver;

    @Column(nullable = false)
    private String time;

    public Message(String text, Long sender, Long receiver, String time) {
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
    }

    public Message(String text, Long sender, Long receiver) {
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Message() {
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String  time) {
        this.time = time;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
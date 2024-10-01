package com.bezkoder.springjwt.models;

import jakarta.persistence.*;


@Entity
@Table(name = "requests")

public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sender;
    private Long receiver;

    public Request(Long receiver, Long sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

    public Request() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }
}
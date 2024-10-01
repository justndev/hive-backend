package com.bezkoder.springjwt.models;

import jakarta.persistence.*;


@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lastMessage;

    private Long sender;

    private Long receiver;

    public Chat(Long id, Long lastMessage, Long sender, Long receiver) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Chat(Long lastMessage, Long sender, Long receiver) {
        this.lastMessage = lastMessage;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Chat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Long lastMessage) {
        this.lastMessage = lastMessage;
    }
}
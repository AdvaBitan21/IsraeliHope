package com.technion.android.israelihope.Objects;

import com.google.firebase.Timestamp;

public class Chat {

    public enum ChatType {
        TEXT,
        CHALLENGE
    }

    private ChatType type;
    private String sender;
    private String receiver;
    private Timestamp messageTime;

    private String message;         //type TEXT
    private Challenge challenge;    //type CHALLENGE

    public Chat() {
    }

    public Chat(String sender, String receiver, Timestamp messageTime, String message) {
        this.type = ChatType.TEXT;
        this.sender = sender;
        this.receiver = receiver;
        this.messageTime = messageTime;
        this.message = message;
        this.challenge = null;
    }

    public Chat(String sender, String receiver, Timestamp messageTime, Challenge challenge) {
        this.type = ChatType.CHALLENGE;
        this.sender = sender;
        this.receiver = receiver;
        this.messageTime = messageTime;
        this.message = "";
        this.challenge = challenge;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Timestamp messageTime) {
        this.messageTime = messageTime;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}

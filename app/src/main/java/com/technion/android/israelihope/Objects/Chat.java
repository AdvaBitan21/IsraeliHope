package com.technion.android.israelihope.Objects;


import com.google.firebase.Timestamp;

public class Chat {
    private String sender;
    private String receiver;
    private String message;
    private Timestamp messageTime; //TODO change to Date

    public Chat(String sender, String receiver, String message, Timestamp messageTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.messageTime = messageTime;
    }

    public Chat(){
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
}

package com.technion.android.israelihope.Objects;

import android.net.Uri;

import com.google.firebase.Timestamp;

public class Chat {

    public enum ChatType {
        TEXT,
        CHALLENGE,
        PICTURE
    }

    private ChatType type;
    private String sender;
    private String receiver;
    private Timestamp messageTime;

    private String message;         //type TEXT
    private Challenge challenge;    //type CHALLENGE
    private String pictureUri;      //type PICTURE

    public Chat() {
    }

    public Chat(String sender, String receiver, Timestamp messageTime, String stringContent) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageTime = messageTime;
        this.challenge = null;

        //type TEXT
        if(stringContent.startsWith("MESSAGE:")){
            this.type = ChatType.TEXT;
            this.message = stringContent.substring(8);
            this.pictureUri = "";
        }

        //type PICTURE
        if(stringContent.startsWith("PICTURE:")){
            this.type = ChatType.PICTURE;
            this.message = "";
            this.pictureUri = stringContent.substring(8);
        }

    }

    //type CHALLENGE
    public Chat(String sender, String receiver, Timestamp messageTime, Challenge challenge) {
        this.type = ChatType.CHALLENGE;
        this.sender = sender;
        this.receiver = receiver;
        this.messageTime = messageTime;
        this.message = "";
        this.challenge = challenge;
        this.pictureUri = null;
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

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }
}

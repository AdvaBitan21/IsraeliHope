package com.technion.android.israelihope.Objects;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class Conversation {

    // The sender is the user whose "Conversations" collection in firebase includes this object.
    private String email;
    private Timestamp lastMessageTime;
    private String lastMessageId;
    private int unseenCount;

    public Conversation() {
    }

    public Conversation(String email, Timestamp lastMessageTime, String lastMessageId) {
        this.email = email;
        this.lastMessageTime = lastMessageTime;
        this.lastMessageId = lastMessageId;
        this.unseenCount = 0;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Timestamp lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnseenCount() {
        return unseenCount;
    }

    public void incrementUnseen() {
        this.unseenCount++;
    }

    public void decreaseUnseenCount() {
        this.unseenCount--;
    }


    public void clearUnseen() {
        this.unseenCount = 0;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(email, that.email);
    }

}

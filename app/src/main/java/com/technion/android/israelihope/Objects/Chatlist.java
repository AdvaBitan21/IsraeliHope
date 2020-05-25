package com.technion.android.israelihope.Objects;

import java.util.ArrayList;

public class Chatlist {
    public String email;
    public ArrayList<String> emails;

    public Chatlist(String email, ArrayList<String> emails) {
        this.email = email;
        this.emails = emails;
    }

    public Chatlist() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getEmails() {
        return emails;
    }

    public void addToEmails(String email_to_add) {
        this.emails.add(email_to_add);
    }

}

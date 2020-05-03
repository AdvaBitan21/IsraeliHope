package com.technion.android.israelihope.Objects;

import android.net.Uri;

import com.technion.android.israelihope.Utils;

public class User {

    private String email;
    private String full_name;
    private String birth_date;
    private Utils.UserType type;
    private int score_first_quiz;
    private int num_challenges;
    private String url_string;
    private String status;

    public User(String email, String full_name, String birth_date, Utils.UserType type, int num_challenges, String url_string, String status) {
        this.email = email;
        this.full_name = full_name;
        this.birth_date = birth_date;
        this.type = type;
        this.score_first_quiz=0;
        this.num_challenges = num_challenges;
        this.url_string = url_string;
        this.status = status;
    }

    public int getScoreFirstQuiz() {
        return score_first_quiz;
    }

    public void setScoreFirstQuiz(int num_rights_first_quiz) {
        this.score_first_quiz = num_rights_first_quiz;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getBirthDate() {
        return birth_date;
    }

    public void setBirthDate(String birth_date) {
        this.birth_date = birth_date;
    }

    public Utils.UserType getType() {
        return type;
    }

    public void setType(Utils.UserType type) {
        this.type = type;
    }

    public int getNum_challenges() {
        return num_challenges;
    }

    public void setNum_challenges(int num_challenges) {
        this.num_challenges = num_challenges;
    }

    public String getUrl_string() {
        return url_string;
    }

    public void setUrl_string(String url_string) {
        this.url_string = url_string;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

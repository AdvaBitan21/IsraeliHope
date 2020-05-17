package com.technion.android.israelihope.Objects;

import com.technion.android.israelihope.Utils;

public class User {

    private String email;
    private String user_name;
    private String city;
    private String birth_date;
    private Utils.UserType type;
    private int score_first_quiz;
    private int num_challenges;
    private String status;

    public User() {
    }

    public User(String email, String user_name, String city, String birth_date, Utils.UserType type, int num_challenges, String status) {
        this.email = email;
        this.user_name = user_name;
        this.city = city;
        this.birth_date = birth_date;
        this.type = type;
        this.score_first_quiz = -1;//marked as not answered
        this.num_challenges = num_challenges;
        this.status = status;
    }

    public User(String email, String user_name, String city, String birth_date) {
        this.email = email;
        this.user_name = user_name;
        this.city = city;
        this.type=Utils.UserType.A;//need to change
        this.birth_date = birth_date;
        this.status = "online";
        this.score_first_quiz=-1;
    }

    public int getScore_first_quiz() {
        return score_first_quiz;
    }

    public void setScore_first_quiz(int score_first_quiz) {
        this.score_first_quiz = score_first_quiz;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }
}

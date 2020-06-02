package com.technion.android.israelihope.Objects;

import com.technion.android.israelihope.Utils;

public class User {

    private String email;
    private String userName;
    private String city;
    private String birthDate;
    private Utils.UserType type;
    private int score_first_quiz;
    private int num_challenges;
    private String status;
    private String token_id;

    public User() {
    }

    public User(String email, String userName, String city, String birthDate, Utils.UserType type, int num_challenges, String status) {
        this.email = email;
        this.userName = userName;
        this.city = city;
        this.birthDate = birthDate;
        this.type = type;
        this.score_first_quiz = -1;//marked as not answered
        this.num_challenges = num_challenges;
        this.status = status;
        this.token_id="";

    }

    public User(String email, String userName, String city, String birthDate) {
        this.email = email;
        this.userName = userName;
        this.city = city;
        this.type=Utils.UserType.A;//need to change
        this.birthDate = birthDate;
        this.status = "online";
        this.score_first_quiz=-1;
    }

    public int getScore_first_quiz() {
        return score_first_quiz;
    }

    public void setScore_first_quiz(int score_first_quiz) {
        this.score_first_quiz = score_first_quiz;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}

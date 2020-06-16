package com.technion.android.israelihope.Objects;

import java.io.Serializable;

public class User implements Serializable {

    //for sign up
    public enum UserType {
        Jewish1,
        Jewish2,
        Jewish3,
        Christian1,
        Christian2,
        Christian3,
        Muslim1,
        Muslim2,
        Muslim3,
        Druze,
        Bedouin
    }
    public enum AcademicRole{
        Student,
        Academic_Staff,
        Administrative_Staff
    }

    private String email;
    private String userName;
    private String city;
    private String birthDate;
    private UserType type;
    private int score_first_quiz;
    private int num_challenges;
    private String status;
    private String token_id;
    private AcademicRole academicRole;

    public User() {
    }

    public User(String email, String userName, String city, String birthDate, UserType type, int num_challenges, String status) {
        this.email = email;
        this.userName = userName;
        this.city = city;
        this.birthDate = birthDate;
        this.type = type;
        this.score_first_quiz = -1;//marked as not answered
        this.num_challenges = num_challenges;
        this.status = status;
        this.token_id = "";
        this.academicRole = AcademicRole.Student;

    }

    public User(String email, String userName, String city, String birthDate) {
        this.email = email;
        this.userName = userName;
        this.city = city;
        this.type = UserType.Jewish1;//need to change
        this.birthDate = birthDate;
        this.status = "online";
        this.score_first_quiz = -1;
    }


    public AcademicRole getAcademicRole() {
        return academicRole;
    }

    public void setAcademicRole(AcademicRole academicRole) {
        this.academicRole = academicRole;
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


    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }
}

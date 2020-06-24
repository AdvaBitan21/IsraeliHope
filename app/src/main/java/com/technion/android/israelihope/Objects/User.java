package com.technion.android.israelihope.Objects;

import java.io.Serializable;

public class User implements Serializable {

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
    public enum AcademicRole {
        Student,
        Academic_Staff,
        Administrative_Staff
    }

    private String email;
    private String userName;
    private UserType type;
    private AcademicRole academicRole;

    private int scoreFirstQuiz;
    private String status;
    private String tokenId;


    public User() {
    }

    public User(String email, String userName, UserType type, AcademicRole role) {
        this.email = email;
        this.userName = userName;
        this.type = type;
        this.academicRole = role;
        this.scoreFirstQuiz = -1;
        this.status = "online";
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public AcademicRole getAcademicRole() {
        return academicRole;
    }

    public void setAcademicRole(AcademicRole academicRole) {
        this.academicRole = academicRole;
    }

    public int getScoreFirstQuiz() {
        return scoreFirstQuiz;
    }

    public void setScoreFirstQuiz(int scoreFirstQuiz) {
        this.scoreFirstQuiz = scoreFirstQuiz;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }
}

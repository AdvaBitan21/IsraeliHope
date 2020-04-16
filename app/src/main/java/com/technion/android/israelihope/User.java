package com.technion.android.israelihope;

public class User {

    private String email;
    private String full_name;
    private String birth_date;
    private Utils.UserType type;
    private int score_first_quiz;

    public User(String email, String full_name, String birth_date, Utils.UserType type) {
        this.email = email;
        this.full_name = full_name;
        this.birth_date = birth_date;
        this.type = type;
        this.score_first_quiz=0;
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

}

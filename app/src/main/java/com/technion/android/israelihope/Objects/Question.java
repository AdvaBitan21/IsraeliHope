package com.technion.android.israelihope.Objects;

import com.technion.android.israelihope.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Question {
    private String id;
    private String content;
    private Utils.QuestionType question_type;
    private ArrayList<String> possible_answers; // from 2 to 4
    private ArrayList<String> right_answers;
    private String from_email;
    private String to_email;
    //private Religon religon_target need to add the religon the question discusses on
    private Map<Utils.UserType,Integer> count_rights;
    private int count_answers;
    private int first_quiz_index; // if it is not first quiz it will be -1
    private Utils.UserType subject;

    public Question(String id, String content, Utils.QuestionType questionType, ArrayList<String> possible_answers, ArrayList<String> right_answers, String from_email, String to_email, int firstQuizIndex,Utils.UserType subject) {
        this.id = id;
        this.content = content;
        this.question_type = questionType;
        this.possible_answers = possible_answers;
        this.right_answers = right_answers;
        this.from_email = from_email;
        this.to_email = to_email;
        this.count_rights = new HashMap<>();
        InitCountRights();
        this.count_answers = 0;
        this.first_quiz_index = firstQuizIndex;
        this.subject=subject;
    }
    private void InitCountRights(){
        count_rights.put(Utils.UserType.A,0);
        count_rights.put(Utils.UserType.B,0);
        count_rights.put(Utils.UserType.C,0);
        count_rights.put(Utils.UserType.D,0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Utils.QuestionType getQuestionType() {
        return question_type;
    }

    public void setQuestionType(Utils.QuestionType questionType) {
        this.question_type = questionType;
    }

    public Utils.UserType getSubject() {
        return subject;
    }

    public void setSubject(Utils.UserType subject) {
        this.subject = subject;
    }

    public ArrayList<String> getPossibleAnswers() {
        return possible_answers;
    }

    public void setPossibleAnswers(ArrayList<String> possible_answers) {
        this.possible_answers = possible_answers;
    }

    public ArrayList<String> getRightAnswers() {
        return right_answers;
    }

    public void setRightAnswers(ArrayList<String> right_answers) {
        this.right_answers = right_answers;
    }

    public String getFromEmail() {
        return from_email;
    }

    public void setFromEmail(String from_email) {
        this.from_email = from_email;
    }

    public String getToEmail() {
        return to_email;
    }

    public void setToEmail(String to_email) {
        this.to_email = to_email;
    }

    public Map<Utils.UserType, Integer> getCountRights() {
        return count_rights;
    }

    public void setCountRights(HashMap<Utils.UserType, Integer> countRights) {
        this.count_rights = count_rights;
    }

    public int getCountAnswers() {
        return count_answers;
    }

    public void setCountAnswers(int count_answers) {
        this.count_answers = count_answers;
    }


    public int getFirstQuizIndex() {
        return first_quiz_index;
    }

    public void setFirstQuizIndex(int firstQuizIndex) {
        this.first_quiz_index = firstQuizIndex;
    }

    public void addRightAnswerByUser(Utils.UserType type){
        count_rights.put(type,count_rights.get(type)+1);
    }
}

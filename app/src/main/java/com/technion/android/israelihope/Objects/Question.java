package com.technion.android.israelihope.Objects;

import com.technion.android.israelihope.CheckBoxQuestionFragment;
import com.technion.android.israelihope.CloseQuestionFragment;
import com.technion.android.israelihope.YesNoQuestionFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class Question implements Serializable {

    private String id;
    private String content;
    private QuestionType question_type;
    private ArrayList<String> possible_answers;     // from 2 to 4
    private ArrayList<String> right_answers;
    private Map<String, Integer> countRights;
    private Map<String, Integer> countAnswers;
    private int first_quiz_index;                   // if it is not first quiz it will be -1
    private QuestionSubject subject;


    public Question() {
        this.id = "";
        this.content = "";
        this.question_type = QuestionType.CLOSE;
        this.possible_answers = new ArrayList<>();
        possible_answers.add("");
        possible_answers.add("");
        possible_answers.add("");
        possible_answers.add("");
        possible_answers.add("");

        this.right_answers = new ArrayList<>();
        right_answers.add("dummy");
        this.first_quiz_index = -1;
        this.subject = QuestionSubject.Bedouin;

        initCountRights();
        initCountAnswers();
    }

    public Question(String id, String content, QuestionType questionType, ArrayList<String> possible_answers, ArrayList<String> right_answers, int firstQuizIndex, QuestionSubject subject) {
        this.id = id;
        this.content = content;
        this.question_type = questionType;
        this.possible_answers = possible_answers;
        this.right_answers = right_answers;
        this.first_quiz_index = firstQuizIndex;
        this.subject = subject;

        initCountRights();
        initCountAnswers();
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


    public QuestionSubject getSubject() {
        return subject;
    }

    public void setSubject(QuestionSubject subject) {
        this.subject = subject;
    }


    public QuestionType getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(QuestionType question_type) {
        this.question_type = question_type;
    }


    public ArrayList<String> getPossible_answers() {
        return possible_answers;
    }

    public void setPossible_answers(ArrayList<String> possible_answers) {
        this.possible_answers = possible_answers;
    }


    public ArrayList<String> getRight_answers() {
        return right_answers;
    }

    public void setRight_answers(ArrayList<String> right_answers) {
        this.right_answers = right_answers;
    }


    private void initCountRights() {
        this.countRights = new HashMap<>();
        for (User.UserType type : User.UserType.values()) {
            countRights.put(type.name(), 0);
        }
    }

    public Map<String, Integer> getCountRights() {
        return countRights;
    }

    public void setCountRights(Map<String, Integer> countRights) {
        this.countRights = countRights;
    }

    public void addRightAnswerByUser(User.UserType type) {
        countRights.put("" + type, countRights.get("" + type) + 1);
    }


    private void initCountAnswers() {
        this.countAnswers = new HashMap<>();
        for (User.UserType type : User.UserType.values()) {
            countAnswers.put(type.name(), 0);
        }
    }

    public Map<String, Integer> getCountAnswers() {
        return countAnswers;
    }

    public void setCountAnswers(Map<String, Integer> countAnswers) {
        this.countAnswers = countAnswers;
    }

    public void addAnswerByUser(User.UserType type) {
        countAnswers.put(type.name(), countAnswers.get(type.name()) + 1);
    }


    public int getFirst_quiz_index() {
        return first_quiz_index;
    }

    public void setFirst_quiz_index(int first_quiz_index) {
        this.first_quiz_index = first_quiz_index;
    }


    public Fragment createQuestionFragment() {

        Fragment fragment = null;
        switch (question_type) {
            case YES_NO:
                fragment = new YesNoQuestionFragment(this);
                break;
            case CLOSE:
                fragment = new CloseQuestionFragment(this);
                break;
            case CHECKBOX:
                fragment = new CheckBoxQuestionFragment(this);
                break;
            default:
                break;
        }
        return fragment;
    }


    public enum QuestionType {
        YES_NO,
        CLOSE,
        CHECKBOX
    }

    public enum QuestionSubject {
        General,
        Jewish,
        Christian,
        Muslim,
        Druze,
        Bedouin,
        Ethiopian,
        Moroccan
    }

}

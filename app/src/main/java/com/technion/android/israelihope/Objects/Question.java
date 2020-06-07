package com.technion.android.israelihope.Objects;

import com.technion.android.israelihope.CheckBoxQuestionFragment;
import com.technion.android.israelihope.CloseQuestionFragment;
import com.technion.android.israelihope.Utils;
import com.technion.android.israelihope.YesNoQuestionFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;

public class Question implements Serializable {
    private String id;
    private String content;
    private Utils.QuestionType question_type;
    private ArrayList<String> possible_answers;  // from 2 to 4
    private ArrayList<String> right_answers;
    private Map<String, Integer> countRights;   // firebase demands String key
    private int count_answers;
    private int first_quiz_index;               // if it is not first quiz it will be -1
    private Utils.UserType subject;
    //private Religon religon_target need to add the religon the question discusses on


    public Question() {
    }

    public Question(String id, String content, Utils.QuestionType questionType, ArrayList<String> possible_answers, ArrayList<String> right_answers, int firstQuizIndex, Utils.UserType subject) {
        this.id = id;
        this.content = content;
        this.question_type = questionType;
        this.possible_answers = possible_answers;
        this.right_answers = right_answers;
        this.countRights = new HashMap<>();
        this.count_answers = 0;
        this.first_quiz_index = firstQuizIndex;
        this.subject = subject;

        InitCountRights();
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


    public Utils.UserType getSubject() {
        return subject;
    }

    public void setSubject(Utils.UserType subject) {
        this.subject = subject;
    }


    public Utils.QuestionType getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(Utils.QuestionType question_type) {
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

    public void addRightAnswerByUser(Utils.UserType type) {
        countRights.put("" + type, countRights.get("" + type) + 1);
    }


    public Map<String, Integer> getCountRights() {
        return countRights;
    }

    public void setCountRights(Map<String, Integer> countRights) {
        this.countRights = countRights;
    }

    private void InitCountRights() {
        countRights.put("" + Utils.UserType.A, 0);
        countRights.put("" + Utils.UserType.B, 0);
        countRights.put("" + Utils.UserType.C, 0);
        countRights.put("" + Utils.UserType.D, 0);
    }


    public int getCount_answers() {
        return count_answers;
    }

    public void setCount_answers(int count_answers) {
        this.count_answers = count_answers;
    }


    public int getFirst_quiz_index() {
        return first_quiz_index;
    }

    public void setFirst_quiz_index(int first_quiz_index) {
        this.first_quiz_index = first_quiz_index;
    }


    public Fragment getQuestionFragment() {

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
}

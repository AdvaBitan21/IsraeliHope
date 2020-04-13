package com.technion.android.israelihope;

import java.util.ArrayList;

public class Question {
    private String id;
    private String content;
    private Utils.QuestionType questionType;
    private ArrayList<String> answers; // from 2 to 4
    private String from_email;
    private String to_email;
    //private Religon religon_target need to add the religon the question discusses on
    private ArrayList<Integer> countRights;
    private int count_answers;
}

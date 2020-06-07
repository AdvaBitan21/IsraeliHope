package com.technion.android.israelihope.Objects;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

public class Challenge implements Serializable {

    public enum ChallengeState {
        SENT,
        IN_PROGRESS,
        OUT_OF_TIME,
        CORRECT,
        WRONG
    }

    private String questionId;
    private ChallengeState state;

    public Challenge() {}

    public Challenge(String questionId) {
        this.questionId = questionId;
        this.state = ChallengeState.SENT;
    }


    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public ChallengeState getState() {
        return state;
    }

    public void setState(ChallengeState state) {
        this.state = state;
    }
}

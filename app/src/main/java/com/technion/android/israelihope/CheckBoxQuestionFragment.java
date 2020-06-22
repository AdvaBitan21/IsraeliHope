package com.technion.android.israelihope;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.Objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CheckBoxQuestionFragment extends Fragment {

    private Question mQuestion;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private User mUser;
    private int[] check;

//    private static final long START_TIME_IN_MILLIS = 30000;
//    private CountDownTimer mCountDownTimer;
//    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;


    public CheckBoxQuestionFragment(Question question) {
        this.mQuestion = question;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_check_box_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Query requestQuery = db.collection("Users").whereEqualTo("email", mAuth.getCurrentUser().getEmail());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mUser = document.toObject(User.class);
                    }

                    initUI();
                    InitNextQuestion();
                }
            }
        });
    }

    private void initUI() {

        TextView questionIndex = getActivity().findViewById(R.id.question_number);
        questionIndex.setText("" + mQuestion.getFirst_quiz_index());

        TextView questionContent = getActivity().findViewById(R.id.question);
        questionContent.setText(mQuestion.getContent());

        final CheckBox choice1 = getActivity().findViewById(R.id.choice1);
        choice1.setText(mQuestion.getPossible_answers().get(0));

        final CheckBox choice2 = getActivity().findViewById(R.id.choice2);
        choice2.setText(mQuestion.getPossible_answers().get(1));

        final CheckBox choice3 = getActivity().findViewById(R.id.choice3);
        choice3.setText(mQuestion.getPossible_answers().get(2));

        final CheckBox choice4 = getActivity().findViewById(R.id.choice4);
        choice4.setText(mQuestion.getPossible_answers().get(3));

        final CheckBox choice5 = getActivity().findViewById(R.id.choice5);
        choice5.setText(mQuestion.getPossible_answers().get(4));
    }


    private void colorTheRightAndWrongAnswers() {
        ArrayList<String> choices = mQuestion.getPossible_answers();
        check = new int[5];
        check[0] = 0;
        check[1] = 0;
        check[2] = 0;
        check[3] = 0;
        check[4] = 0;
        if (mQuestion.getRight_answers().contains(choices.get(0))) {
            check[0]++;
            getActivity().findViewById(R.id.choice1).setBackgroundColor(Color.GREEN);
        } else
            getActivity().findViewById(R.id.choice1).setBackgroundColor(Color.RED);

        if (mQuestion.getRight_answers().contains(choices.get(1))) {
            check[1]++;
            getActivity().findViewById(R.id.choice2).setBackgroundColor(Color.GREEN);
        } else
            getActivity().findViewById(R.id.choice2).setBackgroundColor(Color.RED);

        if (mQuestion.getRight_answers().contains(choices.get(2))) {
            check[2]++;
            getActivity().findViewById(R.id.choice3).setBackgroundColor(Color.GREEN);
        } else
            getActivity().findViewById(R.id.choice3).setBackgroundColor(Color.RED);

        if (mQuestion.getRight_answers().contains(choices.get(3))) {
            check[3]++;
            getActivity().findViewById(R.id.choice4).setBackgroundColor(Color.GREEN);
        } else
            getActivity().findViewById(R.id.choice4).setBackgroundColor(Color.RED);

        if (mQuestion.getRight_answers().contains(choices.get(4))) {
            check[4]++;
            getActivity().findViewById(R.id.choice5).setBackgroundColor(Color.GREEN);
        } else
            getActivity().findViewById(R.id.choice5).setBackgroundColor(Color.RED);
    }


    private void checkAnswer() {

        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);

        final Map<String, Object> updates = new HashMap<String, Object>();
        mQuestion.addAnswerByUser(mUser.getType());
        updates.put("countAnswers", mQuestion.getCountAnswers());

        colorTheRightAndWrongAnswers();

        if (((CheckBox) getActivity().findViewById(R.id.choice1)).isChecked())
            check[0]++;
        if (((CheckBox) getActivity().findViewById(R.id.choice2)).isChecked())
            check[1]++;
        if (((CheckBox) getActivity().findViewById(R.id.choice3)).isChecked())
            check[2]++;
        if (((CheckBox) getActivity().findViewById(R.id.choice4)).isChecked())
            check[3]++;
        if (((CheckBox) getActivity().findViewById(R.id.choice5)).isChecked())
            check[4]++;
        boolean flag = true;
        for (int i = 0; i < 5; i++)
            if (check[i] == 1)
                flag = false;
        if (flag == true) {
            mQuestion.addRightAnswerByUser(mUser.getType());
            updates.put("countRights", mQuestion.getCountRights());

            if (mQuestion.getFirst_quiz_index() >= 0)
                ((FirstQuizActivity) getActivity()).IncreaseScore();
        }

        DocumentReference questionRef = db.collection("Questions").document(mQuestion.getId());
        questionRef.update(updates);
    }

//    private void initTimer() {
//        final TextView timeLeft = getActivity().findViewById(R.id.time_left);
//        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                mTimeLeftInMillis = millisUntilFinished;
//                timeLeft.setText("" + ((int) (mTimeLeftInMillis / 1000) % 60));
//            }
//
//            @Override
//            public void onFinish() {
//                nextQuestion();
//                //can add message that time is over
//            }
//        }.start();
//
//    }

    private void nextQuestion() {

        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
        checkAnswer();
        int index = mQuestion.getFirst_quiz_index();
        if (index == Utils.AMOUNT_OF_QUESTIONS_FIRST_QUIZ) {
            //Move to FirstQuizFinishFragment
            ((FirstQuizActivity) getActivity()).increaseQuizProgress();
            ((FirstQuizActivity) getActivity()).loadFragment(new FirstQuizFinishFragment());
            return;
        }
        Query questionRef = FirebaseFirestore.getInstance().collection("Questions").whereEqualTo("first_quiz_index", index + 1);
        questionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot doc : task.getResult()) {
                        //Move to next question
                        Question q = doc.toObject(Question.class);
                        ((FirstQuizActivity) getActivity()).increaseQuizProgress();
                        ((FirstQuizActivity) getActivity()).loadFragment(q.createQuestionFragment());
                    }
                }
            }
        });
    }


    private void InitNextQuestion() {

        final Button doneBtn = getActivity().findViewById(R.id.done);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });
    }

}

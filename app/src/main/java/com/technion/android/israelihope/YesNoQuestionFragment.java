package com.technion.android.israelihope;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class YesNoQuestionFragment extends Fragment {
    private static final long START_TIME_IN_MILLIS = 30000;

    private Question mQuestion;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private User mUser;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;


    public YesNoQuestionFragment(Question question) {
        this.mQuestion = question;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        return inflater.inflate(R.layout.close_question_fragment, container, false);
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

                    InitUI();
                    InitTimer();


                }
            }
        });
    }

    private void InitUI(){

        TextView questionIndex = getActivity().findViewById(R.id.question_number);
        questionIndex.setText(mQuestion.getFirstQuizIndex());

        TextView questionContent = getActivity().findViewById(R.id.question);
        questionIndex.setText(mQuestion.getContent());

        final Button choice1= getActivity().findViewById(R.id.choice1);
        choice1.setText(mQuestion.getPossibleAnswers().get(0));
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { CheckAnswer(mQuestion.getPossibleAnswers().get(0),choice1);}});

        final Button choice2= getActivity().findViewById(R.id.choice2);
        choice2.setText(mQuestion.getPossibleAnswers().get(1));
        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { CheckAnswer(mQuestion.getPossibleAnswers().get(0),choice2);}});





    }

    private void CheckAnswer(String possible_answer,Button btn){
        mCountDownTimer.cancel();
        final Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("count_answers",mQuestion.getCountAnswers()+1);

        if(possible_answer.equals(mQuestion.getRightAnswers())) {
            if(btn!=null)
                btn.setBackgroundColor(Color.GREEN);

            mQuestion.addRightAnswerByUser(mUser.getType());
            updates.put("count_rights",mQuestion.getCountRights());

            DocumentReference questionRef = db.collection("Questions").document(mQuestion.getId());
            questionRef.update(updates);

        }
        else {
            if(btn!=null)
                btn.setBackgroundColor(Color.RED);

            DocumentReference questionRef = db.collection("Questions").document(mQuestion.getId());
            questionRef.update(updates);

        }

        NextQuestion();

    }
    private void InitTimer(){
        final TextView timeLeft = getActivity().findViewById(R.id.time_left);
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                timeLeft.setText(((int) (mTimeLeftInMillis / 1000) % 60));
            }

            @Override
            public void onFinish() {
                CheckAnswer("dummy",null);
                //can add message that time is over
            }
        }.start();

    }

    private void NextQuestion() {
        Button next = getActivity().findViewById(R.id.next);
        next.setVisibility(View.VISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = mQuestion.getFirstQuizIndex();
                if (index == Utils.AMOUNT_OF_QUESTIONS_FIRST_QUIZ) {
                    //Move to DoneFirstQuizFragment
                    return;
                }
                index++;
                Query questionRef = FirebaseFirestore.getInstance().collection("Questions").whereEqualTo("first_quiz_index", index + 1);
                questionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                //Move to next question
                                Question q = doc.toObject(Question.class);
                                switch (q.getQuestionType()) {
                                    case YesNo:
                                        ((MainActivity) getContext()).loadFragment(new YesNoQuestionFragment(q));
                                        break;
                                    case Close:
                                        ((MainActivity) getContext()).loadFragment(new CloseQuestionFragment(q));
                                        break;
                                    case CheckBox:
                                        ((MainActivity) getContext()).loadFragment(new CheckBoxQuestionFragment(q));
                                        break;
                                    default:
                                        break;

                                }
                            }
                        }
                    }
                });
            }
        });
    }


}

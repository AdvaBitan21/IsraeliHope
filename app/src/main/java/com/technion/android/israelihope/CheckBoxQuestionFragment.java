package com.technion.android.israelihope;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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


public class CheckBoxQuestionFragment extends Fragment {
    private static final long START_TIME_IN_MILLIS = 30000;

    private Question mQuestion;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private User mUser;
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private int[] check= {0,0,0,0,0};


    public CheckBoxQuestionFragment(Question question) {
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

                    initUI();
                    initTimer();


                }
            }
        });
    }

    private void initUI(){

        TextView questionIndex = getActivity().findViewById(R.id.question_number);
        questionIndex.setText(mQuestion.getFirstQuizIndex());

        TextView questionContent = getActivity().findViewById(R.id.question);
        questionIndex.setText(mQuestion.getContent());

        final CheckBox choice1= getActivity().findViewById(R.id.choice1);
        choice1.setText(mQuestion.getPossibleAnswers().get(0));

        final CheckBox choice2= getActivity().findViewById(R.id.choice2);
        choice2.setText(mQuestion.getPossibleAnswers().get(1));


        final CheckBox choice3= getActivity().findViewById(R.id.choice3);
        choice3.setText(mQuestion.getPossibleAnswers().get(2));

        final CheckBox choice4= getActivity().findViewById(R.id.choice4);
        choice4.setText(mQuestion.getPossibleAnswers().get(3));

        final CheckBox choice5= getActivity().findViewById(R.id.choice5);
        choice5.setText(mQuestion.getPossibleAnswers().get(4));

        final Button doneBtn= getActivity().findViewById(R.id.done);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { checkAnswer();}});
    }

    private void colorTheRightAndWrongAnsers(){
        ArrayList<String> choices = mQuestion.getPossibleAnswers();

        if(mQuestion.getRightAnswers().contains(choices.get(0))) {
            check[0]++;
            getActivity().findViewById(R.id.choice1).setBackgroundColor(Color.GREEN);
        }
        else
            getActivity().findViewById(R.id.choice1).setBackgroundColor(Color.RED);

        if(mQuestion.getRightAnswers().contains(choices.get(1))) {
            check[1]++;
            getActivity().findViewById(R.id.choice2).setBackgroundColor(Color.GREEN);
        }
        else
            getActivity().findViewById(R.id.choice2).setBackgroundColor(Color.RED);

        if(mQuestion.getRightAnswers().contains(choices.get(2))) {
            check[2]++;
            getActivity().findViewById(R.id.choice3).setBackgroundColor(Color.GREEN);
        }
        else
            getActivity().findViewById(R.id.choice3).setBackgroundColor(Color.RED);

        if(mQuestion.getRightAnswers().contains(choices.get(3))) {
            check[3]++;
            getActivity().findViewById(R.id.choice4).setBackgroundColor(Color.GREEN);
        }
        else
            getActivity().findViewById(R.id.choice4).setBackgroundColor(Color.RED);

        if(mQuestion.getRightAnswers().contains(choices.get(4))) {
            check[4]++;
            getActivity().findViewById(R.id.choice5).setBackgroundColor(Color.GREEN);
        }
        else
            getActivity().findViewById(R.id.choice5).setBackgroundColor(Color.RED);


    }
    private void checkAnswer(){
        mCountDownTimer.cancel();
        Button btn =getActivity().findViewById(R.id.done);
        final Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("count_answers",mQuestion.getCountAnswers()+1);

        colorTheRightAndWrongAnsers();
        if(((CheckBox)getActivity().findViewById(R.id.choice1)).isChecked())
            check[0]++;
        if(((CheckBox)getActivity().findViewById(R.id.choice2)).isChecked())
            check[1]++;
        if(((CheckBox)getActivity().findViewById(R.id.choice3)).isChecked())
            check[2]++;
        if(((CheckBox)getActivity().findViewById(R.id.choice4)).isChecked())
            check[3]++;
        if(((CheckBox)getActivity().findViewById(R.id.choice5)).isChecked())
            check[4]++;
        boolean flag= true;
        for(int i=0;i<5;i++)
            if(check[i]==1)
                flag=false;
        if(flag==true) {
            btn.setText("נכון מאוד");
            mQuestion.addRightAnswerByUser(mUser.getType());
            updates.put("count_rights", mQuestion.getCountRights());

            if(mQuestion.getFirstQuizIndex()>=0)
                ((MainActivity)getActivity()).IncreasFirstQuizScore();

        }
        else{
            btn.setText("יש לך טעות");
            btn.setBackgroundColor(Color.RED);
        }

        DocumentReference questionRef = db.collection("Questions").document(mQuestion.getId());
        questionRef.update(updates);

        nextQuestion();

    }
    private void initTimer(){
        final TextView timeLeft = getActivity().findViewById(R.id.time_left);
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                timeLeft.setText(((int) (mTimeLeftInMillis / 1000) % 60));
            }

            @Override
            public void onFinish() {
                checkAnswer();
                //can add message that time is over
            }
        }.start();

    }

    private void nextQuestion() {
        Button next = getActivity().findViewById(R.id.next);
        next.setVisibility(View.VISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index= mQuestion.getFirstQuizIndex();
                if(index == Utils.AMOUNT_OF_QUESTIONS_FIRST_QUIZ)
                {
                    ((MainActivity) getContext()).loadFragment(new FirstQuizFinishFragment());
                    return;
                }
                Query questionRef = FirebaseFirestore.getInstance().collection("Questions").whereEqualTo("first_quiz_index", index+1);
                questionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                //Move to next question
                                Question q = doc.toObject(Question.class);
                                switch (q.getQuestionType()){
                                    case YesNo: ((MainActivity) getContext()).loadFragment(new YesNoQuestionFragment(q));break;
                                    case Close: ((MainActivity) getContext()).loadFragment(new CloseQuestionFragment(q));break;
                                    case CheckBox: ((MainActivity) getContext()).loadFragment(new CheckBoxQuestionFragment(q));break;
                                    default:break;

                                }
                            }
                        }
                    }
                });


            }
        });

    }


}

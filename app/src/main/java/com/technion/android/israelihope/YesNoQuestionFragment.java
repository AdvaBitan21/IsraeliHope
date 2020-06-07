package com.technion.android.israelihope;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class YesNoQuestionFragment extends Fragment {


    private Question mQuestion;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private User mUser;
    private Button mChosen;
    private boolean isChosen = false;


    public YesNoQuestionFragment(Question question) {
        this.mQuestion = question;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_yes_no_question, container, false);
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

        final Button choice1 = getActivity().findViewById(R.id.choice1);
        choice1.setText(mQuestion.getPossible_answers().get(0));
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.animateClick(choice1);

                if (isChosen == false)
                    isChosen = true;
                else {
                    mChosen.setBackgroundTintList(null);
                    mChosen.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }

                mChosen = choice1;
                mChosen.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                mChosen.setTextColor(getResources().getColor(R.color.white));
            }
        });

        final Button choice2 = getActivity().findViewById(R.id.choice2);
        choice2.setText(mQuestion.getPossible_answers().get(1));
        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.animateClick(choice2);

                if (isChosen == false)
                    isChosen = true;
                else {
                    mChosen.setBackgroundTintList(null);
                    mChosen.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }

                mChosen = choice2;
                mChosen.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                mChosen.setTextColor(getResources().getColor(R.color.white));
            }
        });
    }


    private void checkAnswer() {

        final Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("count_answers", mQuestion.getCount_answers() + 1);

        if (mChosen != null && mQuestion.getRight_answers().contains(mChosen.getText())) {

            if (mQuestion.getFirst_quiz_index() >= 0)
                ((FirstQuizActivity) getActivity()).IncreaseScore();

            mQuestion.addRightAnswerByUser(mUser.getType());
            updates.put("countRights", mQuestion.getCountRights());

        }

        DocumentReference questionRef = db.collection("Questions").document(mQuestion.getId());
        questionRef.update(updates);
    }


    private void nextQuestion() {

        Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
        checkAnswer();

        int index = mQuestion.getFirst_quiz_index();
        if (index == Utils.AMOUNT_OF_QUESTIONS_FIRST_QUIZ) {
            //Move to FirstQuizFinishFragment
            ((FirstQuizActivity) getContext()).increaseQuizProgress();
            ((FirstQuizActivity) getContext()).loadFragment(new FirstQuizFinishFragment());
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
                        ((FirstQuizActivity) getContext()).increaseQuizProgress();
                        ((FirstQuizActivity) getActivity()).loadFragment(q.getQuestionFragment());
                    }
                }
            }
        });
    }


    private void InitNextQuestion() {

        ImageButton next = getActivity().findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });
    }

}

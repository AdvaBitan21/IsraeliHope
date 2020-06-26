package com.technion.android.israelihope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Objects.Question;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EditQuestionFragment extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Question question;
    private EditText content;
    private EditText first_ans;
    private EditText second_ans;
    private EditText third_ans;
    private EditText forth_ans;
    private EditText fifth_ans;
    private boolean isAdding;

    private CheckBox first_check;
    private CheckBox second_check;
    private CheckBox third_check;
    private CheckBox forth_check;
    private CheckBox fifth_check;
    private Spinner typeSpinner;
    private Spinner subjectSpinner;


    public EditQuestionFragment(Question question, boolean isAdding) {
        this.question = question;

        this.isAdding = isAdding;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return inflater.inflate(R.layout.fragment_edit_question, null);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        typeSpinner = getActivity().findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.question_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        subjectSpinner = getActivity().findViewById(R.id.subject_spinner);
        ArrayAdapter<CharSequence> adapterSubject = ArrayAdapter.createFromResource(getContext(),
                R.array.question_subjects, android.R.layout.simple_spinner_item);
        adapterSubject.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(adapterSubject);
        subjectSpinner.setSelection(GetIndexOfSubject(question.getSubject()));

        content = getActivity().findViewById(R.id.content_edit);
        first_ans = getActivity().findViewById(R.id.first_edit);
        second_ans = getActivity().findViewById(R.id.second_edit);
        third_ans = getActivity().findViewById(R.id.third_edit);
        forth_ans = getActivity().findViewById(R.id.forth_edit);
        fifth_ans = getActivity().findViewById(R.id.fifth_edit);

        first_check = getActivity().findViewById(R.id.choice1);
        second_check = getActivity().findViewById(R.id.choice2);
        third_check = getActivity().findViewById(R.id.choice3);
        forth_check = getActivity().findViewById(R.id.choice4);
        fifth_check = getActivity().findViewById(R.id.choice5);
        content.setText(question.getContent());
        if (question.getQuestion_type().equals(Question.QuestionType.YES_NO))
            typeSpinner.setSelection(0);
        if (question.getQuestion_type().equals(Question.QuestionType.CLOSE))
            typeSpinner.setSelection(1);
        if (question.getQuestion_type().equals(Question.QuestionType.CHECKBOX))
            typeSpinner.setSelection(2);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                typeMode(typeSpinner.getSelectedItem().toString());
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                typeMode(typeSpinner.getSelectedItem().toString());

            }

        });
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        setUpDeleteBtn();
        setUpUpdateBtn();


    }


    private void setUpDeleteBtn() {

        Button deleteBtn = getActivity().findViewById(R.id.delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdding == true)
                    getActivity().onBackPressed();
                else {
                    CollectionReference requestsRef = db.collection("Questions");
                    Query requestQuery = requestsRef.whereEqualTo("id", question.getId());
                    requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            getActivity().onBackPressed();
                                        }
                                    });
                                }
                            }
                        }
                    });


                }
            }
        });
    }

    private void setUpUpdateBtn() {
        Button updateBtn = getActivity().findViewById(R.id.save);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkBeforeUpdate(typeSpinner.getSelectedItem().toString()))
                    return;
                final HashMap<String, Object> updates = new HashMap<>();
                question.setContent(content.getText().toString());
                question.setSubject(getSubject(subjectSpinner.getSelectedItem().toString()));
                if (typeSpinner.getSelectedItem().toString().equals("שאלת נכון/לא נכון"))
                    question.setQuestion_type(Question.QuestionType.YES_NO);
                if (typeSpinner.getSelectedItem().toString().equals("שאלה אמריקאית"))
                    question.setQuestion_type(Question.QuestionType.CLOSE);
                if (typeSpinner.getSelectedItem().toString().equals("שאלת סימון תשובות"))
                    question.setQuestion_type(Question.QuestionType.CHECKBOX);
                ArrayList<String> pos_ans = new ArrayList<>();
                pos_ans.add(first_ans.getText().toString());
                pos_ans.add(second_ans.getText().toString());
                pos_ans.add(third_ans.getText().toString());
                pos_ans.add(forth_ans.getText().toString());
                pos_ans.add(fifth_ans.getText().toString());


                ArrayList<String> right_ans = new ArrayList<>();
                if (first_check.isChecked())
                    right_ans.add(first_ans.getText().toString());
                if (second_check.isChecked())
                    right_ans.add(second_ans.getText().toString());
                if (third_check.isChecked())
                    right_ans.add(third_ans.getText().toString());

                if (forth_check.isChecked())
                    right_ans.add(forth_ans.getText().toString());
                if (fifth_check.isChecked())
                    right_ans.add(fifth_ans.getText().toString());

                question.setPossible_answers(pos_ans);
                question.setRight_answers(right_ans);
                final DocumentReference qRef;
                if (isAdding == true) {
                    qRef = FirebaseFirestore.getInstance().collection("Questions").document();
                    question.setId(qRef.getId());
                } else
                    qRef = FirebaseFirestore.getInstance().collection("Questions").document(question.getId());
                qRef.set(question).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getActivity().onBackPressed();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
    }
private int GetIndexOfSubject(Question.QuestionSubject sub){
        if(sub == Question.QuestionSubject.General)
            return 0;
    if(sub == Question.QuestionSubject.Jewish)
    return 1;
    if(sub == Question.QuestionSubject.Muslim)
        return 2;
    if(sub == Question.QuestionSubject.Christian)
        return 3;
    if(sub == Question.QuestionSubject.Druze)
        return 4;
    if(sub == Question.QuestionSubject.Bedouin)
        return 5;
    if(sub == Question.QuestionSubject.Ethiopian)
        return 6;
    if(sub == Question.QuestionSubject.Moroccan)
        return 7;

    return 0;

}
    private Question.QuestionSubject getSubject(String s) {
        if (s.equals(R.string.question_subject_general))
            return Question.QuestionSubject.General;
        if (s.equals(R.string.question_subject_jewish))
            return Question.QuestionSubject.Jewish;
        if (s.equals(R.string.question_subject_christian))
            return Question.QuestionSubject.Christian;
        if (s.equals(R.string.question_subject_muslim))
            return Question.QuestionSubject.Muslim;
        if (s.equals(R.string.question_subject_druze))
            return Question.QuestionSubject.Druze;
        if (s.equals(R.string.question_subject_bedouin))
            return Question.QuestionSubject.Bedouin;
        if (s.equals(R.string.question_subject_ethiopian))
            return Question.QuestionSubject.Ethiopian;
        if (s.equals(R.string.question_subject_moroccan))
            return Question.QuestionSubject.Moroccan;
        return Question.QuestionSubject.General;

    }


    private boolean checkBeforeUpdate(String type) {
        if (content.getText().toString().length() == 0) {
            Toast.makeText(getContext(), "יש לכתוב תוכן לשאלה", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (type.equals("שאלת נכון/לא נכון")) {
            if (first_ans.getText().toString().length() == 0 || second_ans.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "יש לכתוב תוכן לתשובות האפשריות", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!((first_check.isChecked() && !second_check.isChecked()) || (!first_check.isChecked() && second_check.isChecked()))) {
                Toast.makeText(getContext(), "יש לבחור בדיוק תשובה אחת נכונה", Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        if (type.equals("שאלה אמריקאית")) {
            if (first_ans.getText().toString().length() == 0 || second_ans.getText().toString().length() == 0 ||
                    third_ans.getText().toString().length() == 0 || forth_ans.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "יש לכתוב תוכן לתשובות האפשריות", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!((first_check.isChecked() && !second_check.isChecked() && !third_check.isChecked() && !forth_check.isChecked())
                    || (second_check.isChecked() && !first_check.isChecked() && !third_check.isChecked() && !forth_check.isChecked())
                    || (third_check.isChecked() && !second_check.isChecked() && !first_check.isChecked() && !forth_check.isChecked())
                    || (forth_check.isChecked() && !second_check.isChecked() && !third_check.isChecked() && !first_check.isChecked()))) {
                Toast.makeText(getContext(), "יש לבחור בדיוק תשובה אחת נכונה", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        if (type.equals("שאלת סימון תשובות")) {
            if (first_ans.getText().toString().length() == 0 || second_ans.getText().toString().length() == 0 ||
                    third_ans.getText().toString().length() == 0 || forth_ans.getText().toString().length() == 0 || fifth_ans.getText().toString().length() == 0) {
                Toast.makeText(getContext(), "יש לכתוב תוכן לתשובות האפשריות", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!forth_check.isChecked() && !second_check.isChecked() && !third_check.isChecked() && !first_check.isChecked()) {
                Toast.makeText(getContext(), "יש לבחור לפחות תשובה אחת נכונה", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return true;

    }

    private void typeMode(String type) {
        ArrayList<String> ans = question.getPossible_answers();
        for (int i = 0; i < 5 - ans.size(); i++)
            ans.add("");
        if (type.equals("שאלת נכון/לא נכון")) {
            first_ans.setText(ans.get(0));
            second_ans.setText(ans.get(1));
            third_ans.setVisibility(View.GONE);
            forth_ans.setVisibility(View.GONE);
            fifth_ans.setVisibility(View.GONE);
            third_check.setVisibility(View.GONE);
            forth_check.setVisibility(View.GONE);
            fifth_check.setVisibility(View.GONE);

            if (question.getRight_answers().contains(first_ans.getText().toString()))
                first_check.setChecked(true);
            else
                second_check.setChecked(true);


        }

        if (type.equals("שאלת סימון תשובות")) {
            first_ans.setVisibility(View.VISIBLE);
            first_ans.setText(ans.get(0));
            second_ans.setText(ans.get(1));
            third_ans.setText(ans.get(2));
            forth_ans.setText(ans.get(3));
            fifth_ans.setText(ans.get(4));


            third_ans.setVisibility(View.VISIBLE);
            forth_ans.setVisibility(View.VISIBLE);
            fifth_ans.setVisibility(View.VISIBLE);
            third_check.setVisibility(View.VISIBLE);
            forth_check.setVisibility(View.VISIBLE);
            fifth_check.setVisibility(View.VISIBLE);


            if (question.getRight_answers().contains(first_ans.getText().toString()))
                first_check.setChecked(true);
            if (question.getRight_answers().contains(second_ans.getText().toString()))
                second_check.setChecked(true);
            if (question.getRight_answers().contains(third_ans.getText().toString()))
                third_check.setChecked(true);
            if (question.getRight_answers().contains(forth_ans.getText().toString()))
                forth_check.setChecked(true);
            if (question.getRight_answers().contains(fifth_ans.getText().toString()))
                fifth_check.setChecked(true);
        }
        if (type.equals("שאלה אמריקאית")) {
            first_ans.setText(ans.get(0));
            second_ans.setText(ans.get(1));
            third_ans.setText(ans.get(2));
            forth_ans.setText(ans.get(3));
            fifth_ans.setVisibility(View.GONE);
            fifth_check.setVisibility(View.GONE);

            third_ans.setVisibility(View.VISIBLE);
            forth_ans.setVisibility(View.VISIBLE);
            third_check.setVisibility(View.VISIBLE);
            forth_check.setVisibility(View.VISIBLE);


            if (question.getRight_answers().contains(first_ans.getText().toString()))
                first_check.setChecked(true);
            if (question.getRight_answers().contains(second_ans.getText().toString()))
                second_check.setChecked(true);
            if (question.getRight_answers().contains(third_ans.getText().toString()))
                third_check.setChecked(true);
            if (question.getRight_answers().contains(forth_ans.getText().toString()))
                forth_check.setChecked(true);

        }


    }
}

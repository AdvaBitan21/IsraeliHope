package com.technion.android.israelihope;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.android.israelihope.Objects.Question;

import java.util.ArrayList;

public class Utils {
    public enum QuestionType {
        YesNo,
        Close,
        CheckBox
    }

    public enum UserType{
        A,
        B,
        C,
        D

    }

    public static int AMOUNT_OF_QUESTIONS_FIRST_QUIZ = 20;



    public static void uploadQuestionToFirebase()  {
        String content="בצום הרמאדן אסור לאכול אבל מותר לשתות";
        Utils.QuestionType questionType= QuestionType.YesNo;
        ArrayList<String> possible_answers=new ArrayList<>();
        possible_answers.add("נכון");
        possible_answers.add("לא נכון");

        ArrayList<String> right_answers=new ArrayList<>();
        right_answers.add("לא נכון");
        String from_email="";
        String to_email="";
        int firstQuizIndex=1;
        Utils.UserType subject=Utils.UserType.A;

        Question q = new Question("",content,questionType,possible_answers,right_answers,from_email,to_email,firstQuizIndex,subject);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newQuestion = db.collection("Questions").document();
        q.setId(newQuestion.getId());
        newQuestion.set(q).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Log.d("Utils", "Uploaded book: ".concat(t));
                        //Toast.makeText(LoginActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        //Toast.makeText(LoginActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


}

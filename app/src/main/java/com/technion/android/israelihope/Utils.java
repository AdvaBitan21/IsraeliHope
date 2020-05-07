package com.technion.android.israelihope;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.technion.android.israelihope.Objects.Question;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Utils {

    public enum QuestionType {
        YesNo,
        Close,
        CheckBox
    }

    public enum UserType {
        A,
        B,
        C,
        D
    }

    public static int AMOUNT_OF_QUESTIONS_FIRST_QUIZ = 20;
    public static int OPEN_GALLERY_REQUEST = 1;


    public static void uploadQuestionToFirebase() {
        String content = "בצום הרמאדן אסור לאכול אבל מותר לשתות";
        Utils.QuestionType questionType = QuestionType.YesNo;
        ArrayList<String> possible_answers = new ArrayList<>();
        possible_answers.add("נכון");
        possible_answers.add("לא נכון");

        ArrayList<String> right_answers = new ArrayList<>();
        right_answers.add("לא נכון");
        String from_email = "";
        String to_email = "";
        int firstQuizIndex = 1;
        Utils.UserType subject = Utils.UserType.A;

        Question q = new Question("", content, questionType, possible_answers, right_answers, from_email, to_email, firstQuizIndex, subject);
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

    /**
     * Loads a users profile picture into the desired imageView.
     *
     * @param imageView the imageView to load the picture into.
     * @param email     the email of the user
     */
    public static void loadProfileImage(final Context context, final ImageView imageView, String email) {
        FirebaseStorage.getInstance().getReference().child("profileImages/" + email + ".jpeg")
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context).load(task.getResult()).into(imageView);
            }
        });
    }


    /**
     * Enables/Disables all child views in a view group.
     *
     * @param viewGroup the view group
     * @param enabled   true to enable, false to disable
     */
    public static void enableDisableClicks(Activity activity, ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableClicks(activity, (ViewGroup) view, enabled);
            }
        }
    }


}

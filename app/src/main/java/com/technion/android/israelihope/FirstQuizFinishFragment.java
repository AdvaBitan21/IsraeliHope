package com.technion.android.israelihope;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.android.israelihope.Objects.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

public class FirstQuizFinishFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_quiz_finish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        double rights = (double) ((FirstQuizActivity) getActivity()).getRightsAmount();
        final int score = (int) (Math.round(100 * rights / Utils.AMOUNT_OF_QUESTIONS_FIRST_QUIZ));

        TextView scoreTextView = ((TextView) getActivity().findViewById(R.id.score));
        scoreTextView.setText("" + score);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userRef.update("score_first_quiz", score).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void aVoid) {

                                                                               getActivity().findViewById(R.id.go_homepage).setOnClickListener(new View.OnClickListener() {
                                                                                   @Override
                                                                                   public void onClick(View view) {
                                                                                       String role = "";
                                                                                       if (((MainActivity) getActivity()).mUser.getAcademicRole() == User.AcademicRole.Student)
                                                                                           role = "Students";
                                                                                       else
                                                                                           role = "" + ((MainActivity) getActivity()).mUser.getAcademicRole();
                                                                                       final DocumentReference statRef = db.collection("Statistics").document("FirstQuizHistogram_" + role);
                                                                                       statRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                               @Override
                                                                                                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                   if (task.isSuccessful()) {
                                                                                                                                       StatisticsFragment.HistogramObject document = task.getResult().toObject(StatisticsFragment.HistogramObject.class);
                                                                                                                                       document.addToHistByUser(Utils.gradeRange(score));
                                                                                                                                       statRef.update("hist", document.getHist()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                           @Override
                                                                                                                                           public void onSuccess(Void aVoid) {
                                                                                                                                               Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                                                                                               startActivity(intent);
                                                                                                                                               getActivity().finish();
                                                                                                                                           }
                                                                                                                                       });
                                                                                                                                   }
                                                                                                                               }
                                                                                                                           }
                                                                                       );
                                                                                   }

                                                                               });


                                                                               SetGoToHomepageButton();
                                                                           }
                                                                       }
        );
    }


    private void SetGoToHomepageButton() {
        getActivity().findViewById(R.id.go_homepage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //go to homepage fragment
            }
        });
    }



}

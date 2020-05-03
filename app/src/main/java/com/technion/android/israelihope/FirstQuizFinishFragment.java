package com.technion.android.israelihope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirstQuizFinishFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_quiz_welcome_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        double rights =(double)((MainActivity)getActivity()).getFirstQuizRightsAmount();
        int score = (int)(Math.round(rights/Utils.AMOUNT_OF_QUESTIONS_FIRST_QUIZ));
        ((TextView)getActivity().findViewById(R.id.score)).setText(score);

        FirebaseFirestore db =FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userRef.update("score_first_quiz",score);
        
        SetGoToHomepageButton();




    }

    private void SetGoToHomepageButton(){
        getActivity().findViewById(R.id.go_homepage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //go to homepage fragment
            }});
    }
}

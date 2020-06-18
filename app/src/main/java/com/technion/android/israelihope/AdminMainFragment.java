package com.technion.android.israelihope;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.android.israelihope.Objects.Question;

import java.util.HashMap;
import java.util.Map;

public class AdminMainFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        return inflater.inflate(R.layout.fragment_main_admin, null);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().findViewById(R.id.all_questions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(new ShowAllQuestionsFragment());
            }
        });

        getActivity().findViewById(R.id.add_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question dummy = new Question();
                ((MainActivity)getActivity()).loadFragment(new EditQuestionFragment(dummy,true));

            }
        });
        getActivity().findViewById(R.id.disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(),LoginActivity.class);
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                startActivity(intent);
                getActivity().finish();

            }});
    }

/*
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_admin);


        // Make status bar white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        findViewById(R.id.all_questions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ShowAllQuestionsFragment());
            }
        });

       /* findViewById(R.id.add_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AddQuestionFragment());
            }
        });
        findViewById(R.id.add_gift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AddGiftFragment());
            }
        });*//*
        findViewById(R.id.disconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> removeToken = new HashMap<>();
                removeToken.put("token_id", "");
                db.collection("Users").document(mAuth.getCurrentUser().getEmail()).update(removeToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(AdminMainFragment.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });




    }*/






}

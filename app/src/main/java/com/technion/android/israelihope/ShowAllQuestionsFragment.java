package com.technion.android.israelihope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Adapters.AllQuestionsListAdapter;
import com.technion.android.israelihope.Adapters.QuestionsAdapter;
import com.technion.android.israelihope.Objects.Question;

import java.util.ArrayList;
import java.util.List;

public class ShowAllQuestionsFragment extends Fragment {

    List<Question> questionsList ;
    AllQuestionsListAdapter myAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_question, null);
    }

    public  ShowAllQuestionsFragment(){}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        questionsList = new ArrayList<>();
        RecyclerView myRv = getActivity().findViewById(R.id.List);

        AllQuestionsListAdapter.OnItemClickListener listener = new AllQuestionsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Question item) {
                ((MainActivity)getActivity()).loadFragment(new EditQuestionFragment(item,false));

            }
        };

        myAdapter = new AllQuestionsListAdapter(getContext(),questionsList, listener);
        myRv.setLayoutManager(new LinearLayoutManager(getContext()));
        myRv.setAdapter(myAdapter);



        getAllQuestions();




    }


    private void getAllQuestions(){
        final List<Question> questions=new ArrayList<>() ;

        Query query = FirebaseFirestore.getInstance().collection("Questions").orderBy("first_quiz_index");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult()) {
                    questions.add(document.toObject(Question.class));
                }
                questionsList.addAll(questions);
                myAdapter.notifyDataSetChanged();


            }
        });}




}

package com.technion.android.israelihope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Adapters.UserAdapter;
import com.technion.android.israelihope.Objects.Chatlist;
import com.technion.android.israelihope.Objects.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private ArrayList<User> mUsers;

    FirebaseUser firebaseUser;

    private ArrayList<String> usersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<String>();

        Query query = FirebaseFirestore.getInstance().collection("Chatlist");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                usersList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Chatlist chatlist = document.toObject(Chatlist.class);
                    if(chatlist.email.equals(firebaseUser.getEmail())){
                        usersList = chatlist.getEmails();
                    }
                }

                chatList();
            }
        });
    }


    private void chatList() {
        mUsers = new ArrayList<>();
        CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
        requestCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                mUsers.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    User user = document.toObject(User.class);
                    if (usersList.contains(user.getEmail()))
                        mUsers.add(user);
                }
                userAdapter = new UserAdapter(getContext(), mUsers, new ArrayList<User>(), true);
                recyclerView.setAdapter(userAdapter);
            }
        });

    }

}
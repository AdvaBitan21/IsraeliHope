package com.technion.android.israelihope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> mUsers;

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

        usersList = new ArrayList<String>();

        Query query = FirebaseFirestore.getInstance().collection("Chatlist");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                usersList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Chatlist chatlist = document.toObject(Chatlist.class);
                    if (chatlist.email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        usersList = chatlist.getEmails();
                    }
                }
            }
        });

        chatList();
    }


    private void chatList() {
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), mUsers, new ArrayList<User>(), true);
        recyclerView.setAdapter(userAdapter);

        CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
        requestCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    if (dc.getType().equals(DocumentChange.Type.MODIFIED)) {
                        User user = dc.getDocument().toObject(User.class);
                        if (mUsers.contains(user)) {
                            int index = mUsers.indexOf(user);
                            mUsers.remove(user);
                            mUsers.add(index, user);
                            userAdapter.notifyItemChanged(mUsers.indexOf(user), Utils.STATUS_CHANGE_PAYLOAD);
                        }
                    }
                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                        User user = dc.getDocument().toObject(User.class);
                        if (usersList.contains(user.getEmail()))
                            mUsers.add(user);
                        userAdapter.notifyItemInserted(mUsers.size() - 1);
                    }
                }
            }
        });

    }

}
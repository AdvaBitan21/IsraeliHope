package com.technion.android.israelihope;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser firebaseUser;

    private List<Chatlist> usersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chats_fragment, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        Query query = FirebaseFirestore.getInstance().collection("Chatlist");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                usersList.clear();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Chatlist chatlist = document.toObject(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();
            }
        });
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    usersList.clear();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Chatlist chatlist = document.toObject(Chatlist.class);
//                        usersList.add(chatlist);
//                    }
//
//                    chatList();
//                }
//            }
//        });
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
                   for (Chatlist chatlist : usersList) {
                       if(user.getEmail().equals(chatlist.getEmail()) && !user.getEmail().equals(firebaseUser.getEmail())){
                           mUsers.add(user);
                       }
                   }
               }
               userAdapter = new UserAdapter(getContext(), mUsers, true);
               recyclerView.setAdapter(userAdapter);
           }
       });

    }
}

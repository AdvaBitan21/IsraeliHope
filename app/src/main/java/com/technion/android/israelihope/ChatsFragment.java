package com.technion.android.israelihope;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Adapters.ConversationsAdapter;
import com.technion.android.israelihope.Objects.Conversation;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ConversationsAdapter adapter;
    private ArrayList<Conversation> mConversations;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mConversations = new ArrayList<>();
        adapter = new ConversationsAdapter(getContext(), mConversations);

        RecyclerView recyclerView = getView().findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        Query query = db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("Conversations")
                .orderBy("lastMessageTime", Query.Direction.ASCENDING);

        //Listening to conversations updates in order to get the latest conversations on top
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                    //On conversation addition - add it to the top
                    if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                        Conversation conversation = dc.getDocument().toObject(Conversation.class);
                        mConversations.add(0, conversation);
                        adapter.notifyItemInserted(0);
                    }

                    //On conversation change - move it to the top
                    if (dc.getType().equals(DocumentChange.Type.MODIFIED)) {
                        Conversation conversation = dc.getDocument().toObject(Conversation.class);
                        int index = mConversations.indexOf(conversation);
                        if (index == 0) {
                            mConversations.set(0, conversation);
                            adapter.notifyItemChanged(0, Utils.LAST_MESSAGE_CHANGE_PAYLOAD);
                            return;
                        }
                        mConversations.remove(conversation);
                        mConversations.add(0, conversation);
                        adapter.notifyItemMoved(index, 0);
                        adapter.notifyItemChanged(0, Utils.LAST_MESSAGE_CHANGE_PAYLOAD);
                    }
                }
            }
        });
    }

}
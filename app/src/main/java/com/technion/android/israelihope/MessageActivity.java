package com.technion.android.israelihope;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Adapters.MessageAdapter;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.Chatlist;
import com.technion.android.israelihope.Objects.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MessageActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;

    ImageButton btn_send;
    Button btn_challenge;
    EditText txt_send;
    TextView user_name;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;
    String userEmail;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.messages_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        btn_send = findViewById(R.id.btn_send);
        btn_challenge = findViewById(R.id.btn_challenge);
        txt_send = findViewById(R.id.message_editText);
        user_name = findViewById(R.id.user_name);


        intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        String userName = intent.getStringExtra("userName");
        user_name.setText(userName);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txt_send.getText().toString();
                sendMessage(firebaseUser.getEmail(), userEmail, msg);
                txt_send.setText("");
            }
        });





        //TODO challenge page
//        btn_challenge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        FirebaseFirestore.getInstance().collection("Users").document(userEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        User user = document.toObject(User.class);
                        readMessage(firebaseUser.getEmail(), user.getEmail());
                    }
                }
            }
        });
    }


    private void sendMessage(final String sender, final String receiver, String message){
        if(message.isEmpty())
            return;

        HashMap<String, Object> chat = new HashMap<>();
        chat.put("sender", sender);
        chat.put("receiver", receiver);
        chat.put("message", message);

        FieldValue timestamp = FieldValue.serverTimestamp();
        chat.put("messageTime", timestamp);

        FirebaseFirestore.getInstance().collection("Chats").add(chat);

        FirebaseFirestore.getInstance().collection("Chatlist").document(sender).set(new Chatlist(sender));
        FirebaseFirestore.getInstance().collection("Chatlist").document(receiver).set(new Chatlist(receiver));

        readMessage(sender, receiver);

    }

    private void readMessage(final String myEmail, final String userEmail){
        mchat = new ArrayList<>();
        final ArrayList<String> isSender = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Chats")
                .orderBy("messageTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        mchat.clear();
                        isSender.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Chat chat = document.toObject(Chat.class);
                            if (chat.getReceiver().equals(myEmail) && chat.getSender().equals(userEmail)) {
                                mchat.add(chat);
                                isSender.add(userEmail);
                            }
                            if (chat.getReceiver().equals(userEmail) && chat.getSender().equals(myEmail)) {
                                mchat.add(chat);
                                isSender.add(myEmail);
                            }
                        }
                        messageAdapter = new MessageAdapter(MessageActivity.this, mchat, isSender);
//                        messageAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(messageAdapter);

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.status("online");
    }

}

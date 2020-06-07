package com.technion.android.israelihope;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Adapters.MessageAdapter;
import com.technion.android.israelihope.Dialogs.AddContentToChatDialog;
import com.technion.android.israelihope.Objects.Challenge;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.Chatlist;
import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.Objects.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageActivity extends AppCompatActivity implements Serializable {

    User senderUser;
    User receiverUser;

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<Chat> mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Change statusBar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        Intent intent = getIntent();
        receiverUser = (User) intent.getSerializableExtra("receiver");
        senderUser = (User) intent.getSerializableExtra("sender");

        ((TextView) findViewById(R.id.user_name)).setText(receiverUser.getUserName());

        initSendMessageComponents();
        initAddContentButton();
        initChat();
    }


    private void initAddContentButton() {
        findViewById(R.id.btn_challenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, AddContentToChatDialog.class);
                startActivityForResult(intent, Utils.SEND_CHALLENGE_REQUEST);
            }
        });
    }

    private void initSendMessageComponents() {

        final ImageButton btn_send = findViewById(R.id.btn_send);
        final EditText txt_send = findViewById(R.id.message_editText);

        txt_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txt_send.getText().toString().trim().isEmpty()) {
                    btn_send.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Grey)));
                    btn_send.setEnabled(false);
                } else {
                    btn_send.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                    btn_send.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = txt_send.getText().toString();
                sendMessage(senderUser.getEmail(), receiverUser.getEmail(), msg);
                txt_send.setText("");
            }
        });
    }

    private void initChat() {

        recyclerView = findViewById(R.id.messages_view);
        recyclerView.setHasFixedSize(true);

        //start messages from bottom
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //scroll when keyboard is shown
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        readMessages();
    }

    private void readMessages() {

        //initial setup of the adapter
        mChat = new ArrayList<>();
        final ArrayList<DocumentReference> mDocuments = new ArrayList<>();
        messageAdapter = new MessageAdapter(MessageActivity.this, mChat, mDocuments);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

        //listening to message additions
        FirebaseFirestore.getInstance().collection("Chats")
                .orderBy("messageTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            if (dc.getType().equals(DocumentChange.Type.ADDED)) {
                                Chat chat = dc.getDocument().toObject(Chat.class);
                                if (chatBelongsToConversation(chat)) {
                                    mChat.add(chat);
                                    mDocuments.add(dc.getDocument().getReference());
                                    messageAdapter.notifyItemInserted(mChat.size() - 1);
                                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                                }
                            }
                        }
                    }
                });
    }


    public void sendChallenge(Question question) {
        if (question == null)
            return;

        final String sender = senderUser.getEmail();
        final String receiver = receiverUser.getEmail();

        Chat chat = new Chat(sender, receiver, Timestamp.now(), new Challenge(question.getId()));
        FirebaseFirestore.getInstance().collection("Chats").add(chat);

        updateUsersChatlists();
    }

    private void sendMessage(final String sender, final String receiver, String message) {
        if (message.isEmpty())
            return;

        Chat chat = new Chat(sender, receiver, Timestamp.now(), message);
        FirebaseFirestore.getInstance().collection("Chats").add(chat);

        updateUsersChatlists();
    }

    private void updateUsersChatlists() {

        final DocumentReference senderReference = FirebaseFirestore.getInstance().collection("Chatlist").document(senderUser.getEmail());
        senderReference.update("emails", FieldValue.arrayRemove(receiverUser.getEmail()));
        senderReference.update("emails", FieldValue.arrayUnion(receiverUser.getEmail()));

        final DocumentReference receiverReference = FirebaseFirestore.getInstance().collection("Chatlist").document(receiverUser.getEmail());
        receiverReference.update("emails", FieldValue.arrayRemove(senderUser.getEmail()));
        receiverReference.update("emails", FieldValue.arrayUnion(senderUser.getEmail()));
    }


    private boolean chatBelongsToConversation(Chat chat) {
        return (chat.getReceiver().equals(senderUser.getEmail()) && chat.getSender().equals(receiverUser.getEmail())) ||
                (chat.getReceiver().equals(receiverUser.getEmail()) && chat.getSender().equals(senderUser.getEmail()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.status("online");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SEND_CHALLENGE_REQUEST && data != null) {
            Question question = (Question) data.getSerializableExtra("question");
            if (question != null)
                sendChallenge(question);
        }
    }
}

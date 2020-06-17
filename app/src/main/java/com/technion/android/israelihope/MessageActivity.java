package com.technion.android.israelihope;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
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
import com.google.firebase.firestore.SetOptions;
import com.technion.android.israelihope.Adapters.MessageAdapter;
import com.technion.android.israelihope.Dialogs.AddContentToChatDialog;
import com.technion.android.israelihope.Objects.Challenge;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.Conversation;
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

    private User senderUser;
    private User receiverUser;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Chat> mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Change statusBar color
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.lightGrey));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Intent intent = getIntent();
        receiverUser = (User) intent.getSerializableExtra("receiver");
        senderUser = (User) intent.getSerializableExtra("sender");

        ((TextView) findViewById(R.id.user_name)).setText(receiverUser.getUserName());

        initBackButton();
        initSendMessageComponents();
        initAddContentButton();
        initChat();
    }


    private void initAddContentButton() {
        findViewById(R.id.btn_challenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, AddContentToChatDialog.class);
                startActivityForResult(intent, Utils.ADD_CONTENT_REQUEST);
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

    private void initBackButton() {
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                            if (recyclerView.getAdapter().getItemCount() > 0) {
                                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                            }
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
                                    clearUnseenCountInConversation();
                                }
                            }
                        }
                    }
                });
    }


    public void sendPicture(final Uri uri) {
        if (uri == null)
            return;

        final String sender = senderUser.getEmail();
        final String receiver = receiverUser.getEmail();

        final Chat chat = new Chat(sender, receiver, Timestamp.now(), getPictureString(uri));
        FirebaseFirestore.getInstance().collection("Chats").add(chat).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                addChatToUsersConversations(chat, task.getResult().getId());
            }
        });
    }

    public void sendChallenge(Question question) {
        if (question == null)
            return;

        final String sender = senderUser.getEmail();
        final String receiver = receiverUser.getEmail();

        final Chat chat = new Chat(sender, receiver, Timestamp.now(), new Challenge(question.getId()));
        FirebaseFirestore.getInstance().collection("Chats").add(chat).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                addChatToUsersConversations(chat, task.getResult().getId());
            }
        });
    }

    private void sendMessage(final String sender, final String receiver, String message) {
        if (message.isEmpty())
            return;

        final Chat chat = new Chat(sender, receiver, Timestamp.now(), getMessageString(message));
        FirebaseFirestore.getInstance().collection("Chats").add(chat).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                addChatToUsersConversations(chat, task.getResult().getId());
            }
        });
    }

    private void addChatToUsersConversations(final Chat chat, final String chatId) {

        //Sender - current chat is the last message, all messages seen.
        Conversation senderConversation = new Conversation(receiverUser.getEmail(), chat.getMessageTime(), chatId);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(senderUser.getEmail())
                .collection("Conversations")
                .document(receiverUser.getEmail())
                .set(senderConversation);

        //Receiver - current chat is the last message, increment unseenCount.
        final Conversation receiverConversation = new Conversation(senderUser.getEmail(), chat.getMessageTime(), chatId);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(receiverUser.getEmail())
                .collection("Conversations")
                .document(senderUser.getEmail())
                .set(receiverConversation);
    }

    private void clearUnseenCountInConversation() {

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(senderUser.getEmail())
                .collection("Conversations")
                .document(receiverUser.getEmail())
                .update("unseenCount", 0);
    }


    private String getMessageString(String message) {
        return "MESSAGE:" + message;
    }

    private String getPictureString(Uri uri) {
        return "PICTURE:" + uri.toString();
    }

    private boolean chatBelongsToConversation(Chat chat) {
        return (chat.getReceiver().equals(senderUser.getEmail()) && chat.getSender().equals(receiverUser.getEmail())) ||
                (chat.getReceiver().equals(receiverUser.getEmail()) && chat.getSender().equals(senderUser.getEmail()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearUnseenCountInConversation();
        Utils.status("offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.status("online");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.ADD_CONTENT_REQUEST && data != null) {
            String contentType = data.getStringExtra("contentType");

            if (contentType.equals("CHALLENGE")) {
                Question question = (Question) data.getSerializableExtra("question");
                if (question != null)
                    sendChallenge(question);
            }

            if (contentType.equals("PICTURE")) {
                Uri imageUri = data.getData();
                if (imageUri != null)
                    sendPicture(imageUri);
            }
        }
    }
}

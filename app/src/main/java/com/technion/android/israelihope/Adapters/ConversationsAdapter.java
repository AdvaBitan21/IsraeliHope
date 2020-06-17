package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.MainActivity;
import com.technion.android.israelihope.MessageActivity;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.Conversation;
import com.technion.android.israelihope.Objects.User;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Conversation> mConversations;

    public ConversationsAdapter(Context mContext, ArrayList<Conversation> mConversations) {
        this.mContext = mContext;
        this.mConversations = mConversations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, parent, false);
        return new ConversationsAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        bindUser(holder, position);
        bindLastMessage(holder, position);
        initUnseenCount(holder, position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.get(0).equals(Utils.LAST_MESSAGE_CHANGE_PAYLOAD)) {
                //bindUnseenCount(holder, position);
                bindLastMessage(holder, position);
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }


    private void bindUser(@NonNull final ViewHolder holder, final int position) {

        final Conversation conversation = mConversations.get(position);

        //Listening to user modifications for user details updates.
        FirebaseFirestore.getInstance().collection("Users").document(conversation.getEmail())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        final User user = documentSnapshot.toObject(User.class);
                        holder.username.setText(user.getUserName());
                        Utils.loadProfileImage(mContext, holder.profile_image, user.getEmail());
                        bindUserStatus(holder, user);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, MessageActivity.class);
                                intent.putExtra("receiver", user);
                                intent.putExtra("sender", ((MainActivity) mContext).getCurrentUser());
                                mContext.startActivity(intent);
                                conversation.clearUnseen();
                                holder.unseenCount.setVisibility(View.GONE);
                            }
                        });
                    }
                });
    }

    private void bindUserStatus(@NonNull ViewHolder holder, User user) {
        if (user.getStatus().equals("online")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }
    }

    private void bindLastMessage(final ViewHolder holder, final int position) {

        final Conversation conversation = mConversations.get(position);

        FirebaseFirestore.getInstance()
                .collection("Chats")
                .document(conversation.getLastMessageId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Chat chat = task.getResult().toObject(Chat.class);
                bindLastMessageUI(holder, chat);
                if (!chat.isSeen() && chatSentToMe(chat, position)) {
                    increaseUnseenCount(holder);
                }
            }
        });
    }

    private void bindLastMessageUI(final ViewHolder holder, Chat chat) {
        switch (chat.getType()) {
            case TEXT:
                holder.last_msg.setText(chat.getMessage());
                holder.last_msg_icon.setVisibility(View.GONE);
                break;
            case CHALLENGE:
                holder.last_msg.setText(mContext.getString(R.string.challenge));
                holder.last_msg_icon.setVisibility(View.VISIBLE);
                holder.last_msg_icon.setImageDrawable(mContext.getDrawable(R.drawable.question_filled));
                break;
            case PICTURE:
                holder.last_msg.setText(mContext.getString(R.string.picture));
                holder.last_msg_icon.setVisibility(View.VISIBLE);
                holder.last_msg_icon.setImageDrawable(mContext.getDrawable(R.drawable.camera_filled));
                break;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chat.getMessageTime().toDate());
        holder.last_msg_time.setText(Utils.getTimeStringForChat(mContext, calendar));
    }

    private void initUnseenCount(@NonNull final ViewHolder holder, final int position) {
        FirebaseFirestore.getInstance().collection("Chats")
                .whereEqualTo("receiver", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .whereEqualTo("sender", mConversations.get(position).getEmail())
                .whereEqualTo("seen", false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    increaseUnseenCount(holder);
                }

                //The last one happens in bindLastMessage
                decreaseUnseenCount(holder);
            }
        });
    }

    private void increaseUnseenCount(@NonNull ViewHolder holder) {
        if (holder.unseenCount.getVisibility() == View.VISIBLE) {
            int unseenCount = Integer.parseInt(holder.unseenCount.getText().toString()) + 1;
            holder.unseenCount.setText(String.valueOf(unseenCount));
        } else {
            holder.unseenCount.setVisibility(View.VISIBLE);
            holder.unseenCount.setText("1");
        }
    }

    private void decreaseUnseenCount(@NonNull ViewHolder holder) {
        if (holder.unseenCount.getVisibility() == View.VISIBLE) {
            int unseenCount = Integer.parseInt(holder.unseenCount.getText().toString()) - 1;
            if (unseenCount == 0)
                holder.unseenCount.setVisibility(View.GONE);
            else
                holder.unseenCount.setText(String.valueOf(unseenCount));
        }
    }


    private boolean chatSentToMe(Chat chat, int position) {

        String receiverEmail = mConversations.get(position).getEmail();
        String myEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        return (chat.getReceiver().equals(myEmail) && chat.getSender().equals(receiverEmail));
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        private TextView last_msg_time;
        private ImageView last_msg_icon;
        private TextView unseenCount;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            last_msg_icon = itemView.findViewById(R.id.last_msg_icon);
            last_msg_time = itemView.findViewById(R.id.last_mag_time);
            unseenCount = itemView.findViewById(R.id.unseenCount);
        }
    }

}

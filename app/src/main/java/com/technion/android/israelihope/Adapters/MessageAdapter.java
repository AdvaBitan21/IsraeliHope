package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_RIGHT = 0;
    public static final int MSG_TYPE_LEFT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private List<String> isSender;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChat, List<String> isSender){
        this.mContext = mContext;
        this.mChat = mChat;
        this.isSender = isSender;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.my_message, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.their_message, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        SimpleDateFormat simple_format = new SimpleDateFormat("HH:mm");
        Timestamp time = chat.getMessageTime();
        if(time == null){
            return;
        }
        holder.message_time.setText(simple_format.format(time.toDate()));

        holder.show_message.setText(chat.getMessage());


        Utils.loadProfileImage(mContext, holder.profile_image, isSender.get(position));

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getEmail())){
            return MSG_TYPE_RIGHT;
        }
        else
            return MSG_TYPE_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        public TextView message_time;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.message_body);
            profile_image = itemView.findViewById(R.id.avatar);
            message_time = itemView.findViewById(R.id.message_time);
        }

    }
}
package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.MainActivity;
import com.technion.android.israelihope.MessageActivity;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.User;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<User> mUsers;
    private ArrayList<User> allUsers;
    private ArrayList<User> diffUsers;
    private ArrayList<User> original;


    public UserAdapter(Context mContext, ArrayList<User> mUsers, ArrayList<User> allUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.diffUsers = mUsers;
        this.allUsers = allUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username.setText(user.getUserName());

        Utils.loadProfileImage(mContext, holder.profile_image, user.getEmail());

        holder.img_on.setVisibility(View.GONE);
        holder.img_off.setVisibility(View.GONE);
        holder.last_msg.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("receiver", user);
                intent.putExtra("sender", ((MainActivity) mContext).getCurrentUser());
                mContext.startActivity(intent);
            }
        });
    }

    private void bindUserStatus(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        if (user.getStatus().equals("online")) {
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        if (!payloads.isEmpty()) {
            if (payloads.get(0).equals(Utils.STATUS_CHANGE_PAYLOAD)) {
                bindUserStatus(holder, position);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void bindLastMessage(final ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore.getInstance()
                .collection("Chats")
                .orderBy("messageTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Chat chat = document.toObject(Chat.class);
                            if (chatBelongsToConversation(chat, user.getEmail(), firebaseUser.getEmail())) {
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
                        }
                    }
                });
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<User> results = new ArrayList<User>();
                if (original == null)
                    original = allUsers;

                if (constraint != null) {

                    if (original != null && original.size() > 0) {
                        for (final User user : allUsers) {
                            if (user.getUserName().contains(constraint.toString()))
                                results.add(user);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                mUsers = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void searchRandomDifferentUsers() {
        int num_of_random_results = Math.min(3, diffUsers.size());
        Random rand = new Random();
        ArrayList<User> randList = new ArrayList<>();
        int i = 0;
        while (i < num_of_random_results) {


            int randomIndex = rand.nextInt(diffUsers.size());
            User u = diffUsers.get(randomIndex);
            if (!randList.contains(u)) {
                i++;
                randList.add(u);

            }
        }
        mUsers = randList;
        notifyDataSetChanged();

    }

    private boolean chatBelongsToConversation(Chat chat, String email1, String email2) {
        return (chat.getReceiver().equals(email1) && chat.getSender().equals(email2)) ||
                (chat.getReceiver().equals(email2) && chat.getSender().equals(email1));
    }

    private boolean currentUserIsSender(Chat chat) {
        return chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;
        private TextView last_msg_time;
        private ImageView last_msg_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);
            last_msg_icon = itemView.findViewById(R.id.last_msg_icon);
            last_msg_time = itemView.findViewById(R.id.last_mag_time);
        }
    }


}

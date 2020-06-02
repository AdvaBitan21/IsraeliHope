package com.technion.android.israelihope.Adapters;

import android.app.Activity;
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
import com.technion.android.israelihope.MessageActivity;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.User;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.util.ArrayList;
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

    private boolean isChat;
    private ArrayList<User> original;


    String theLastMessage;

    public UserAdapter(Context mContext, ArrayList<User> mUsers, ArrayList<User> allUsers, boolean isChat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.diffUsers = mUsers;
        this.isChat = isChat;
        this.allUsers=allUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_chat_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username.setText(user.getUserName());
        holder.num_challenges.setText(String.valueOf(user.getNum_challenges()));

        Utils.loadProfileImage(mContext, holder.profile_image, user.getEmail());

        if (isChat) {
            lastMessage(user.getEmail(), holder.last_msg);

            if (user.getStatus().equals("online")) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
            holder.last_msg.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("userEmail", user.getEmail());
                intent.putExtra("userName", user.getUserName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView num_challenges;
        private TextView last_msg;


        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            num_challenges = itemView.findViewById(R.id.num_challenges);
            last_msg = itemView.findViewById(R.id.last_msg);
        }
    }

    private void lastMessage(final String userEmail, final TextView last_msg) {
        theLastMessage = "";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            return;

        Query query = FirebaseFirestore.getInstance().collection("Chats").orderBy("messageTime", Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Chat chat = document.toObject(Chat.class);
                    if ((chat.getReceiver().equals(firebaseUser.getEmail()) && chat.getSender().equals(userEmail)) ||
                            (chat.getReceiver().equals(userEmail) && chat.getSender().equals(firebaseUser.getEmail()))) {
                        theLastMessage = chat.getMessage();
                    }

                    switch (theLastMessage) {
                        case "":
                            break;

                        default:
                            last_msg.setText(theLastMessage);
                            break;
                    }

                    theLastMessage = "";
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

    public void searchRandomDiffrentUsers(){
        int num_of_random_results=3;
        Random rand = new Random();
        ArrayList<User> randList = new ArrayList<>();
        int i=0;
        while(i< num_of_random_results) {


            int randomIndex = rand.nextInt(diffUsers.size());
            User u=diffUsers.get(randomIndex);
            if(!randList.contains(u)){
                i++;
                randList.add(u);

            }
        }
        mUsers= randList;
        notifyDataSetChanged();

    }

}

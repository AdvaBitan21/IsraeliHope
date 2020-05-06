package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.MessageActivity;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Objects.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    String theLastMessage;

     public UserAdapter(Context mContext, List<User> mUsers){
         this.mContext = mContext;
         this.mUsers = mUsers;
     }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
         return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.username.setText(user.getUserName());
        Glide.with(mContext).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(holder.profile_image);
        holder.num_challenges.setText(user.getNum_challenges());

        lastMessage(user.getEmail(), holder.last_msg);

        if (user.getStatus().equals("online")){
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userEmail", user.getEmail());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
         public TextView username;
         public ImageView profile_image;
         private ImageView img_on;
         private ImageView img_off;
         private TextView num_challenges;
         private TextView last_msg;


         public ViewHolder(View itemView){
             super(itemView);

             username = itemView.findViewById(R.id.username);
             profile_image = itemView.findViewById(R.id.avatar);
             img_on = itemView.findViewById(R.id.img_on);
             img_off = itemView.findViewById(R.id.img_off);
             num_challenges = itemView.findViewById(R.id.num_challenges);
             last_msg = itemView.findViewById(R.id.last_msg);
         }
     }

     private  void lastMessage(final String userEmail, final TextView last_msg) {
         theLastMessage = "";
         final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                     Chat chat = snapshot.getValue(Chat.class);
                     if(chat.getReceiver().equals(firebaseUser.getEmail()) && chat.getSender().equals(userEmail) ||
                             chat.getReceiver().equals(userEmail) && chat.getSender().equals(firebaseUser.getEmail())){
                         theLastMessage = chat.getMessage();
                     }

                     switch (theLastMessage) {
                         case "":
                             last_msg.setText("אין הודעות");
                             break;

                         default:
                             last_msg.setText(theLastMessage);
                             break;
                     }

                     theLastMessage = "";
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }
}

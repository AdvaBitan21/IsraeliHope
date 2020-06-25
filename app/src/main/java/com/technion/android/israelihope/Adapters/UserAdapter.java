package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.technion.android.israelihope.MainActivity;
import com.technion.android.israelihope.MessageActivity;
import com.technion.android.israelihope.Objects.User;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<User> mUsers;         // Users whose type is different from mine.
    private ArrayList<User> allUsers;       // All users besides me.
    private ArrayList<User> original;


    public UserAdapter(Context mContext, ArrayList<User> mUsers, ArrayList<User> allUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.allUsers = allUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_search_user, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mUsers.get(position);
        holder.username.setText(user.getUserName());

        Utils.loadProfileImage(mContext, holder.profile_image, user.getEmail());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, MessageActivity.class);
            intent.putExtra("receiver", user);
            intent.putExtra("sender", ((MainActivity) mContext).getCurrentUser());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
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


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }


}

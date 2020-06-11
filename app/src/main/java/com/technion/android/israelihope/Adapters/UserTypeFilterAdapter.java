package com.technion.android.israelihope.Adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technion.android.israelihope.Objects.User;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserTypeFilterAdapter extends RecyclerView.Adapter<UserTypeFilterAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<User.UserType> chosenTypes;      // Filters already applied by the user
    private ArrayList<User.UserType> tempChosenTypes;  // Filters that are currently chosen but not necessarily applied yet

    public UserTypeFilterAdapter(Context context) {
        this.mContext = context;
        chosenTypes = new ArrayList<>();
        tempChosenTypes = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserTypeFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_type_filter, parent, false);
        return new UserTypeFilterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserTypeFilterAdapter.ViewHolder holder, final int position) {
        final User.UserType userType = User.UserType.values()[position];
        holder.user_type.setText(userType.name());

        updateItemChosen(holder, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.animateClick(view);
                if (tempChosenTypes.contains(userType))
                    tempChosenTypes.remove(userType);
                else
                    tempChosenTypes.add(userType);

                updateItemChosen(holder, position);
            }
        });

        ObjectAnimator.ofPropertyValuesHolder(holder.itemView,
                PropertyValuesHolder.ofFloat(View.SCALE_Y, .5f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_X, .5f, 1))
                .setDuration(300)
                .start();
    }

    @Override
    public int getItemCount() {
        return User.UserType.values().length;
    }

    private void updateItemChosen(@NonNull final UserTypeFilterAdapter.ViewHolder holder, int position) {

        final User.UserType userType = User.UserType.values()[position];

        if (tempChosenTypes.contains(userType)) {
            holder.user_type.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimaryDark)));
            holder.user_type.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.user_type.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.transparentGrey)));
            holder.user_type.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
        }
    }

    public void applyChosenFilters() {
        chosenTypes.clear();
        chosenTypes.addAll(tempChosenTypes);
        tempChosenTypes.clear();
    }

    public ArrayList<User.UserType> getChosenTypes() {
        if (chosenTypes.isEmpty())
            return new ArrayList<>(Arrays.asList(User.UserType.values()));

        return chosenTypes;
    }

    public void clearChosen() {
        chosenTypes.clear();
        notifyDataSetChanged();
    }

    public void clearTempChosen() {
        tempChosenTypes.clear();
        tempChosenTypes.addAll(chosenTypes);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView user_type;

        public ViewHolder(View itemView) {
            super(itemView);
            user_type = itemView.findViewById(R.id.user_type);
        }
    }

}

package com.technion.android.israelihope.Adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionSubjectFilterAdapter extends RecyclerView.Adapter<QuestionSubjectFilterAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Question.QuestionSubject> chosenSubjects;         // Filters already applied by the user
    private ArrayList<Question.QuestionSubject> tempChosenSubjects;     // Filters that are currently chosen but not necessarily applied yet

    public QuestionSubjectFilterAdapter(Context context) {
        this.mContext = context;
        chosenSubjects = new ArrayList<>();
        tempChosenSubjects = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_question_subject_filter, parent, false);
        return new QuestionSubjectFilterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Question.QuestionSubject subject = Question.QuestionSubject.values()[position];
        holder.subject.setText(subject.name());

        updateItemChosen(holder, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.animateClick(view);
                if (tempChosenSubjects.contains(subject))
                    tempChosenSubjects.remove(subject);
                else
                    tempChosenSubjects.add(subject);

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
        return Question.QuestionSubject.values().length;
    }

    private void updateItemChosen(@NonNull final ViewHolder holder, int position) {

        final Question.QuestionSubject subject = Question.QuestionSubject.values()[position];

        if (tempChosenSubjects.contains(subject)) {
            holder.subject.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimaryDark)));
            holder.subject.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.subject.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.transparentGrey)));
            holder.subject.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
        }
    }

    public void applyChosenFilters() {
        chosenSubjects.clear();
        chosenSubjects.addAll(tempChosenSubjects);
        tempChosenSubjects.clear();
    }

    public ArrayList<Question.QuestionSubject> getChosenSubjects() {
        if (chosenSubjects.isEmpty())
            return new ArrayList<>(Arrays.asList(Question.QuestionSubject.values()));

        return chosenSubjects;
    }

    public void clearChosen() {
        chosenSubjects.clear();
        notifyDataSetChanged();
    }

    public void clearTempChosen() {
        tempChosenSubjects.clear();
        tempChosenSubjects.addAll(chosenSubjects);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView subject;

        public ViewHolder(View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
        }
    }

}

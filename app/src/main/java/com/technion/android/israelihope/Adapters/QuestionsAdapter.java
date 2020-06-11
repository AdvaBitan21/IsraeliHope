package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.Objects.User;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.SendChallengeActivity;
import com.technion.android.israelihope.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Question> mQuestions;
    private ArrayList<Question> mFilteredQuestion;

    private int currentExpanded = -1;

    public QuestionsAdapter(Context context, ArrayList<Question> questions) {
        this.mContext = context;
        this.mQuestions = questions;
        this.mFilteredQuestion = new ArrayList<>(questions);
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_question, parent, false);
        return new QuestionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuestionsAdapter.ViewHolder holder, final int position) {
        final Question question = mFilteredQuestion.get(position);
        holder.question.setText(question.getContent());

        if (currentExpanded == position)
            holder.expandLayout.setVisibility(View.VISIBLE);
        else
            holder.expandLayout.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentExpanded == position) {
                    currentExpanded = -1;
                    notifyItemChanged(position);
                } else {
                    holder.expandLayout.setVisibility(View.VISIBLE);
                    notifyItemChanged(currentExpanded);
                    currentExpanded = position;
                }
            }
        });

        holder.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SendChallengeActivity) mContext).sendChallenge(question);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredQuestion.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout expandLayout;
        public TextView question;
        public Button sendButton;

        public ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            sendButton = itemView.findViewById(R.id.send_question);
            expandLayout = itemView.findViewById(R.id.send_layout);
        }

    }


    public void applyUserTypesFilter(ArrayList<User.UserType> userTypes) {

        mFilteredQuestion.clear();
        for (Question question : mQuestions) {
            if (userTypes.contains(question.getSubject()))
                mFilteredQuestion.add(question);
        }

        notifyDataSetChanged();
    }

}

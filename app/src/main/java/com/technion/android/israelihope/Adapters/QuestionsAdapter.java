package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.SendChallengeActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_question, parent, false);
        return new QuestionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Question question = mFilteredQuestion.get(position);
        holder.question.setText(question.getContent());

        ViewCompat.setTransitionName(holder.statisticsButton, "expand" + position);

        if (currentExpanded == position) {
            holder.expandLayout.setVisibility(View.VISIBLE);
        } else {
            holder.expandLayout.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentExpanded == position) {
                    currentExpanded = -1;
                    notifyItemChanged(position);

                } else {
                    notifyItemChanged(currentExpanded);
                    currentExpanded = position;
                    notifyItemChanged(currentExpanded);
                }
            }
        });

        holder.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SendChallengeActivity) mContext).sendChallenge(question);
            }
        });

        holder.statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SendChallengeActivity) mContext).showQuestionStatistics(question, holder.statisticsButton);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFilteredQuestion.size();
    }

    public void applyUserTypesFilter(ArrayList<Question.QuestionSubject> questionSubjects) {

        mFilteredQuestion.clear();
        for (Question question : mQuestions) {
            if (questionSubjects.contains(question.getSubject()))
                mFilteredQuestion.add(question);
        }

        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout expandLayout;
        private TextView question;
        private Button sendButton;
        private ImageButton statisticsButton;

        public ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            sendButton = itemView.findViewById(R.id.send_question);
            expandLayout = itemView.findViewById(R.id.send_layout);
            statisticsButton = itemView.findViewById(R.id.statistics_button);
        }

    }

}

package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.R;

import java.util.List;

public class AllQuestionsListAdapter extends RecyclerView.Adapter<AllQuestionsListAdapter.MyViewHolder>{

    private Context mContext ;
    private List<Question> mData ;
    private AllQuestionsListAdapter.OnItemClickListener listener;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

            TextView question_content;
            TextView is_first_quiz;

            public MyViewHolder(View itemView) {
                super(itemView);
                question_content = itemView.findViewById(R.id.question_content);
                is_first_quiz = itemView.findViewById(R.id.is_first_quiz);

            }

            public void bind(final Question item, final OnItemClickListener listener) {

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
            }
        }


        public interface OnItemClickListener {
            void onItemClick(Question item);
        }


        public AllQuestionsListAdapter(Context mContext, List<Question> mData, OnItemClickListener listener) {
            this.mContext = mContext;
            this.mData = mData;
            this.listener = listener;
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {

            View view ;
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            view = mInflater.inflate(R.layout.item_question_admin_list,parent,false);
            return new AllQuestionsListAdapter.MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {



            holder.question_content.setText((mData.get(position).getContent()));
            int index= mData.get(position).getFirst_quiz_index();
            if(index>0)
                holder.is_first_quiz.setText("שאלה "+index+" בשאלון הראשוני");
            else
                holder.is_first_quiz.setText("");


            holder.bind(mData.get(position), listener);
        }


        @Override
        public int getItemCount() {
            return mData.size();
        }





    }



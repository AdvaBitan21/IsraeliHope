package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.technion.android.israelihope.Dialogs.ChallengeDialog;
import com.technion.android.israelihope.Objects.Challenge;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Item types
    private static final int MSG_TYPE_RIGHT = 0;
    private static final int MSG_TYPE_LEFT = 1;
    private static final int CHALLENGE_TYPE_RIGHT = 2;
    private static final int CHALLENGE_TYPE_LEFT = 3;

    private Context mContext;
    private List<Chat> mChat;
    private List<DocumentReference> mDocuments;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChat, List<DocumentReference> mDocuments) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.mDocuments = mDocuments;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case MSG_TYPE_RIGHT:
                view = LayoutInflater.from(mContext).inflate(R.layout.my_message, parent, false);
                return new MessageAdapter.MessageViewHolder(view);
            case MSG_TYPE_LEFT:
                view = LayoutInflater.from(mContext).inflate(R.layout.their_message, parent, false);
                return new MessageAdapter.MessageViewHolder(view);
            case CHALLENGE_TYPE_RIGHT:
                view = LayoutInflater.from(mContext).inflate(R.layout.my_challenge, parent, false);
                return new MessageAdapter.MyChallengeViewHolder(view);
            case CHALLENGE_TYPE_LEFT:
                view = LayoutInflater.from(mContext).inflate(R.layout.their_challenge, parent, false);
                return new MessageAdapter.TheirChallengeViewHolder(view);
        }

        return null;
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Chat chat = mChat.get(position);
        if (chat.getSender().equals(firebaseUser.getEmail())) {
            if (chat.getType().equals(Chat.ChatType.CHALLENGE))
                return CHALLENGE_TYPE_RIGHT;
            return MSG_TYPE_RIGHT;
        } else {
            if (chat.getType().equals(Chat.ChatType.CHALLENGE))
                return CHALLENGE_TYPE_LEFT;
            return MSG_TYPE_LEFT;
        }
    }


// =========================================== Binders ========================================== //


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder)
            onBindMessageViewHolder((MessageViewHolder) holder, position);
        if (holder instanceof MyChallengeViewHolder)
            onBindMyChallengeViewHolder((MyChallengeViewHolder) holder, position);
        if (holder instanceof TheirChallengeViewHolder)
            onBindTheirChallengeViewHolder((TheirChallengeViewHolder) holder, position);
    }


    public void onBindMessageViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        Utils.loadProfileImage(mContext, holder.profile_image, chat.getSender());

        SimpleDateFormat simple_format = new SimpleDateFormat("HH:mm");
        Timestamp time = chat.getMessageTime();
        if (time == null)
            return;
        holder.message_time.setText(simple_format.format(time.toDate()));

        holder.show_message.setText(chat.getMessage());

    }


    public void onBindMyChallengeViewHolder(@NonNull MessageAdapter.MyChallengeViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        Utils.loadProfileImage(mContext, holder.profile_image, chat.getSender());

        SimpleDateFormat simple_format = new SimpleDateFormat("HH:mm");
        Timestamp time = chat.getMessageTime();
        if (time == null)
            return;
        holder.message_time.setText(simple_format.format(time.toDate()));

        bindMyChallengeQuestion(holder, position);
    }

    private void bindMyChallengeQuestion(@NonNull final MessageAdapter.MyChallengeViewHolder holder, int position) {
        String questionId = mChat.get(position).getChallenge().getQuestionId();
        FirebaseFirestore.getInstance().collection("Questions").document(questionId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Question question = task.getResult().toObject(Question.class);
                if (question.getQuestion_type().equals(Utils.QuestionType.YES_NO)) {
                    holder.yesno_question_layout.setVisibility(View.VISIBLE);
                    holder.yesno_question.setText(question.getContent());
                }
            }
        });

        addMyChallengeListeners(holder, position);
    }

    private void addMyChallengeListeners(@NonNull final MessageAdapter.MyChallengeViewHolder holder, int position) {
        mDocuments.get(position).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                Challenge challenge = documentSnapshot.toObject(Chat.class).getChallenge();

                if (challenge.getState().equals(Challenge.ChallengeState.IN_PROGRESS)) {
                    holder.dots.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.GONE);
                } else {
                    holder.dots.setVisibility(View.GONE);
                    holder.status.setVisibility(View.VISIBLE);
                }

                if (challenge.getState().equals(Challenge.ChallengeState.CORRECT)) {
                    holder.status.setText(mContext.getResources().getString(R.string.challenge_answered_correct));
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.green));
                    holder.challenge_layout.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.messageGreen)));
                }

                if (challenge.getState().equals(Challenge.ChallengeState.WRONG)) {
                    holder.status.setText(mContext.getResources().getString(R.string.challenge_answered_wrong));
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                    holder.challenge_layout.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.lightRed)));
                }

                if (challenge.getState().equals(Challenge.ChallengeState.OUT_OF_TIME)) {
                    holder.status.setText(mContext.getResources().getString(R.string.times_up));
                    holder.status.setTextColor(mContext.getResources().getColor(R.color.darkGrey));
                    holder.challenge_layout.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                }

            }
        });
    }


    private void onBindTheirChallengeViewHolder(@NonNull MessageAdapter.TheirChallengeViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);

        Utils.loadProfileImage(mContext, holder.profile_image, chat.getSender());

        SimpleDateFormat simple_format = new SimpleDateFormat("HH:mm");
        Timestamp time = chat.getMessageTime();
        if (time == null)
            return;
        holder.message_time.setText(simple_format.format(time.toDate()));

        holder.start_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChallengeDialog.class);
                intent.putExtra("challenge", chat.getChallenge());
                intent.putExtra("documentId", mDocuments.get(position).getId());
                mContext.startActivity(intent);
            }
        });

        addTheirChallengeListeners(holder, position);
    }

    private void bindTheirChallengeQuestion(@NonNull final MessageAdapter.TheirChallengeViewHolder holder, int position) {
        String questionId = mChat.get(position).getChallenge().getQuestionId();
        FirebaseFirestore.getInstance().collection("Questions").document(questionId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Question question = task.getResult().toObject(Question.class);
                if (question.getQuestion_type().equals(Utils.QuestionType.YES_NO)) {
                    holder.yesno_question_layout.setVisibility(View.VISIBLE);
                    holder.yesno_question.setText(question.getContent());
                }
            }
        });
    }

    private void addTheirChallengeListeners(@NonNull final MessageAdapter.TheirChallengeViewHolder holder, final int position) {
        mDocuments.get(position).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                Challenge challenge = documentSnapshot.toObject(Chat.class).getChallenge();

                if (challenge.getState().equals(Challenge.ChallengeState.CORRECT)) {
                    bindTheirChallengeQuestion(holder, position);
                    holder.empty_question_layout.setVisibility(View.GONE);
                    holder.challenge_layout.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.messageGreen)));
                }

                if (challenge.getState().equals(Challenge.ChallengeState.WRONG)) {
                    bindTheirChallengeQuestion(holder, position);
                    holder.empty_question_layout.setVisibility(View.GONE);
                    holder.challenge_layout.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.lightRed)));
                }

                if (challenge.getState().equals(Challenge.ChallengeState.OUT_OF_TIME)) {
                    bindTheirChallengeQuestion(holder, position);
                    holder.empty_question_layout.setVisibility(View.GONE);
                    holder.challenge_layout.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorGrey)));
                }
            }
        });
    }


// ======================================== View Holders ======================================== //

    private static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView profile_image;
        public TextView message_time;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.message_body);
            profile_image = itemView.findViewById(R.id.avatar);
            message_time = itemView.findViewById(R.id.message_time);
        }
    }


    private static class MyChallengeViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout challenge_layout;
        public ImageView profile_image;
        public TextView message_time;

        public LazyLoader dots;
        public TextView status;

        public RelativeLayout yesno_question_layout;
        public TextView yesno_question;

        public RelativeLayout multichoice_question_layout;
        public TextView multichoice_question;


        public MyChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            challenge_layout = itemView.findViewById(R.id.challenge_layout);
            profile_image = itemView.findViewById(R.id.avatar);
            message_time = itemView.findViewById(R.id.message_time);
            yesno_question_layout = itemView.findViewById(R.id.yesno_question_container);
            yesno_question = itemView.findViewById(R.id.yesno_question);
            dots = itemView.findViewById(R.id.dots);
            status = itemView.findViewById(R.id.status);
        }
    }


    private static class TheirChallengeViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout challenge_layout;
        public ImageView profile_image;
        public TextView message_time;

        public Button start_challenge;

        public RelativeLayout empty_question_layout;

        public RelativeLayout yesno_question_layout;
        public TextView yesno_question;

        public RelativeLayout multichoice_question_layout;
        public TextView multichoice_question;


        public TheirChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            challenge_layout = itemView.findViewById(R.id.challenge_layout);
            profile_image = itemView.findViewById(R.id.avatar);
            message_time = itemView.findViewById(R.id.message_time);
            yesno_question_layout = itemView.findViewById(R.id.yesno_question_container);
            yesno_question = itemView.findViewById(R.id.yesno_question);
            start_challenge = itemView.findViewById(R.id.start_challenge);
            empty_question_layout = itemView.findViewById(R.id.empty_question_container);
        }
    }

}
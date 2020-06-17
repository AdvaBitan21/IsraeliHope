package com.technion.android.israelihope.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technion.android.israelihope.Dialogs.ChallengeDialog;
import com.technion.android.israelihope.Objects.Challenge;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private static final int PICTURE_TYPE_RIGHT = 4;
    private static final int PICTURE_TYPE_LEFT = 5;

    private Context mContext;
    private List<Chat> mChat;
    private List<DocumentReference> mDocuments;


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
                return new MessageViewHolder(view);
            case MSG_TYPE_LEFT:
                view = LayoutInflater.from(mContext).inflate(R.layout.their_message, parent, false);
                return new MessageViewHolder(view);
            case CHALLENGE_TYPE_RIGHT:
                view = LayoutInflater.from(mContext).inflate(R.layout.my_challenge, parent, false);
                return new MyChallengeViewHolder(view);
            case CHALLENGE_TYPE_LEFT:
                view = LayoutInflater.from(mContext).inflate(R.layout.their_challenge, parent, false);
                return new TheirChallengeViewHolder(view);
            case PICTURE_TYPE_RIGHT:
                view = LayoutInflater.from(mContext).inflate(R.layout.my_message, parent, false);
                return new PictureViewHolder(view);
            case PICTURE_TYPE_LEFT:
                view = LayoutInflater.from(mContext).inflate(R.layout.their_message, parent, false);
                return new PictureViewHolder(view);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = mChat.get(position);
        switch (chat.getType()) {
            case TEXT:
                return currentUserIsSender(chat) ? MSG_TYPE_RIGHT : MSG_TYPE_LEFT;
            case PICTURE:
                return currentUserIsSender(chat) ? PICTURE_TYPE_RIGHT : PICTURE_TYPE_LEFT;
            case CHALLENGE:
                return currentUserIsSender(chat) ? CHALLENGE_TYPE_RIGHT : CHALLENGE_TYPE_LEFT;
            default:
                return -1;
        }
    }


    private boolean currentUserIsSender(Chat chat) {
        return chat.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }


// =========================================== Binders ========================================== //

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        ChatViewHolder chatViewHolder = (ChatViewHolder) holder;

        if(!currentUserIsSender(chat)) {
            mDocuments.get(position).update("seen", true);
        }
        Utils.loadProfileImage(mContext, chatViewHolder.profile_image, chat.getSender());

        SimpleDateFormat hour_format = new SimpleDateFormat("HH:mm");
        Timestamp time = chat.getMessageTime();
        if (time == null)
            return;
        chatViewHolder.message_time.setText(hour_format.format(time.toDate()));

        bindDateLayout(chatViewHolder, position);

        if (holder instanceof MessageViewHolder)
            onBindMessageViewHolder((MessageViewHolder) holder, position);
        if (holder instanceof MyChallengeViewHolder)
            onBindMyChallengeViewHolder((MyChallengeViewHolder) holder, position);
        if (holder instanceof TheirChallengeViewHolder)
            onBindTheirChallengeViewHolder((TheirChallengeViewHolder) holder, position);
        if (holder instanceof PictureViewHolder)
            onBindPictureViewHolder((PictureViewHolder) holder, position);
    }

    private void bindDateLayout(ChatViewHolder holder, int position) {

        holder.date_title_layout.setVisibility(View.GONE);

        Chat currentChat = mChat.get(position);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(currentChat.getMessageTime().toDate());

        if (position > 0) {
            Chat previousChat = mChat.get(position - 1);
            Calendar previousCalendar = Calendar.getInstance();
            previousCalendar.setTime(previousChat.getMessageTime().toDate());

            if (currentCalendar.get(Calendar.DAY_OF_YEAR) > previousCalendar.get(Calendar.DAY_OF_YEAR)) {
                holder.date.setText(Utils.getDateStringForChat(mContext, currentCalendar));
                holder.date_title_layout.setVisibility(View.VISIBLE);
            }
        } else {
            holder.date.setText(Utils.getDateStringForChat(mContext, currentCalendar));
            holder.date_title_layout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        Chat chat = mChat.get(position);
        if (!payloads.isEmpty()) {
            if (payloads.get(0).equals(Utils.PICTURE_CHANGE_PAYLOAD)) {
                loadPictureToImageView((PictureViewHolder) holder, Uri.parse(chat.getPictureUri()));
            }
        }
    }


    private void onBindMessageViewHolder(@NonNull final MessageViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.message.setText(chat.getMessage());
    }


    private void onBindPictureViewHolder(@NonNull final PictureViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);

        holder.picture.setVisibility(View.INVISIBLE);
        holder.progressbar.setVisibility(View.VISIBLE);

        final StorageReference reference = FirebaseStorage.getInstance()
                .getReference("chatPictures")
                .child(mDocuments.get(position).getId() + ".jpeg");

        //If picture already on firebase, load it from the server. Otherwise, upload it.
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                mDocuments.get(position).update("pictureUri", uri.toString());
                loadPictureToImageView(holder, uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {

                    //Receiver - Listen to uploading progress on sender side
                    if (!currentUserIsSender(chat)) {
                        mDocuments.get(position).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                notifyItemChanged(position, Utils.PICTURE_CHANGE_PAYLOAD);
                            }
                        });
                        return;
                    }

                    //Sender - upload picture to firebase
                    try {
                        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(chat.getPictureUri()));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                loadPictureToImageView(holder, Uri.parse(chat.getPictureUri()));
                            }
                        });
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadPictureToImageView(final PictureViewHolder holder, Uri uri) {

        Glide.with(mContext)
                .load(uri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.picture.setVisibility(View.VISIBLE);
                        holder.progressbar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.picture);
    }


    private void onBindMyChallengeViewHolder(@NonNull final MyChallengeViewHolder holder, int position) {

        final String questionId = mChat.get(position).getChallenge().getQuestionId();
        FirebaseFirestore.getInstance().collection("Questions").document(questionId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Question question = task.getResult().toObject(Question.class);
                if (question.getQuestion_type().equals(Question.QuestionType.YES_NO)) {
                    holder.yesno_question_layout.setVisibility(View.VISIBLE);
                    holder.yesno_question.setText(question.getContent());
                }
                if (question.getQuestion_type().equals(Question.QuestionType.CLOSE)) {
                    holder.multichoice_question_layout.setVisibility(View.VISIBLE);
                    holder.multichoice_question.setText(question.getContent());
                    holder.multichoice_choice1.setText(question.getPossible_answers().get(0));
                    holder.multichoice_choice2.setText(question.getPossible_answers().get(1));
                    holder.multichoice_choice3.setText(question.getPossible_answers().get(2));
                    holder.multichoice_choice4.setText(question.getPossible_answers().get(3));
                }
            }
        });

        addMyChallengeListeners(holder, position);
    }

    private void addMyChallengeListeners(@NonNull final MyChallengeViewHolder holder, int position) {
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


    private void onBindTheirChallengeViewHolder(@NonNull TheirChallengeViewHolder holder, final int position) {

        final Chat chat = mChat.get(position);

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

    private void bindTheirChallengeQuestion(@NonNull final TheirChallengeViewHolder holder, int position) {
        String questionId = mChat.get(position).getChallenge().getQuestionId();
        FirebaseFirestore.getInstance().collection("Questions").document(questionId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Question question = task.getResult().toObject(Question.class);
                if (question.getQuestion_type().equals(Question.QuestionType.YES_NO)) {
                    holder.yesno_question_layout.setVisibility(View.VISIBLE);
                    holder.yesno_question.setText(question.getContent());
                }
                if (question.getQuestion_type().equals(Question.QuestionType.CLOSE)) {
                    holder.multichoice_question_layout.setVisibility(View.VISIBLE);
                    holder.multichoice_question.setText(question.getContent());
                    holder.multichoice_choice1.setText(question.getPossible_answers().get(0));
                    holder.multichoice_choice2.setText(question.getPossible_answers().get(1));
                    holder.multichoice_choice3.setText(question.getPossible_answers().get(2));
                    holder.multichoice_choice4.setText(question.getPossible_answers().get(3));
                }
            }
        });
    }

    private void addTheirChallengeListeners(@NonNull final TheirChallengeViewHolder holder, final int position) {
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

    private static class ChatViewHolder extends RecyclerView.ViewHolder {

        public ImageView profile_image;
        public TextView message_time;
        public RelativeLayout date_title_layout;
        public TextView date;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.avatar);
            message_time = itemView.findViewById(R.id.message_time);
            date_title_layout = itemView.findViewById(R.id.date_title_layout);
            date = itemView.findViewById(R.id.date);
        }
    }

    private static class MessageViewHolder extends ChatViewHolder {
        public TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_body);
        }
    }

    private static class PictureViewHolder extends ChatViewHolder {
        public ImageView picture;
        public ProgressBar progressbar;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.picture_body);
            progressbar = itemView.findViewById(R.id.progressbar);
        }
    }

    private static class MyChallengeViewHolder extends ChatViewHolder {

        public LinearLayout challenge_layout;

        public LazyLoader dots;
        public TextView status;

        public RelativeLayout yesno_question_layout;
        public TextView yesno_question;

        public RelativeLayout multichoice_question_layout;
        public TextView multichoice_question;
        public Button multichoice_choice1;
        public Button multichoice_choice2;
        public Button multichoice_choice3;
        public Button multichoice_choice4;

        public MyChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            challenge_layout = itemView.findViewById(R.id.challenge_layout);
            yesno_question_layout = itemView.findViewById(R.id.yesno_question_container);
            yesno_question = itemView.findViewById(R.id.yesno_question);
            multichoice_question_layout = itemView.findViewById(R.id.multichoice_question_container);
            multichoice_question = itemView.findViewById(R.id.multichoice_question);
            multichoice_choice1 = itemView.findViewById(R.id.multichoice_choice1);
            multichoice_choice2 = itemView.findViewById(R.id.multichoice_choice2);
            multichoice_choice3 = itemView.findViewById(R.id.multichoice_choice3);
            multichoice_choice4 = itemView.findViewById(R.id.multichoice_choice4);
            dots = itemView.findViewById(R.id.dots);
            status = itemView.findViewById(R.id.status);
        }
    }

    private static class TheirChallengeViewHolder extends ChatViewHolder {

        public LinearLayout challenge_layout;

        public Button start_challenge;

        public RelativeLayout empty_question_layout;

        public RelativeLayout yesno_question_layout;
        public TextView yesno_question;

        public RelativeLayout multichoice_question_layout;
        public TextView multichoice_question;
        public Button multichoice_choice1;
        public Button multichoice_choice2;
        public Button multichoice_choice3;
        public Button multichoice_choice4;


        public TheirChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            challenge_layout = itemView.findViewById(R.id.challenge_layout);
            yesno_question_layout = itemView.findViewById(R.id.yesno_question_container);
            yesno_question = itemView.findViewById(R.id.yesno_question);
            multichoice_question_layout = itemView.findViewById(R.id.multichoice_question_container);
            multichoice_question = itemView.findViewById(R.id.multichoice_question);
            multichoice_choice1 = itemView.findViewById(R.id.multichoice_choice1);
            multichoice_choice2 = itemView.findViewById(R.id.multichoice_choice2);
            multichoice_choice3 = itemView.findViewById(R.id.multichoice_choice3);
            multichoice_choice4 = itemView.findViewById(R.id.multichoice_choice4);
            start_challenge = itemView.findViewById(R.id.start_challenge);
            empty_question_layout = itemView.findViewById(R.id.empty_question_container);
        }
    }

}
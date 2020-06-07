package com.technion.android.israelihope;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technion.android.israelihope.Objects.Chat;
import com.technion.android.israelihope.Objects.Question;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;

public class Utils {

    public enum QuestionType {
        YES_NO,
        CLOSE,
        CHECKBOX
    }

    public enum UserType {
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I,
        J
    }

    public static int AMOUNT_OF_QUESTIONS_FIRST_QUIZ = 36;
    public static int OPEN_GALLERY_REQUEST = 1;
    public static int SEND_CHALLENGE_REQUEST = 2;


    public static void closeKeyboard(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        View view = ((Activity) context).getCurrentFocus();
        if (view == null)
            view = new View((Activity) context);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static void openKeyboard(Context context, EditText editText) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }


    /**
     * Enables/Disables all child views in a view group.
     *
     * @param viewGroup the view group
     * @param enabled   true to enable, false to disable
     */
    public static void enableDisableClicks(Activity activity, ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableClicks(activity, (ViewGroup) view, enabled);
            }
        }
    }


    public static void animateClick(View view) {
        ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.97f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.97f, 1))
                .setDuration(200)
                .start();
    }


    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


// ================================= Profile Picture Management ================================= //

    /**
     * Loads a users profile picture into the desired ImageView.
     *
     * @param imageView the ImageView to load the picture into.
     * @param email     the email of the user
     */
    public static void loadProfileImage(final Context context, final ImageView imageView, String email) {

        if (context instanceof Activity) {
            if (((Activity) context).isDestroyed() || ((Activity) context).isFinishing())
                return;
        }

        FirebaseStorage.getInstance().getReference().child("profileImages/" + email + ".jpeg")
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context).load(task.getResult()).into(imageView);
            }
        });
    }


    /**
     * Opens image gallery in order to choose a picture.
     */
    public static void openImageChooser(Context context) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (context instanceof Activity)
            ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), OPEN_GALLERY_REQUEST);

    }


    /**
     * Uploads a users profile image to firebase storage.
     *
     * @param bitmap the bitmap of the image to upload.
     * @param email  the email of the user.
     */
    public static void uploadProfileImage(Bitmap bitmap, String email) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        final StorageReference reference = FirebaseStorage.getInstance()
                .getReference("profileImages")
                .child(email + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                });
    }


    private static void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfileUri(uri);
            }
        });
    }


    private static void setUserProfileUri(Uri uri) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        assert user != null;
        user.updateProfile(request);
    }


// ===================================== Firebase Functions ===================================== //


    public static void uploadQuestionsToFirebase(Context context) {

        ArrayList<Question> questions = getQuestions();
        for (Question question : questions) {
            DocumentReference newQuestionDoc = FirebaseFirestore.getInstance().collection("Questions").document();
            question.setId(newQuestionDoc.getId());
            newQuestionDoc.set(question);
        }

    }


    public static ArrayList<Question> getQuestions() {

        ArrayList<Question> questions = new ArrayList<>();

        ArrayList<String> possibleAnswers1 = new ArrayList<String>();
        possibleAnswers1.add("א");
        possibleAnswers1.add("ב");
        possibleAnswers1.add("ג");
        possibleAnswers1.add("ד");

        ArrayList<String> rightAnswers1 = new ArrayList<String>();
        rightAnswers1.add("ג");

        questions.add(new Question("", "שאלת רב ברירה 1", QuestionType.CLOSE, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.A));
        questions.add(new Question("", "שאלת רב ברירה 2", QuestionType.CLOSE, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.B));
        questions.add(new Question("", "שאלת רב ברירה 3", QuestionType.CLOSE, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.C));
        questions.add(new Question("", "שאלת רב ברירה 4", QuestionType.CLOSE, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.D));
        questions.add(new Question("", "שאלת רב ברירה 5", QuestionType.CLOSE, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.A));
        questions.add(new Question("", "שאלת רב ברירה 6", QuestionType.CLOSE, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.B));
        questions.add(new Question("", "שאלת רב ברירה 7", QuestionType.CLOSE, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.C));

        possibleAnswers1.clear();
        possibleAnswers1.add("נכון");
        possibleAnswers1.add("לא נכון");

        rightAnswers1.clear();
        rightAnswers1.add("נכון");

        questions.add(new Question("", "שאלת נכון/לא נכון 1", QuestionType.YES_NO, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.A));
        questions.add(new Question("", "שאלת נכון/לא נכון 2", QuestionType.YES_NO, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.B));
        questions.add(new Question("", "שאלת נכון/לא נכון 3", QuestionType.YES_NO, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.C));
        questions.add(new Question("", "שאלת נכון/לא נכון 4", QuestionType.YES_NO, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.D));
        questions.add(new Question("", "שאלת נכון/לא נכון 5", QuestionType.YES_NO, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.A));
        questions.add(new Question("", "שאלת נכון/לא נכון 6", QuestionType.YES_NO, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.B));
        questions.add(new Question("", "שאלת נכון/לא נכון 7", QuestionType.YES_NO, new ArrayList<String>(possibleAnswers1), new ArrayList<String>(rightAnswers1), -1, UserType.C));

        return questions;
    }


    /**
     * Deletes specific fields from all documents in a specific collection.
     *
     * @param collectionName the collection to iterate through.
     * @param fieldNames     names of all fields to delete
     */
    public static void deleteFieldsFromFirebase(String collectionName, final ArrayList<String> fieldNames) {

        final CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(collectionName);
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        for (String field : fieldNames) {
                            document.getReference().update(field, FieldValue.delete());
                        }
                    }
                }
            }
        });
    }


    public static void addFieldsToFirebase() {

        final CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("collection_name");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        document.getReference().update("field_name", "value");
                    }
                }
            }
        });
    }


    public static void status(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        if (firebaseUser != null)
            FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getEmail()).update(hashMap);
    }


// ============================================================================================== //

}

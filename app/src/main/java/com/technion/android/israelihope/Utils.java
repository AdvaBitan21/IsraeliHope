package com.technion.android.israelihope;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.technion.android.israelihope.Objects.Question;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;

public class Utils {

    public enum QuestionType {
        YesNo,
        Close,
        CheckBox
    }

    public enum UserType {
        A,
        B,
        C,
        D
    }

    public static int AMOUNT_OF_QUESTIONS_FIRST_QUIZ = 5;
    public static int OPEN_GALLERY_REQUEST = 1;


    public static void uploadQuestionToFirebase() {
        String content = "שאלה 1 ";
        Utils.QuestionType questionType = QuestionType.YesNo;
        ArrayList<String> possible_answers = new ArrayList<>();
        possible_answers.add("נכון");
        possible_answers.add("לא נכון");

        ArrayList<String> right_answers = new ArrayList<>();
        right_answers.add("לא נכון");
        String from_email = "";
        String to_email = "";
        int firstQuizIndex = 1;
        Utils.UserType subject = Utils.UserType.A;

        Question q = new Question("", content, questionType, possible_answers, right_answers, from_email, to_email, firstQuizIndex, subject);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference newQuestion = db.collection("Questions").document();
        q.setId(newQuestion.getId());
        newQuestion.set(q).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Log.d("Utils", "Uploaded book: ".concat(t));
                    //Toast.makeText(LoginActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                } else {
                    //Toast.makeText(LoginActivity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


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




// ================================= Profile Picture Management ================================= //

    /**
     * Loads a users profile picture into the desired imageView.
     *
     * @param imageView the imageView to load the picture into.
     * @param email     the email of the user
     */
    public static void loadProfileImage(final Context context, final ImageView imageView, String email) {
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

// ============================================================================================== //

    public static void status(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        if(firebaseUser != null)
            FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getEmail()).update(hashMap);
    }

}

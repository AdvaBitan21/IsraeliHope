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
import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.Objects.User;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class Utils {

// ========================================== Constants ========================================= //

    public static int AMOUNT_OF_QUESTIONS_FIRST_QUIZ = 36;

    public static int ADD_CONTENT_REQUEST = 10;
    public static int OPEN_GALLERY_REQUEST = 12;
    public static int SEND_CHALLENGE_REQUEST = 13;

    public static int STATUS_CHANGE_PAYLOAD = 20;
    public static int PICTURE_CHANGE_PAYLOAD = 21;
    public static int LAST_MESSAGE_CHANGE_PAYLOAD = 22;
    public static boolean clicksEnabled = true;


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

    public static void uploadQuestionsToFirebase() {

        ArrayList<Question> questions = getQuestions();
        for (Question question : questions) {
            DocumentReference newQuestionDoc = FirebaseFirestore.getInstance().collection("Questions").document();
            question.setId(newQuestionDoc.getId());
            newQuestionDoc.set(question);
        }
    }

    public static ArrayList<Question> getQuestions() {

        ArrayList<Question> questions = new ArrayList<>();

        ArrayList<String> possible_answers = new ArrayList<>();
        possible_answers.add("א");
        possible_answers.add("ב");
        possible_answers.add("ג");
        possible_answers.add("ד");

        ArrayList<String> right_answers = new ArrayList<>();
        right_answers.add("ג");

//        questions.add(new Question("", "שאלת רב ברירה 1", Question.QuestionType.CLOSE, possible_answers, right_answers, -1, User.UserType.A));
//        questions.add(new Question("", "שאלת רב ברירה 2", Question.QuestionType.CLOSE, possible_answers, right_answers, -1, User.UserType.B));
//        questions.add(new Question("", "שאלת רב ברירה 3", Question.QuestionType.CLOSE, possible_answers, right_answers, -1, User.UserType.C));
//        questions.add(new Question("", "שאלת רב ברירה 4", Question.QuestionType.CLOSE, possible_answers, right_answers, -1, User.UserType.D));
//        questions.add(new Question("", "שאלת רב ברירה 5", Question.QuestionType.CLOSE, possible_answers, right_answers, -1, User.UserType.A));
//        questions.add(new Question("", "שאלת רב ברירה 6", Question.QuestionType.CLOSE, possible_answers, right_answers, -1, User.UserType.B));
//        questions.add(new Question("", "שאלת רב ברירה 7", Question.QuestionType.CLOSE, possible_answers, right_answers, -1, User.UserType.C));

        ArrayList<String> possible_answers1 = new ArrayList<>();
        possible_answers1.add("נכון");
        possible_answers1.add("לא נכון");


        ArrayList<String> right_answers1 = new ArrayList<>();
        right_answers1.add("נכון");

//        questions.add(new Question("", "שאלת נכון/לא נכון 1", Question.QuestionType.YES_NO, possible_answers1, right_answers1, -1, User.UserType.A));
//        questions.add(new Question("", "שאלת נכון/לא נכון 2", Question.QuestionType.YES_NO, possible_answers1, right_answers1, -1, User.UserType.B));
//        questions.add(new Question("", "שאלת נכון/לא נכון 3", Question.QuestionType.YES_NO, possible_answers1, right_answers1, -1, User.UserType.C));
//        questions.add(new Question("", "שאלת נכון/לא נכון 4", Question.QuestionType.YES_NO, possible_answers1, right_answers1, -1, User.UserType.D));
//        questions.add(new Question("", "שאלת נכון/לא נכון 5", Question.QuestionType.YES_NO, possible_answers1, right_answers1, -1, User.UserType.A));
//        questions.add(new Question("", "שאלת נכון/לא נכון 6", Question.QuestionType.YES_NO, possible_answers1, right_answers1, -1, User.UserType.B));
//        questions.add(new Question("", "שאלת נכון/לא נכון 7", Question.QuestionType.YES_NO, possible_answers1, right_answers1, -1, User.UserType.C));

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

        final CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("Questions");
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Map<String, Integer> countRights = new HashMap<>();
                    for (User.UserType type : User.UserType.values()) {
                        countRights.put(type.name(), 0);
                    }
                    document.getReference().update("countRights", countRights);
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

// ================================== Various Utility Functions ================================= //

    public static void closeKeyboard(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((Activity) context).getCurrentFocus();
        if (view == null)
            view = new View(context);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void openKeyboard(Context context, EditText editText) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    /**
     * Enables/Disables all child views in a view group.
     *
     * @param viewGroup the view group
     * @param enabled   true to enable, false to disable
     */
    public static void enableDisableClicks(Activity activity, ViewGroup viewGroup, boolean enabled) {
        clicksEnabled = enabled;

        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableClicks(activity, (ViewGroup) view, enabled);
            }
        }
    }

    public static void enableDisableBackPressed(boolean val) {
        clicksEnabled = val;
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

    public static String getDateStringForChat(Context context, Calendar calendar) {

        Calendar now = Calendar.getInstance();

        String day = "";
        String month = "";

        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {

            if (calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR))
                return context.getString(R.string.today);

            if (calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) - 1)
                return context.getString(R.string.yesterday);

            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    day = context.getString(R.string.sunday);
                    break;
                case Calendar.MONDAY:
                    day = context.getString(R.string.monday);
                    break;
                case Calendar.TUESDAY:
                    day = context.getString(R.string.tuesday);
                    break;
                case Calendar.WEDNESDAY:
                    day = context.getString(R.string.wednesday);
                    break;
                case Calendar.THURSDAY:
                    day = context.getString(R.string.thursday);
                    break;
                case Calendar.FRIDAY:
                    day = context.getString(R.string.friday);
                    break;
                case Calendar.SATURDAY:
                    day = context.getString(R.string.saturday);
                    break;
            }

            if (calendar.get(Calendar.DAY_OF_YEAR) > now.get(Calendar.DAY_OF_YEAR) - 7) {
                return day;
            }

            switch (calendar.get(Calendar.MONTH)) {
                case Calendar.JANUARY:
                    month = context.getString(R.string.in_january);
                    break;
                case Calendar.FEBRUARY:
                    month = context.getString(R.string.in_february);
                    break;
                case Calendar.MARCH:
                    month = context.getString(R.string.in_march);
                    break;
                case Calendar.APRIL:
                    month = context.getString(R.string.in_april);
                    break;
                case Calendar.MAY:
                    month = context.getString(R.string.in_may);
                    break;
                case Calendar.JUNE:
                    month = context.getString(R.string.in_june);
                    break;
                case Calendar.JULY:
                    month = context.getString(R.string.in_july);
                    break;
                case Calendar.AUGUST:
                    month = context.getString(R.string.in_august);
                    break;
                case Calendar.SEPTEMBER:
                    month = context.getString(R.string.in_september);
                    break;
                case Calendar.OCTOBER:
                    month = context.getString(R.string.in_october);
                    break;
                case Calendar.NOVEMBER:
                    month = context.getString(R.string.in_november);
                    break;
                case Calendar.DECEMBER:
                    month = context.getString(R.string.in_december);
                    break;
            }

            return day + ", " + calendar.get(Calendar.DAY_OF_MONTH) + " " + month;
        }

        return calendar.get(Calendar.DAY_OF_MONTH) + " " + month + ", " + calendar.get(Calendar.YEAR);
    }

    public static String getTimeStringForChat(Context context, Calendar calendar) {

        Calendar now = Calendar.getInstance();

        String day = "";

        if (calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {

            if (calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)) {
                SimpleDateFormat hour_format = new SimpleDateFormat("HH:mm");
                return hour_format.format(calendar.getTime());
            }

            if (calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) - 1)
                return context.getString(R.string.yesterday);

            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    day = context.getString(R.string.sunday);
                    break;
                case Calendar.MONDAY:
                    day = context.getString(R.string.monday);
                    break;
                case Calendar.TUESDAY:
                    day = context.getString(R.string.tuesday);
                    break;
                case Calendar.WEDNESDAY:
                    day = context.getString(R.string.wednesday);
                    break;
                case Calendar.THURSDAY:
                    day = context.getString(R.string.thursday);
                    break;
                case Calendar.FRIDAY:
                    day = context.getString(R.string.friday);
                    break;
                case Calendar.SATURDAY:
                    day = context.getString(R.string.saturday);
                    break;
            }

            if (calendar.get(Calendar.DAY_OF_YEAR) > now.get(Calendar.DAY_OF_YEAR) - 7) {
                return day;
            }
        }

        SimpleDateFormat date_format = new SimpleDateFormat("d.M.yyyy");
        return date_format.format(calendar.getTime());
    }

    public static void initHistogram() {
        HashMap<String, Integer> counts = new HashMap<>();

        counts.put("Jewish1", 0);
        counts.put("Jewish2", 0);
        counts.put("Jewish3", 0);
        counts.put("Christian1", 0);
        counts.put("Christian2", 0);
        counts.put("Christian3", 0);
        counts.put("Muslim1", 0);
        counts.put("Muslim2", 0);
        counts.put("Muslim3", 0);
        counts.put("Druze", 0);
        counts.put("Bedouin", 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference questionRef = db.collection("Statistics").document("CountUsers");
        questionRef.update("hist", counts);

        HashMap<String, Integer> hist = new HashMap<>();
        hist.put("0-9", 0);
        hist.put("10-19", 0);
        hist.put("20-29", 0);
        hist.put("30-39", 0);
        hist.put("40-49", 0);
        hist.put("50-59", 0);
        hist.put("60-69", 0);
        hist.put("70-79", 0);
        hist.put("80-89", 0);
        hist.put("90-100", 0);

        DocumentReference questionRef1 = db.collection("Statistics").document("FirstQuizHistogram_Students");
        questionRef1.update("hist", hist);
        DocumentReference questionRef2 = db.collection("Statistics").document("FirstQuizHistogram_AcademicStaff");
        questionRef2.update("hist", hist);
        DocumentReference questionRef3 = db.collection("Statistics").document("FirstQuizHistogram_AdministrativeStaff");
        questionRef3.update("hist", hist);
    }

    public static String gradeRange(int num) {
        if (num >= 0 && num <= 9)
            return "0-9";
        if (num >= 10 && num <= 19)
            return "10-19";
        if (num >= 20 && num <= 29)
            return "20-29";
        if (num >= 30 && num <= 39)
            return "30-39";
        if (num >= 40 && num <= 49)
            return "40-49";
        if (num >= 50 && num <= 59)
            return "50-59";
        if (num >= 60 && num <= 69)
            return "60-69";
        if (num >= 70 && num <= 79)
            return "70-79";
        if (num >= 80 && num <= 89)
            return "80-89";
        return "90-100";
    }
}

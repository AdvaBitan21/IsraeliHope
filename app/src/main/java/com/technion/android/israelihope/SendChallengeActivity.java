package com.technion.android.israelihope;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Adapters.QuestionSubjectFilterAdapter;
import com.technion.android.israelihope.Adapters.QuestionsAdapter;
import com.technion.android.israelihope.Dialogs.QuestionDetailsDialog;
import com.technion.android.israelihope.Objects.Question;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SendChallengeActivity extends AppCompatActivity {

    private Question.QuestionType questionType;
    private QuestionsAdapter questionsAdapter;
    private QuestionSubjectFilterAdapter questionSubjectFilterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_challenge);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getQuestionType();
        setupAnimations();
        initQuestionsRV();
        initCancelButton();
        initFilterButton();
        initUserTypeFilterRV();
        initApplyFilterButton();
        initClearFilterButton();
    }


    private void getQuestionType() {
        Intent intent = getIntent();
        questionType = (Question.QuestionType) intent.getSerializableExtra("question_type");
        if (questionType == null)
            throw new AssertionError("Empty question type");
    }


    private void initQuestionsRV() {
        final RecyclerView questionsRV = findViewById(R.id.questions_RV);
        questionsRV.setLayoutManager(new LinearLayoutManager(this));

        final ArrayList<Question> questions = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection("Questions")
                .whereEqualTo("question_type", questionType.name())
                .whereEqualTo("first_quiz_index", -1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult()) {
                    questions.add(document.toObject(Question.class));
                }
                questionsAdapter = new QuestionsAdapter(SendChallengeActivity.this, questions);
                questionsRV.setAdapter(questionsAdapter);
            }
        });
    }


    private void initCancelButton() {
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateOut();
            }
        });
    }


    private void initUserTypeFilterRV() {

        RecyclerView userTypeRV = findViewById(R.id.user_types_RV);
        userTypeRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        questionSubjectFilterAdapter = new QuestionSubjectFilterAdapter(this);
        userTypeRV.setAdapter(questionSubjectFilterAdapter);
    }

    private void updateFilterLayoutUI(boolean toExpand) {
        final ImageButton filter = findViewById(R.id.filter);
        if (toExpand) {
            filter.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            filter.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            filter.setImageDrawable(getResources().getDrawable(R.drawable.close));
            showFilterOptionsLayout();
        } else {
            filter.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            filter.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            filter.setImageDrawable(getResources().getDrawable(R.drawable.filter));
            hideFilterOptionsLayout();
        }
    }

    private void showFilterOptionsLayout() {

        findViewById(R.id.user_types_filter_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.apply_filter).setVisibility(View.VISIBLE);
        findViewById(R.id.clear_filter).setVisibility(View.VISIBLE);

        Animator animator = ObjectAnimator
                .ofPropertyValuesHolder(findViewById(R.id.user_types_filter_layout),
                        PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 300, 0))
                .setDuration(500);

        animator.setInterpolator(new OvershootInterpolator());
        animator.start();

        ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.apply_filter),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 100, 0))
                .setDuration(200)
                .start();

        ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.clear_filter),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 100, 0))
                .setDuration(200)
                .start();

        questionSubjectFilterAdapter.notifyDataSetChanged(); // for items animation
    }

    private void hideFilterOptionsLayout() {

        Animator animator = ObjectAnimator
                .ofPropertyValuesHolder(findViewById(R.id.user_types_filter_layout),
                        PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0, 300))
                .setDuration(300);

        animator.setInterpolator(new AnticipateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                findViewById(R.id.user_types_filter_layout).setVisibility(View.GONE);
                findViewById(R.id.apply_filter).setVisibility(View.GONE);
                findViewById(R.id.clear_filter).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();

        ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.apply_filter),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0, 100))
                .setDuration(200)
                .start();

        ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.clear_filter),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0, 100))
                .setDuration(200)
                .start();

        questionSubjectFilterAdapter.clearTempChosen();
    }

    private void initFilterButton() {
        final ImageButton filter = findViewById(R.id.filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.animateClick(view);
                boolean toExpand = (findViewById(R.id.user_types_filter_layout).getVisibility() == View.GONE);
                updateFilterLayoutUI(toExpand);
            }
        });
    }

    private void initApplyFilterButton() {
        findViewById(R.id.apply_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.animateClick(view);
                questionSubjectFilterAdapter.applyChosenFilters();
                questionsAdapter.applyUserTypesFilter(questionSubjectFilterAdapter.getChosenSubjects());
                updateFilterLayoutUI(false);
            }
        });
    }

    private void initClearFilterButton() {
        findViewById(R.id.clear_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.animateClick(view);
                updateFilterLayoutUI(false);
                questionSubjectFilterAdapter.clearChosen();
                questionSubjectFilterAdapter.clearTempChosen();
                questionsAdapter.applyUserTypesFilter(questionSubjectFilterAdapter.getChosenSubjects());
            }
        });
    }


    private void setupAnimations() {
        Animation slide_in_up = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        findViewById(R.id.layout).startAnimation(slide_in_up);
        Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(R.id.backLayout).startAnimation(fade_in);
    }

    public void animateOut() {
        Animation slide_out_down = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
        findViewById(R.id.layout).startAnimation(slide_out_down);
        Animation fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        findViewById(R.id.backLayout).startAnimation(fade_out);

        slide_out_down.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation a) {
            }

            public void onAnimationRepeat(Animation a) {
            }

            public void onAnimationEnd(Animation a) {
                finish();
            }

        });
    }

    public void sendChallenge(Question question) {
        Intent intent = new Intent();
        intent.putExtra("question", question);
        setResult(Utils.SEND_CHALLENGE_REQUEST, intent);
        animateOut();
    }


    public void showQuestionStatistics(Question question, View view) {
        Intent intent = new Intent(this, QuestionDetailsDialog.class);
        intent.putExtra("question", question);
        intent.putExtra("transitionName", ViewCompat.getTransitionName(view));

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                view,
                ViewCompat.getTransitionName(view));

        startActivity(intent, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        animateOut();
    }

}

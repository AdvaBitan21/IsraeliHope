package com.technion.android.israelihope.Dialogs;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.android.israelihope.Objects.Challenge;
import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.Objects.User;
import com.technion.android.israelihope.R;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import nl.dionsegijn.konfetti.KonfettiView;

import static nl.dionsegijn.konfetti.models.Shape.CIRCLE;
import static nl.dionsegijn.konfetti.models.Shape.RECT;

public class ChallengeDialog extends AppCompatActivity {

    private static int CHALLENGE_TIME_IN_SECONDS = 30;

    private Challenge challenge;
    private Question question;

    private String selected_answer = "";
    private TextView[] multichoice_texts;
    private TextView[] yesno_texts;
    private CountDownTimer countDownTimer;
    private boolean isDone = false; // true iff times up or answer was chosen

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_challenge);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        multichoice_texts = new TextView[]{
                findViewById(R.id.multichoice_choice1),
                findViewById(R.id.multichoice_choice2),
                findViewById(R.id.multichoice_choice3),
                findViewById(R.id.multichoice_choice4)
        };
        yesno_texts = new TextView[]{
                findViewById(R.id.yesno_choice1),
                findViewById(R.id.yesno_choice2)
        };

        initChallenge();
        updateChallengeState(Challenge.ChallengeState.IN_PROGRESS);
        initQuestionDetails();
        initTimer();
        initDismissButtons();
        initSendAnswerButton();
    }


    private void initChallenge() {
        Intent intent = getIntent();
        challenge = (Challenge) intent.getSerializableExtra("challenge");
    }

    private void initQuestionDetails() {
        FirebaseFirestore.getInstance().collection("Questions").document(challenge.getQuestionId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                question = task.getResult().toObject(Question.class);
                if (question.getQuestion_type().equals(Question.QuestionType.YES_NO))
                    setupYesNoQuestionUI();
                if (question.getQuestion_type().equals(Question.QuestionType.CLOSE))
                    setupMultichoiceQuestionUI();

                initAnswerButtons();
            }
        });
    }

    private void setupYesNoQuestionUI() {
        ((TextView) findViewById(R.id.question)).setText(question.getContent());
        findViewById(R.id.answers_yesno).setVisibility(View.VISIBLE);

        yesno_texts[0].setText(question.getPossible_answers().get(0));
        yesno_texts[1].setText(question.getPossible_answers().get(1));
    }

    private void setupMultichoiceQuestionUI() {
        ((TextView) findViewById(R.id.question)).setText(question.getContent());
        findViewById(R.id.answers_multichoice).setVisibility(View.VISIBLE);

        multichoice_texts[0].setText(question.getPossible_answers().get(0));
        multichoice_texts[1].setText(question.getPossible_answers().get(1));
        multichoice_texts[2].setText(question.getPossible_answers().get(2));
        multichoice_texts[3].setText(question.getPossible_answers().get(3));
    }

    private void initAnswerButtons() {

        if (question.getQuestion_type().equals(Question.QuestionType.CLOSE)) {
            multichoice_texts[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_answer = question.getPossible_answers().get(0);
                    checkSelected(multichoice_texts[0]);
                }
            });
            multichoice_texts[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_answer = question.getPossible_answers().get(1);
                    checkSelected(multichoice_texts[1]);
                }
            });
            multichoice_texts[2].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_answer = question.getPossible_answers().get(2);
                    checkSelected(multichoice_texts[2]);
                }
            });
            multichoice_texts[3].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_answer = question.getPossible_answers().get(3);
                    checkSelected(multichoice_texts[3]);
                }
            });
        }

        if (question.getQuestion_type().equals(Question.QuestionType.YES_NO)) {
            yesno_texts[0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_answer = question.getPossible_answers().get(0);
                    checkSelected(yesno_texts[0]);
                }
            });
            yesno_texts[1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_answer = question.getPossible_answers().get(1);
                    checkSelected(yesno_texts[1]);
                }
            });
        }

    }


    private void updateChallengeState(final Challenge.ChallengeState state) {
        challenge.setState(state);

        FirebaseFirestore.getInstance()
                .collection("Chats")
                .document(getIntent().getStringExtra("documentId"))
                .update("challenge", challenge);
    }


    private void initTimer() {

        final ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setMax(CHALLENGE_TIME_IN_SECONDS * 1000);

        countDownTimer = new CountDownTimer(CHALLENGE_TIME_IN_SECONDS * 1000, 10) {

            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (CHALLENGE_TIME_IN_SECONDS * 1000 - millisUntilFinished));
                ((TextView) findViewById(R.id.clock)).setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1));
            }

            @Override
            public void onFinish() {
                isDone = true;
                progressBar.setProgress(CHALLENGE_TIME_IN_SECONDS * 1000);
                ((TextView) findViewById(R.id.clock)).setText(String.valueOf(0));
                startTimesUpAnimations();
                updateChallengeState(Challenge.ChallengeState.OUT_OF_TIME);
            }
        };
        countDownTimer.start();
    }

    private void initSendAnswerButton() {
        Button send_answer = findViewById(R.id.send_answer);
        send_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                countDownTimer.cancel();
                isDone = true;

                final Challenge.ChallengeState state;
                if (selected_answer.equals(question.getRight_answers().get(0))) {
                    state = Challenge.ChallengeState.CORRECT;
                    updateQuestionCounters(true);
                    updateCorrectLayoutDetails();
                    startCorrectAnimations();
                } else {
                    state = Challenge.ChallengeState.WRONG;
                    updateQuestionCounters(false);
                    startWrongAnimations();
                }

                updateChallengeState(state);
            }
        });
    }

    private void initDismissButtons() {
        findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateDialogOut();
            }
        });
        findViewById(R.id.dismiss2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateDialogOut();
            }
        });
        findViewById(R.id.dismiss3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateDialogOut();
            }
        });
    }


    private void checkSelected(TextView checked) {
        if (!(selected_answer.isEmpty())) {

            checked.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            checked.setTextColor(getResources().getColor(R.color.white));

            for (TextView textView : multichoice_texts) {
                if (textView != checked) {
                    textView.setBackgroundTintList(null);
                    textView.setTextColor(getResources().getColor(R.color.black));
                }
            }

            for (TextView textView : yesno_texts) {
                if (textView != checked) {
                    textView.setBackgroundTintList(null);
                    textView.setTextColor(getResources().getColor(R.color.black));
                }
            }
        }
    }

    private TextView getRightAnswerTextView() {

        String right_answer = question.getRight_answers().get(0);

        if (question.getQuestion_type().equals(Question.QuestionType.CLOSE)) {
            for (TextView textView : multichoice_texts)
                if (textView.getText().toString().equals(right_answer))
                    return textView;
        }

        if (question.getQuestion_type().equals(Question.QuestionType.YES_NO)) {
            for (TextView textView : yesno_texts)
                if (textView.getText().toString().equals(right_answer))
                    return textView;
        }

        return null;
    }

    private TextView getSelectedAnswerTextView() {

        if (question.getQuestion_type().equals(Question.QuestionType.CLOSE)) {
            for (TextView textView : multichoice_texts)
                if (textView.getText().toString().equals(selected_answer))
                    return textView;
        }

        if (question.getQuestion_type().equals(Question.QuestionType.YES_NO)) {
            for (TextView textView : yesno_texts)
                if (textView.getText().toString().equals(selected_answer))
                    return textView;
        }

        return null;
    }

    private void updateCorrectLayoutDetails() {
        String points = String.valueOf(calculatePoints());
        ((TextView) findViewById(R.id.points)).setText(points);

        String congrats = getString(R.string.good_job) + "!";
        ((TextView) findViewById(R.id.congrats)).setText(congrats);
    }

    private void updateQuestionCounters(final boolean rightAnswer) {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                question.addAnswerByUser(user.getType());
                if (rightAnswer)
                    question.addRightAnswerByUser(user.getType());
                FirebaseFirestore.getInstance().collection("Questions").document(question.getId()).set(question);
            }
        });
    }

    private int calculatePoints() {
        int secondsLeft = Integer.parseInt(((TextView) findViewById(R.id.clock)).getText().toString());
        return 50 + secondsLeft;
    }

    @Override
    public void onBackPressed() {
        if (!isDone)
            return;
        super.onBackPressed();
    }

// ======================================= Animations =========================================== //

    private void startCorrectAnimations() {

        Animator scale = ObjectAnimator.ofPropertyValuesHolder(getRightAnswerTextView(),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.9f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.9f, 1f)
        );
        scale.setInterpolator(new OvershootInterpolator());
        scale.setDuration(600);

        scale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                int delay = 100;

                TextView[] array = question.getQuestion_type().equals(Question.QuestionType.CLOSE) ? multichoice_texts : yesno_texts;
                for (TextView answer : array) {

                    final Animator move = ObjectAnimator.ofPropertyValuesHolder(answer,
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, 1500f));
                    move.setInterpolator(new AnticipateInterpolator());
                    move.setDuration(1000);

                    if (answer != getRightAnswerTextView()) {
                        move.setStartDelay(delay);
                        delay += 100;
                    }
                    move.start();
                }

                animateQuestion();
                animateCorrectLayout();
                animateConfetti();
                animateCoinRotation();
                animateTimer();
                animateSendAnswerButton();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        scale.start();
        getRightAnswerTextView().setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));

    }

    private void startWrongAnimations() {

        getSelectedAnswerTextView().setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));

        Animator shake = ObjectAnimator.ofPropertyValuesHolder(getSelectedAnswerTextView(),
                PropertyValuesHolder.ofFloat(View.ROTATION, 0, 1, -1, 1, -1, 1, -1, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.95f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.95f, 1f))
                .setDuration(600);

        final Animator scale = ObjectAnimator.ofPropertyValuesHolder(getRightAnswerTextView(),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 1.1f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 1.1f, 1f))
                .setDuration(600);

        scale.setInterpolator(new OvershootInterpolator());
        scale.setStartDelay(500);

        shake.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                scale.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scale.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                getRightAnswerTextView().setTextColor(getResources().getColor(R.color.white));
                getRightAnswerTextView().setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            }

            @Override
            public void onAnimationEnd(Animator animator) {

                int delay = 100;

                TextView[] array = question.getQuestion_type().equals(Question.QuestionType.CLOSE) ? multichoice_texts : yesno_texts;
                for (TextView answer : array) {

                    final Animator move = ObjectAnimator.ofPropertyValuesHolder(answer,
                            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, 1500f));
                    move.setInterpolator(new AnticipateInterpolator());
                    move.setDuration(1000);

                    if (answer != getRightAnswerTextView()) {
                        move.setStartDelay(delay);
                        delay += 100;
                    }
                    move.start();
                }

                animateQuestion();
                animateWrongLayout();
                animateTimer();
                animateSendAnswerButton();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        shake.start();
    }

    private void startTimesUpAnimations() {

        animateTimesUpTimer();
        findViewById(R.id.send_answer).setEnabled(false);
        findViewById(R.id.send_answer).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Grey)));
        ((TextView) findViewById(R.id.question)).setTextColor(getResources().getColor(R.color.Grey));

        int delay = 1200;
        Animator move = null;
        TextView[] array = question.getQuestion_type().equals(Question.QuestionType.CLOSE) ? multichoice_texts : yesno_texts;
        for (TextView answer : array) {

            answer.setEnabled(false);
            answer.setTextColor(getResources().getColor(R.color.Grey));
            answer.setBackgroundTintList(null);

            move = ObjectAnimator.ofPropertyValuesHolder(answer,
                    PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, 1500f))
                    .setDuration(1000);
            move.setInterpolator(new AnticipateInterpolator());
            move.setStartDelay(delay);
            delay += 100;

            move.start();
        }

        move.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animateQuestion();
                animateTimer();
                animateSendAnswerButton();
                animateTimesUpLayout();
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void animateCorrectLayout() {
        RelativeLayout correctLayout = findViewById(R.id.correctLayout);
        correctLayout.setVisibility(View.VISIBLE);
        Animator scale_in = ObjectAnimator.ofPropertyValuesHolder(correctLayout,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1)
        );
        scale_in.setInterpolator(new OvershootInterpolator());
        scale_in.setDuration(500);
        scale_in.setStartDelay(1000);
        scale_in.start();
    }

    private void animateWrongLayout() {
        RelativeLayout wrongLayout = findViewById(R.id.wrongLayout);
        wrongLayout.setVisibility(View.VISIBLE);
        Animator scale_in = ObjectAnimator.ofPropertyValuesHolder(wrongLayout,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1)
        );
        scale_in.setInterpolator(new OvershootInterpolator());
        scale_in.setDuration(500);
        scale_in.setStartDelay(1000);
        scale_in.start();
    }

    private void animateTimesUpLayout() {
        RelativeLayout wrongLayout = findViewById(R.id.timesUpLayout);
        wrongLayout.setVisibility(View.VISIBLE);
        Animator scale_in = ObjectAnimator.ofPropertyValuesHolder(wrongLayout,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0, 1),
                PropertyValuesHolder.ofFloat(View.ALPHA, 1)
        );
        scale_in.setInterpolator(new OvershootInterpolator());
        scale_in.setDuration(500);
        scale_in.setStartDelay(1000);
        scale_in.start();
    }

    private void animateSendAnswerButton() {
        Button send_answer = findViewById(R.id.send_answer);
        Animator scale_out = ObjectAnimator.ofPropertyValuesHolder(send_answer,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0));
        scale_out.setDuration(300);
        scale_out.setStartDelay(500);
        scale_out.start();
    }

    private void animateQuestion() {

        TextView question = findViewById(R.id.question);

        ObjectAnimator.ofPropertyValuesHolder(question,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0, 10f))
                .setDuration(300)
                .start();
    }

    private void animateConfetti() {
        KonfettiView confetti = findViewById(R.id.viewConfetti);
        confetti.build()
                .addColors(getResources().getColor(R.color.colorPrimary),
                        getResources().getColor(R.color.colorPrimaryDark),
                        getResources().getColor(R.color.white))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(RECT, CIRCLE)
                .setPosition(-50f, confetti.getWidth() + 50f, -50f, -50f)
                .streamFor(70, 1000L);
    }

    private void animateCoinRotation() {
        ImageView coin = findViewById(R.id.coin);

        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5000);
        rotate.setRepeatCount(3);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setInterpolator(new LinearInterpolator());
        coin.startAnimation(rotate);
    }

    private void animateTimer() {
        RelativeLayout timer_layout = findViewById(R.id.timer_layout);

        ObjectAnimator
                .ofPropertyValuesHolder(timer_layout,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0))
                .setDuration(300)
                .start();
    }

    private void animateDialogOut() {
        RelativeLayout backLayout = findViewById(R.id.backLayout);
        RelativeLayout layout = findViewById(R.id.layout);
        RelativeLayout correctLayout = findViewById(R.id.correctLayout);
        RelativeLayout wrongLayout = findViewById(R.id.wrongLayout);

        final Animator scale_out_correct = ObjectAnimator
                .ofPropertyValuesHolder(correctLayout,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0))
                .setDuration(300);

        final Animator scale_out_wrong = ObjectAnimator
                .ofPropertyValuesHolder(wrongLayout,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0))
                .setDuration(300);

        final Animator scale_out_background = ObjectAnimator
                .ofPropertyValuesHolder(layout,
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0))
                .setDuration(300);

        final Animator fade_out = ObjectAnimator
                .ofPropertyValuesHolder(backLayout,
                        PropertyValuesHolder.ofFloat(View.ALPHA, 1, 0))
                .setDuration(300);

        scale_out_background.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                scale_out_correct.start();
                scale_out_wrong.start();
                fade_out.start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                onBackPressed();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scale_out_background.start();
    }

    private void animateTimesUpTimer() {
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorRed)));

        RelativeLayout timer_layout = findViewById(R.id.timer_layout);
        Animator shake = ObjectAnimator.ofPropertyValuesHolder(timer_layout,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 0, 10, -10, 10, -10, 10, -10, 0),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1, 0.95f, 1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, 0.95f, 1f))
                .setDuration(600);

        shake.start();
    }

// ============================================================================================== //

}



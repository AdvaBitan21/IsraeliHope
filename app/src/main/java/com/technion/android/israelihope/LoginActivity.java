package com.technion.android.israelihope;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Make status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            startMainActivity();

        setUpSignUpBtn();
        initLoginBtn();
    }


    private void setUpSignUpBtn() {

        findViewById(R.id.signupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(LoginActivity.this, findViewById(R.id.signupButton), "layout");
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void initLoginBtn() {

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                disappearErrorMsgs();

                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();

                if (!checkEnteredDetails(email, password))
                    return;

                showProgressBar();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startMainActivity();
                        } else {

                            switch (task.getException().getMessage()) {
                                case "There is no user record corresponding to this identifier. The user may have been deleted.":
                                case "The email address is badly formatted.":
                                case "The password is invalid or the user does not have a password.":
                                    findViewById(R.id.wrong_details).setVisibility(View.VISIBLE);
                                    break;
                                case "A network error (such as timeout, interrupted connection or unreachable host) has occurred.":
                                case "An internal error has occurred. [7:]":
                                    findViewById(R.id.no_internet).setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        hideProgressBar();
                        Utils.enableDisableClicks(LoginActivity.this, (ViewGroup) findViewById(android.R.id.content).getRootView(), true);
                    }
                });
            }
        });
    }

    private boolean checkEnteredDetails(String email, String password) {

        if (email.equals("") || password.equals("")) {
            findViewById(R.id.enter_details).setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    protected void startMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        editTokenId();
        startActivity(intent);
        finish();
    }


    private void showProgressBar() {

        disappearErrorMsgs();
        Utils.enableDisableClicks(this, (ViewGroup) findViewById(android.R.id.content).getRootView(), false);

        final Button login = findViewById(R.id.login);
        ValueAnimator shrink = ValueAnimator.ofInt(login.getMeasuredWidth(), Utils.dpToPx(this, 40));

        shrink.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = login.getLayoutParams();
                layoutParams.width = val;
                login.setLayoutParams(layoutParams);
            }
        });

        shrink.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                login.setText("");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                login.setVisibility(View.INVISIBLE);
                findViewById(R.id.progressBarLayout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        shrink.setDuration(400);
        shrink.start();
    }

    private void hideProgressBar() {

        final Button login = findViewById(R.id.login);
        ValueAnimator expand = ValueAnimator.ofInt(login.getMeasuredWidth(), Utils.dpToPx(this, 250));

        expand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = login.getLayoutParams();
                layoutParams.width = val;
                login.setLayoutParams(layoutParams);
            }
        });

        expand.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                findViewById(R.id.progressBarLayout).setVisibility(View.INVISIBLE);
                findViewById(R.id.login).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                login.setText(getString(R.string.login));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        expand.setDuration(400);
        expand.setStartDelay(400);
        expand.start();
    }

    private void disappearErrorMsgs() {
        findViewById(R.id.no_internet).setVisibility(View.INVISIBLE);
        findViewById(R.id.enter_details).setVisibility(View.INVISIBLE);
        findViewById(R.id.wrong_details).setVisibility(View.INVISIBLE);
    }

//    private void fadeOutAll() {
//        Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
//        findViewById(R.id.logo).startAnimation(fadeOut);
//        findViewById(R.id.login).startAnimation(fadeOut);
//        findViewById(R.id.password).startAnimation(fadeOut);
//        findViewById(R.id.email).startAnimation(fadeOut);
//        findViewById(R.id.signupButton).startAnimation(fadeOut);
//    }
//
//    private void fadeInAll() {
//        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
//        findViewById(R.id.logo).startAnimation(fadeIn);
//        findViewById(R.id.login).startAnimation(fadeIn);
//        findViewById(R.id.password).startAnimation(fadeIn);
//        findViewById(R.id.email).startAnimation(fadeIn);
//        findViewById(R.id.signupButton).startAnimation(fadeIn);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//       // fadeInAll();
//    }

    private void editTokenId() {

        String token_id = FirebaseInstanceId.getInstance().getToken();
        String user_email = mAuth.getCurrentUser().getEmail();

        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token_id", token_id);
        db.collection("Users").document(user_email).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

    }

}

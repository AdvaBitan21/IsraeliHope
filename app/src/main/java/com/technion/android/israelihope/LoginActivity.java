package com.technion.android.israelihope;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initLoginBtn();
        initBackButton();
    }

    private void initBackButton() {
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

                            hideProgressBar();
                            Utils.enableDisableClicks(LoginActivity.this, (ViewGroup) findViewById(android.R.id.content).getRootView(), true);

                        }
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
        startActivity(intent);
        finish();
    }


    private void showProgressBar() {

        Utils.enableDisableClicks(this, (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
        findViewById(R.id.login).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressBarLayout).setVisibility(View.VISIBLE);
        disappearErrorMsgs();
    }

    private void hideProgressBar() {

        findViewById(R.id.login).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBarLayout).setVisibility(View.GONE);
    }

    private void disappearErrorMsgs() {
        findViewById(R.id.no_internet).setVisibility(View.INVISIBLE);
        findViewById(R.id.enter_details).setVisibility(View.INVISIBLE);
        findViewById(R.id.wrong_details).setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

}

package com.technion.android.israelihope;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            redirectToMain();

        setUpLoginBtn();
        setUpSignupBtn();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }

    protected void redirectToMain() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void setUpLoginBtn(){
        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,0);
                fadeOutAll();
            }
        });
    }

    private void setUpSignupBtn(){
        findViewById(R.id.signupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up,0);
                fadeOutAll();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fadeInAll();
    }

    private void fadeOutAll() {
        Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        findViewById(R.id.logo).startAnimation(fadeOut);
        findViewById(R.id.loginButton).startAnimation(fadeOut);
        findViewById(R.id.signupButton).startAnimation(fadeOut);
    }

    private void fadeInAll() {
        Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        findViewById(R.id.logo).startAnimation(fadeIn);
        findViewById(R.id.loginButton).startAnimation(fadeIn);
        findViewById(R.id.signupButton).startAnimation(fadeIn);
    }
}

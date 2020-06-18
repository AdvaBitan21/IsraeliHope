package com.technion.android.israelihope;

import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class FirstQuizActivity extends AppCompatActivity {

    private int rights_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_quiz);

        // Make status bar white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        loadFragment(new FirstQuizWelcomeFragment());

    }

    /**
     * Replaces the current main fragment.
     */
    public void loadFragment(Fragment fragment) {

        if (fragment == null)
            throw new AssertionError("Attempt to load a null fragment.");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmant_container, fragment, fragment.toString())
                .addToBackStack(fragment.getClass().toString())
                .commit();
    }


    public void startQuizProgressBar() {
        ProgressBar progressBar = findViewById(R.id.firstQuizProgressbar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(Utils.AMOUNT_OF_QUESTIONS_FIRST_QUIZ * 100); // *100 for smooth animation
        progressBar.setProgress(0);
    }

    public void increaseQuizProgress() {
        ProgressBar progressBar = findViewById(R.id.firstQuizProgressbar);
        int progress = progressBar.getProgress();
        ObjectAnimator.ofInt(progressBar, "progress", progress + 100).start();
    }

    public int getRightsAmount() {
        return rights_amount;
    }

    public void setRightsAmount(int first_quiz_rights_amount) {
        this.rights_amount = first_quiz_rights_amount;
    }

    public void IncreaseScore() {
        rights_amount++;
    }

}

package com.technion.android.israelihope.Dialogs;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.R;

import androidx.appcompat.app.AppCompatActivity;

public class QuestionDetailsDialog extends AppCompatActivity {

    private Question mQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_question_details);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        mQuestion = (Question) getIntent().getSerializableExtra("question");

        handleEnterTransition();
        initOkButton();
        initStatistics();

    }

    private void handleEnterTransition() {

        supportPostponeEnterTransition();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = getIntent().getExtras().getString("transitionName");
            findViewById(R.id.layout).setTransitionName(transitionName);
        }

        supportStartPostponedEnterTransition();
    }

    private void initOkButton() {
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initStatistics() {

        ProgressBar jewishProgressbar = findViewById(R.id.jewish);
        int jewishRates = getGroupSuccessRate("Jewish");
        if (jewishRates == -1) {
            jewishProgressbar.setProgress(0);
            ((TextView) findViewById(R.id.jewish_percentage)).setText("-");
        } else {
            jewishProgressbar.setProgress(jewishRates);
            ((TextView) findViewById(R.id.jewish_percentage)).setText(jewishRates + "%");
        }

        ProgressBar muslimsProgressbar = findViewById(R.id.muslims);
        int muslimsRates = getGroupSuccessRate("Muslims");
        if (muslimsRates == -1) {
            muslimsProgressbar.setProgress(0);
            ((TextView) findViewById(R.id.muslims_percentage)).setText("-");
        } else {
            muslimsProgressbar.setProgress(muslimsRates);
            ((TextView) findViewById(R.id.muslims_percentage)).setText(muslimsRates + "%");
        }

        ProgressBar christiansProgressbar = findViewById(R.id.christians);
        int christiansRates = getGroupSuccessRate("Christians");
        if (christiansRates == -1) {
            christiansProgressbar.setProgress(0);
            ((TextView) findViewById(R.id.christians_percentage)).setText("-");
        } else {
            christiansProgressbar.setProgress(christiansRates);
            ((TextView) findViewById(R.id.christians_percentage)).setText(christiansRates + "%");
        }
    }

    private int getGroupSuccessRate(String groupName) {

        int totalAnswers = 0, totalRights = 0;

        switch (groupName) {
            case "Jewish":
                totalAnswers = mQuestion.getCountAnswers().get("Jewish1") +
                        mQuestion.getCountAnswers().get("Jewish2") +
                        mQuestion.getCountAnswers().get("Jewish3");
                totalRights = mQuestion.getCountRights().get("Jewish1") +
                        mQuestion.getCountRights().get("Jewish2") +
                        mQuestion.getCountRights().get("Jewish3");
                break;
            case "Muslims":
                totalAnswers = mQuestion.getCountAnswers().get("Muslim1") +
                        mQuestion.getCountAnswers().get("Muslim2") +
                        mQuestion.getCountAnswers().get("Muslim3");
                totalRights = mQuestion.getCountRights().get("Muslim1") +
                        mQuestion.getCountRights().get("Muslim2") +
                        mQuestion.getCountRights().get("Muslim3");
                break;
            case "Christians":
                totalAnswers = mQuestion.getCountAnswers().get("Christian1") +
                        mQuestion.getCountAnswers().get("Christian2") +
                        mQuestion.getCountAnswers().get("Christian3");
                totalRights = mQuestion.getCountRights().get("Christian1") +
                        mQuestion.getCountRights().get("Christian2") +
                        mQuestion.getCountRights().get("Christian3");
                break;
        }

        if (totalAnswers == 0)
            return -1;
        return (int) ((totalRights * 100.0f) / totalAnswers);
    }

}

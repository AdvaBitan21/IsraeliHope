package com.technion.android.israelihope.Dialogs;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.technion.android.israelihope.Objects.Question;
import com.technion.android.israelihope.R;
import com.technion.android.israelihope.SendChallengeActivity;
import com.technion.android.israelihope.Utils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddContentToChatDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_content_to_chat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setupAnimations();
        initCancelButton();
        initChallengeButtons();
    }


    private void initCancelButton() {
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateOut();
            }
        });
    }

    private void initChallengeButtons() {

        findViewById(R.id.multichoice_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddContentToChatDialog.this, SendChallengeActivity.class);
                intent.putExtra("question_type", Utils.QuestionType.CLOSE);
                startActivityForResult(intent, Utils.SEND_CHALLENGE_REQUEST);
                animateOut();
            }
        });
        findViewById(R.id.yesno_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddContentToChatDialog.this, SendChallengeActivity.class);
                intent.putExtra("question_type", Utils.QuestionType.YES_NO);
                startActivityForResult(intent, Utils.SEND_CHALLENGE_REQUEST);
                animateOut();
            }
        });
    }

    private void setupAnimations() {
        Animation slide_in_up = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
        findViewById(R.id.buttons).startAnimation(slide_in_up);
        findViewById(R.id.cancel).startAnimation(slide_in_up);
        Animation fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        findViewById(R.id.backLayout).startAnimation(fade_in);
    }

    public void animateOut() {
        Animation slide_out_down = AnimationUtils.loadAnimation(this, R.anim.slide_out_down);
        findViewById(R.id.buttons).startAnimation(slide_out_down);
        findViewById(R.id.cancel).startAnimation(slide_out_down);
        Animation fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        findViewById(R.id.backLayout).startAnimation(fade_out);
    }

    @Override
    public void onBackPressed() {
        animateOut();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SEND_CHALLENGE_REQUEST && data != null) {
            Question question = (Question) data.getSerializableExtra("question");
            Intent intent = new Intent();
            intent.putExtra("question", question);
            setResult(Utils.SEND_CHALLENGE_REQUEST, intent);
        }

        finish();
    }
}

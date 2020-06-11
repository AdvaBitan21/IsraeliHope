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

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class AddContentToChatDialog extends AppCompatActivity {

    private String contentType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_content_to_chat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setupAnimations();
        initCancelButton();
        initChallengeButtons();
        initCameraButton();
    }


    private void initCancelButton() {
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initChallengeButtons() {
        findViewById(R.id.multichoice_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentType = "CHALLENGE";
                Intent intent = new Intent(AddContentToChatDialog.this, SendChallengeActivity.class);
                intent.putExtra("question_type", Question.QuestionType.CLOSE);
                startActivityForResult(intent, Utils.SEND_CHALLENGE_REQUEST);
                animateOut();
            }
        });
        findViewById(R.id.yesno_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentType = "CHALLENGE";
                Intent intent = new Intent(AddContentToChatDialog.this, SendChallengeActivity.class);
                intent.putExtra("question_type", Question.QuestionType.YES_NO);
                startActivityForResult(intent, Utils.SEND_CHALLENGE_REQUEST);
                animateOut();
            }
        });
    }

    private void initCameraButton() {
        findViewById(R.id.picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentType = "PICTURE";
                Utils.openImageChooser(AddContentToChatDialog.this);
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
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SEND_CHALLENGE_REQUEST && data != null) {
            Question question = (Question) data.getSerializableExtra("question");
            Intent intent = new Intent();
            intent.putExtra("contentType", contentType);
            intent.putExtra("question", question);
            setResult(RESULT_OK, intent);
        }

        if (requestCode == Utils.OPEN_GALLERY_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Intent intent = new Intent();
            intent.putExtra("contentType", contentType);
            intent.setData(data.getData());
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            setResult(RESULT_OK, intent);
        }

        finish();
    }
}

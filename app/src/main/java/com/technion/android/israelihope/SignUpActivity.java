package com.technion.android.israelihope;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.technion.android.israelihope.Objects.User;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private Bitmap profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Shared element - enter transition
        supportPostponeEnterTransition();
        supportStartPostponedEnterTransition();

        setupImagePicker();
        initBackButton();
        initFieldIndicators();
        initStep1NextButton();
        initReligionSpinner();
        initAcademicRoleSpinner();
        initReligionMeasureSpinner();
        initStep2NextButton();
        initStep3NextButton();
    }


    private void initBackButton() {
        findViewById(R.id.backButton).setOnClickListener(view -> onBackPressed());
    }


    private void initStep1NextButton() {
        findViewById(R.id.step1Next).setOnClickListener(view -> {
            showProgressBar(findViewById(R.id.step1Next), findViewById(R.id.progressBarLayout));

            //Check if email address already exists
            String email = ((TextView) findViewById(R.id.email)).getText().toString();
            FirebaseFirestore.getInstance().collection("Users").document(email).get().addOnCompleteListener(task -> {
                if (task.getResult().exists()) {
                    findViewById(R.id.emailIndicator).setVisibility(View.GONE);
                    findViewById(R.id.email_exists).setVisibility(View.VISIBLE);
                } else {
                    showStep2Layout();
                }

                hideProgressBar(findViewById(R.id.step1Next), findViewById(R.id.progressBarLayout));
            });
        });
    }

    private void initFieldIndicators() {

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkFields();
            }
        };
        ((EditText) findViewById(R.id.userName)).addTextChangedListener(textWatcher);
        ((EditText) findViewById(R.id.email)).addTextChangedListener(textWatcher);
        ((EditText) findViewById(R.id.password)).addTextChangedListener(textWatcher);
    }

    private boolean checkFields() {

        findViewById(R.id.email_exists).setVisibility(View.GONE);

        String username = ((TextView) findViewById(R.id.userName)).getText().toString();
        String email = ((TextView) findViewById(R.id.email)).getText().toString();
        String password = ((TextView) findViewById(R.id.password)).getText().toString();

        setNextButtonEnabled(findViewById(R.id.step1Next), false);
        boolean enable = true;

        if (username.isEmpty()) {
            findViewById(R.id.userNameIndicator).setVisibility(View.GONE);
            findViewById(R.id.illegal_username).setVisibility(View.GONE);
            enable = false;
        } else {
            findViewById(R.id.userNameIndicator).setVisibility(View.VISIBLE);
            findViewById(R.id.illegal_username).setVisibility(View.GONE);
        }

        if (email.isEmpty()) {
            findViewById(R.id.emailIndicator).setVisibility(View.GONE);
            findViewById(R.id.illegal_email).setVisibility(View.GONE);
            enable = false;
        } else if (!(email.matches("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))) {
            findViewById(R.id.emailIndicator).setVisibility(View.GONE);
            findViewById(R.id.illegal_email).setVisibility(View.VISIBLE);
            enable = false;
        } else {
            findViewById(R.id.emailIndicator).setVisibility(View.VISIBLE);
            findViewById(R.id.illegal_email).setVisibility(View.GONE);
        }

        if (password.isEmpty()) {
            findViewById(R.id.passwordIndicator).setVisibility(View.GONE);
            findViewById(R.id.illegal_password).setVisibility(View.GONE);
            enable = false;
        } else if (password.length() < 6) {
            findViewById(R.id.passwordIndicator).setVisibility(View.GONE);
            findViewById(R.id.illegal_password).setVisibility(View.VISIBLE);
            enable = false;
        } else {
            findViewById(R.id.passwordIndicator).setVisibility(View.VISIBLE);
            findViewById(R.id.illegal_password).setVisibility(View.GONE);
        }

        if (profileImage == null)
            enable = false;

        setNextButtonEnabled(findViewById(R.id.step1Next), enable);
        return enable;
    }

    private void setupImagePicker() {
        findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openImageChooser(SignUpActivity.this);
            }
        });

        findViewById(R.id.editImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openImageChooser(SignUpActivity.this);
            }
        });
    }


    private void showStep2Layout() {

        ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.step1),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 1000))
                .setDuration(400)
                .start();

        findViewById(R.id.step2).setVisibility(View.VISIBLE);
        ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.step2),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -1000, 0))
                .setDuration(400)
                .start();

        Animator animator = ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.circle_one),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 1))
                .setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                findViewById(R.id.circle_one).setBackground(getResources().getDrawable(R.drawable.check_filled));
                findViewById(R.id.number_one).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ((TextView) findViewById(R.id.number_two)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                findViewById(R.id.circle_two).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                findViewById(R.id.line_one).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();

    }

    private void initStep2NextButton() {
        findViewById(R.id.step2Next).setOnClickListener(view -> {
            signUpUser(getUser());
        });
    }

    private void initReligionSpinner() {
        Spinner spinner = findViewById(R.id.religionSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_style) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v.findViewById(R.id.text)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, R.drawable.religion, 0);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.text)).setText(getItem(getCount()));
                    ((TextView) v.findViewById(R.id.text)).setTextColor(getColor(R.color.colorGrey));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };

        adapter.addAll(getResources().getStringArray(R.array.religions));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkSpinners();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initReligionMeasureSpinner() {
        Spinner spinner = findViewById(R.id.religionMeasureSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_style) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v.findViewById(R.id.text)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, R.drawable.measure_clock, 0);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.text)).setText(getItem(getCount()));
                    ((TextView) v.findViewById(R.id.text)).setTextColor(getColor(R.color.colorGrey));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };

        adapter.addAll(getResources().getStringArray(R.array.religion_measures));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkSpinners();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initAcademicRoleSpinner() {
        Spinner spinner = findViewById(R.id.academicSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_style) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v.findViewById(R.id.text)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, R.drawable.student_hat, 0);
                if (position == getCount()) {
                    ((TextView) v.findViewById(R.id.text)).setText(getItem(getCount()));
                    ((TextView) v.findViewById(R.id.text)).setTextColor(getColor(R.color.colorGrey));
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };

        adapter.addAll(getResources().getStringArray(R.array.academic_roles));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkSpinners();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private boolean checkSpinners() {

        String academicRole = ((Spinner) findViewById(R.id.academicSpinner)).getSelectedItem().toString();
        String religion = ((Spinner) findViewById(R.id.religionSpinner)).getSelectedItem().toString();
        String religionMeasure = ((Spinner) findViewById(R.id.religionMeasureSpinner)).getSelectedItem().toString();

        setNextButtonEnabled(findViewById(R.id.step2Next), false);
        boolean enable = true;

        if (academicRole.equals("התפקיד האקדמי שלך") ||
                religion.equals("הדת שלך") ||
                religionMeasure.equals("מידת הדתיות שלך"))
            enable = false;

        setNextButtonEnabled(findViewById(R.id.step2Next), enable);
        return enable;
    }


    private User.UserType getUserType() {
        String religion = ((Spinner) findViewById(R.id.religionSpinner)).getSelectedItem().toString();
        String religionMeasure = ((Spinner) findViewById(R.id.religionMeasureSpinner)).getSelectedItem().toString();

        if (religion.equals("יהדות")) {
            if (religionMeasure.equals("חילוני"))
                return User.UserType.Jewish1;
            if (religionMeasure.equals("מסורתי"))
                return User.UserType.Jewish2;
            if (religionMeasure.equals("דתי"))
                return User.UserType.Jewish3;
        }

        if (religion.equals("איסלאם")) {
            if (religionMeasure.equals("חילוני"))
                return User.UserType.Muslim1;
            if (religionMeasure.equals("מסורתי"))
                return User.UserType.Muslim2;
            if (religionMeasure.equals("דתי"))
                return User.UserType.Muslim3;
        }

        if (religion.equals("נצרות")) {
            if (religionMeasure.equals("חילוני"))
                return User.UserType.Christian1;
            if (religionMeasure.equals("מסורתי"))
                return User.UserType.Christian2;
            if (religionMeasure.equals("דתי"))
                return User.UserType.Christian3;
        }

        if (religion.equals("הדת הדרוזית")) {
            if (religionMeasure.equals("חילוני"))
                return User.UserType.Druze1;
            if (religionMeasure.equals("מסורתי"))
                return User.UserType.Druze2;
            if (religionMeasure.equals("דתי"))
                return User.UserType.Druze3;
        }

        return null;
    }

    private User.AcademicRole getAcademicRole() {
        String academicRole = ((Spinner) findViewById(R.id.academicSpinner)).getSelectedItem().toString();

        if (academicRole.equals("סטודנט"))
            return User.AcademicRole.Student;
        if (academicRole.equals("סגל מנהלי"))
            return User.AcademicRole.Administrative_Staff;
        if (academicRole.equals("סגל אקדמי"))
            return User.AcademicRole.Academic_Staff;

        return null;
    }

    private User getUser() {

        if (!checkFields() || !checkSpinners())
            throw new AssertionError("User details error");

        String email = ((TextView) findViewById(R.id.email)).getText().toString();
        String username = ((TextView) findViewById(R.id.userName)).getText().toString();
        User.UserType userType = getUserType();
        User.AcademicRole academicRole = getAcademicRole();

        return new User(email, username, userType, academicRole);
    }

    private void signUpUser(User user) {

        user.setTokenId(FirebaseInstanceId.getInstance().getToken());
        String password = ((TextView) findViewById(R.id.password)).getText().toString();

        showProgressBar(findViewById(R.id.step2Next), findViewById(R.id.progressBarLayout2));
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Utils.uploadProfileImage(profileImage, user.getEmail());
                        FirebaseFirestore.getInstance()
                                .collection("Users")
                                .document(user.getEmail())
                                .set(user).addOnCompleteListener(task1 -> {
                            showStep3Layout();
                        });
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    hideProgressBar(findViewById(R.id.step2Next), findViewById(R.id.progressBarLayout2));
                    Utils.enableDisableClicks(SignUpActivity.this, (ViewGroup) findViewById(android.R.id.content).getRootView(), true);
                });
    }


    private void showStep3Layout() {

        ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.step2),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 1000))
                .setDuration(400)
                .start();

        findViewById(R.id.step3).setVisibility(View.VISIBLE);
        ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.step3),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, -1000, 0))
                .setDuration(400)
                .start();

        Animator animator = ObjectAnimator.ofPropertyValuesHolder(findViewById(R.id.circle_two),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 1),
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.2f, 1))
                .setDuration(500);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                findViewById(R.id.circle_two).setBackground(getResources().getDrawable(R.drawable.check_filled));
                findViewById(R.id.number_two).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                ((TextView) findViewById(R.id.number_three)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                findViewById(R.id.circle_three).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                findViewById(R.id.line_two).setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private void initStep3NextButton() {
        findViewById(R.id.step3finish).setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, FirstQuizActivity.class);
            startActivity(intent);
            finish();
        });
    }


    private void setNextButtonEnabled(Button button, boolean enable) {

        if (enable) {
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            button.setEnabled(true);
        } else {
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.transparentGrey)));
            button.setEnabled(false);
        }
    }

    private void showProgressBar(Button button, RelativeLayout progressLayout) {
        Utils.enableDisableClicks(this, (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
        ValueAnimator shrink = ValueAnimator.ofInt(button.getMeasuredWidth(), Utils.dpToPx(this, 40));

        shrink.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
            layoutParams.width = val;
            button.setLayoutParams(layoutParams);
        });

        shrink.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                button.setText("");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                button.setVisibility(View.INVISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
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

    private void hideProgressBar(Button button, RelativeLayout progressLayout) {
        Utils.enableDisableClicks(this, (ViewGroup) findViewById(android.R.id.content).getRootView(), true);
        ValueAnimator expand = ValueAnimator.ofInt(button.getMeasuredWidth(), Utils.dpToPx(this, 300));

        expand.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                layoutParams.width = val;
                button.setLayoutParams(layoutParams);
            }
        });

        expand.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                progressLayout.setVisibility(View.INVISIBLE);
                button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                button.setText(getString(R.string.next));
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.OPEN_GALLERY_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                profileImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Glide.with(this).load(profileImage).into((CircleImageView) findViewById(R.id.profile_image));
                checkFields();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //just need to call it
    private void updateCountUsersHist(final User.UserType userType) {
        final DocumentReference statRef = FirebaseFirestore.getInstance().collection("Statistics").document("CountUsers");
        statRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    StatisticsFragment.HistogramObject document = task.getResult().toObject(StatisticsFragment.HistogramObject.class);
                    document.addToHistByUser("" + userType);
                    statRef.update("hist", document.getHist());
                }
            }
        });
    }

}

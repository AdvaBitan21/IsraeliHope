package com.technion.android.israelihope;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.technion.android.israelihope.Objects.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Bitmap profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        setupDatePicker();
        setupImagePicker();
        initCityAutocomplete();
        initSignUpButton();
        initBackButton();

    }


    private void setupDatePicker() {

        final EditText birth_date = findViewById(R.id.birth_date);
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

                birth_date.setText(sdf.format(myCalendar.getTime()));
            }

        };

        birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(SignUpActivity.this,
                        R.style.MySpinnerDatePickerStyle, date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });
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

    private void initBackButton() {
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initSignUpButton() {
        findViewById(R.id.signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disappearErrorMsgs();
                signUpUser(getUser());
            }
        });
    }

    private void initCityAutocomplete() {
        String[] cities = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, cities);
        AutoCompleteTextView textView = findViewById(R.id.city);
        textView.setAdapter(adapter);
    }


    private void signUpUser(final User user1) {

        if (user1 == null) return;

        if (profileImage == null) {
            findViewById(R.id.illegal_image).setVisibility(View.VISIBLE);
            return;
        }
        String token_id = FirebaseInstanceId.getInstance().getToken();
        user1.setToken_id(token_id);

        String password = getPassword();
        assert password != null;

        showProgressBar();
        mAuth.createUserWithEmailAndPassword(user1.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Utils.uploadProfileImage(profileImage, user1.getEmail());
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(user1.getEmail())
                            .set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Intent intent = new Intent(SignUpActivity.this, FirstQuizActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {

                    switch (task.getException().getMessage()) {
                        case "The email address is already in use by another account.":
                            findViewById(R.id.email_exists).setVisibility(View.VISIBLE);
                            break;
                        case "The email address is badly formatted.":
                            findViewById(R.id.illegal_mail).setVisibility(View.VISIBLE);
                            break;
                        case "A network error (such as timeout, interrupted connection or unreachable host) has occurred.":
                        case "An internal error has occurred. [7:]":
                            findViewById(R.id.no_internet).setVisibility(View.VISIBLE);
                            break;
                        default:
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                    }

                    hideProgressBar();
                    Utils.enableDisableClicks(SignUpActivity.this, (ViewGroup) findViewById(android.R.id.content).getRootView(), true);
                }
            }
        });

    }

    private User getUser() {

        String name = getName();
        if (name == null) {
            findViewById(R.id.illegal_name).setVisibility(View.VISIBLE);
            return null;
        }

        String email = getEmail();
        if (email == null) {
            findViewById(R.id.illegal_mail).setVisibility(View.VISIBLE);
            return null;
        }

        String password = getPassword();
        if (password == null) {
            findViewById(R.id.illegal_password).setVisibility(View.VISIBLE);
            return null;
        }

        String city = getCity();
        if (city == null) {
            findViewById(R.id.illegal_city).setVisibility(View.VISIBLE);
            return null;
        }

        String birth_date = getBirthDate();
        if (birth_date == null) {
            findViewById(R.id.illegal_birth_date).setVisibility(View.VISIBLE);
            return null;
        }

        return new User(email, name, city, birth_date);
    }

    private String getName() {
        String name = ((EditText) findViewById(R.id.userName)).getText().toString();
        if (name.equals(""))
            return null;

        return name;
    }

    private String getEmail() {
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        if (email.equals("") || !(email.matches("[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")))
            return null;

        return email.toLowerCase();
    }

    private String getPassword() {
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        if (password.length() < 6)
            return null;

        return password;
    }

    private String getBirthDate() {
        String birth_date = ((EditText) findViewById(R.id.birth_date)).getText().toString();
        if (birth_date.equals(""))
            return null;

        return birth_date;
    }

    private String getCity() {

        String[] cities = getResources().getStringArray(R.array.cities);

        String city = ((AutoCompleteTextView) findViewById(R.id.city)).getText().toString().trim();
        if (city.equals("") || !Arrays.asList(cities).contains(city))
            return null;

        return city;

    }


    private void showProgressBar() {

        Utils.enableDisableClicks(this, (ViewGroup) findViewById(android.R.id.content).getRootView(), false);
        findViewById(R.id.signup).setVisibility(View.INVISIBLE);
        findViewById(R.id.progressBarLayout).setVisibility(View.VISIBLE);
        disappearErrorMsgs();
    }

    private void hideProgressBar() {

        findViewById(R.id.signup).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBarLayout).setVisibility(View.GONE);
    }

    private void disappearErrorMsgs() {
        findViewById(R.id.email_exists).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_mail).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_password).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_name).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_birth_date).setVisibility(View.INVISIBLE);
        findViewById(R.id.no_internet).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_image).setVisibility(View.INVISIBLE);
        findViewById(R.id.illegal_city).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.slide_out_down);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

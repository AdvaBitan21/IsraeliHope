package com.technion.android.israelihope;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.android.israelihope.Dialogs.ChangePasswordDialog;
import com.technion.android.israelihope.Objects.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private User mUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUser = ((MainActivity) getActivity()).getCurrentUser();
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupAnimations();
        getUserDetails();
        initChangeImage();
        initEditUserName();
        initChangePasswordDialog();
        initLogOutButton();
    }


    private void getUserDetails() {

        TextView userName = getView().findViewById(R.id.userName);
        userName.setText(mUser.getUserName());

        CircleImageView profileImage = getView().findViewById(R.id.profile_image);
        Utils.loadProfileImage(getContext(), profileImage, mUser.getEmail());

    }


    private void initChangeImage() {
        getView().findViewById(R.id.editImageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openImageChooser(getActivity());
            }
        });
    }

    private void initChangePasswordDialog() {
        getView().findViewById(R.id.changePasswordButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordDialog dialog = new ChangePasswordDialog();
                dialog.show(getActivity().getSupportFragmentManager(), "change password dialog");
            }
        });
    }

    private void initEditUserName() {

        final EditText userName = getView().findViewById(R.id.userName);
        final ImageView editUserName = getView().findViewById(R.id.editUserName);
        final Button saveUserName = getView().findViewById(R.id.saveUserName);

        editUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserName.setVisibility(View.INVISIBLE);
                saveUserName.setVisibility(View.VISIBLE);
                userName.setFocusableInTouchMode(true);
                userName.setFocusable(true);
                userName.requestFocus();
                Utils.openKeyboard(getActivity(), userName);
            }
        });

        userName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                editUserName.setVisibility(View.VISIBLE);
                saveUserName.setVisibility(View.INVISIBLE);
                userName.setFocusable(false);
                updateUserName(userName.getText().toString().trim());
                return false;
            }
        });

        saveUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editUserName.setVisibility(View.VISIBLE);
                saveUserName.setVisibility(View.INVISIBLE);
                userName.setFocusable(false);
                updateUserName(userName.getText().toString().trim());
            }
        });
    }


    private void initLogOutButton() {

        final Button logoutButton = getActivity().findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.enableDisableClicks(getActivity(), (ViewGroup) getView(), false);
                removeTokenId();
            }
        });
    }


    private void removeTokenId() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> removeToken = new HashMap<>();
        removeToken.put("tokenId", "");
        db.collection("Users").document(mAuth.getCurrentUser().getEmail()).update(removeToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                signOut();
            }
        });
    }

    private void signOut(){
        Utils.status("offline");
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


    private void updateUserName(String name) {
        mUser.setUserName(name);
        ((MainActivity) getActivity()).setCurrentUser(mUser);

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(mUser.getEmail())
                .update("userName", name);
    }


    private void setupAnimations() {
        Animation slide_in_down = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_down);
        getView().findViewById(R.id.layout).startAnimation(slide_in_down);
        Animation fade_in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        getView().findViewById(R.id.backLayout).startAnimation(fade_in);
    }

    public void animateOut() {
        Animation slide_out_up = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_up);
        getView().findViewById(R.id.layout).startAnimation(slide_out_up);
        Animation fade_out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        getView().findViewById(R.id.backLayout).startAnimation(fade_out);

        slide_out_up.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation a) {
            }

            public void onAnimationRepeat(Animation a) {
            }

            public void onAnimationEnd(Animation a) {
                getFragmentManager().popBackStack();
            }

        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.OPEN_GALLERY_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap profileImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                Glide.with(this).load(profileImage).into((CircleImageView) getView().findViewById(R.id.profile_image));
                Glide.with(this).load(profileImage).into((CircleImageView) getActivity().findViewById(R.id.profile));
                Utils.uploadProfileImage(profileImage, mUser.getEmail());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

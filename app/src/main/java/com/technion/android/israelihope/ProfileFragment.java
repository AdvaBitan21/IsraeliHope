package com.technion.android.israelihope;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.technion.android.israelihope.Objects.User;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Animation slide_in_down = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_down);
        getView().findViewById(R.id.layout).startAnimation(slide_in_down);
        Animation fade_in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        getView().findViewById(R.id.backLayout).startAnimation(fade_in);

        getUserDetails();
    }


    private void getUserDetails() {

        User user = ((MainActivity) getActivity()).getCurrentUser();

        TextView userName = getView().findViewById(R.id.userName);
        userName.setText(user.getUserName());

        TextView userBirthday = getView().findViewById(R.id.birthDate);
        userBirthday.setText(user.getBirthDate());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        CircleImageView profileImage = getView().findViewById(R.id.profile_image);
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(profileImage);

    }

}

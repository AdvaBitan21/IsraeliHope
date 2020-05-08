package com.technion.android.israelihope;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Objects.User;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private int first_quiz_rights_amount;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mAuth = FirebaseAuth.getInstance();

        // Make status bar white
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initCurrentUser();
        initToolBar();
        loadFragment(new ChatsFragment()); // TODO - when the chats fragment won't crush
    }


    private void initToolBar() {

        FirebaseUser user = mAuth.getCurrentUser();
        final CircleImageView profileImage = findViewById(R.id.profile);
        Glide.with(this).load(user.getPhotoUrl()).into(profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getCurrentFragment() instanceof ProfileFragment) {
                    onBackPressed();
                    return;
                }
                loadFragment(new ProfileFragment());
            }
        });

    }


    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragmant_container);
    }


    private void initCurrentUser() {

        CollectionReference requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
        Query requestQuery = requestCollectionRef.whereEqualTo("email", mAuth.getCurrentUser().getEmail());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mUser = document.toObject(User.class);
                    }
                }
            }
        });
    }

    public User getCurrentUser() {
        return mUser;
    }

    public void setCurrentUser(User user) {
        this.mUser = user;
    }


    public int getFirstQuizRightsAmount() {
        return first_quiz_rights_amount;
    }

    public void setFirstQuizRightsAmount(int first_quiz_rights_amount) {
        this.first_quiz_rights_amount = first_quiz_rights_amount;
    }


    public void IncreaseFirstQuizScore() {
        first_quiz_rights_amount++;
    }

    /**
     * Replaces the current main fragment.
     */
    public boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmant_container, fragment, fragment.toString())
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
            return true;
        }

        return false;
    }


    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //status("offline");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getCurrentFragment().onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        // Customize onBackPressed for specific fragments //
        if(getCurrentFragment() instanceof ProfileFragment) {
            ((ProfileFragment)getCurrentFragment()).animateOut();
            return;
        }

        if (count == 1) finish();
        else if (count > 1) getSupportFragmentManager().popBackStack();
        else super.onBackPressed();

    }


}


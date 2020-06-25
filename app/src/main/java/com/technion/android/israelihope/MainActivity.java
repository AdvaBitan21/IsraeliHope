package com.technion.android.israelihope;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Objects.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public User mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Make status bar white
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initCurrentUser();
        initToolBar();

        if (mAuth.getCurrentUser().getEmail().equals("admin@gmail.com")) {
            adminUIMode();
            loadFragment(new AdminMainFragment());
        } else
            loadFragment(new ChatsFragment());
    }

    private void adminUIMode() {
        findViewById(R.id.statistics).setVisibility(View.GONE);
        findViewById(R.id.add_users).setVisibility(View.GONE);
        findViewById(R.id.profile).setVisibility(View.GONE);
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
                addFragment(new ProfileFragment());
            }
        });

        ImageView add_users = findViewById(R.id.add_users);
        add_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new UsersFragment());
            }
        });

        ImageView statistics = findViewById(R.id.statistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new StatisticsFragment());
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
                        if (mUser.getScoreFirstQuiz() < 0)
                            throw new AssertionError("Current user didnt do the first quiz.");
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


    /**
     * Replaces the current main fragment.
     */
    public void loadFragment(Fragment fragment) {

        if (fragment == null)
            throw new AssertionError("Attempt to load a null fragment.");

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmant_container, fragment, fragment.toString())
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
        }
    }

    /**
     * Adds a new fragment on top of the current main fragment.
     * The current fragment will remain at it's current state.
     */
    public void addFragment(Fragment fragment) {

        if (fragment == null)
            throw new AssertionError("Attempt to load a null fragment.");

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmant_container, fragment, fragment.toString())
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.status("offline");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Utils.status("online");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCurrentFragment().onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        if (!Utils.clicksEnabled) return;

        int count = getSupportFragmentManager().getBackStackEntryCount();

        // Customize onBackPressed for specific fragments //
        if (getCurrentFragment() instanceof ProfileFragment) {
            ((ProfileFragment) getCurrentFragment()).animateOut();
            return;
        }
        if (getCurrentFragment() instanceof FirstQuizFinishFragment) {
            return;
        }

        if (count == 1) finish();
        else if (count > 1) getSupportFragmentManager().popBackStack();
        else super.onBackPressed();
    }

}


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
import com.google.firebase.database.DatabaseReference;
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

import static com.technion.android.israelihope.Utils.uploadQuestionToFirebase;

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



        //Utils.uploadQuestionToFirebase();
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
                    }

                    if(mUser.getScore_first_quiz()>=0)
                        loadFragment(new ChatsFragment());
                    else
                        loadFragment(new FirstQuizWelcomeFragment());

                }
            }
        });
    }
    public boolean addFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmant_container, fragment, fragment.toString())
                    .addToBackStack(fragment.getClass().toString())
                    .commit();
            return true;
        }
        return false;
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

    /**
     * Adds a new fragment on top of the current main fragment.
     * The current fragment will remain at it's current state.
     */
//    public boolean addFragment(Fragment fragment) {
//
//        if (fragment != null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.fragmant_container, fragment, fragment.toString())
//                    .addToBackStack(fragment.getClass().toString())
//                    .commit();
//            return true;
//        }
//
//        return false;
//    }


//    private void status(String status) {
//        firebaseUser = mAuth.getCurrentUser();
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//
//        FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getEmail()).update(hashMap);
//    }

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

//    @Override
//    protected void onStop() {
//        super.onStop();
//        status("offline");
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCurrentFragment().onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        // Customize onBackPressed for specific fragments //
        if (getCurrentFragment() instanceof ProfileFragment) {
            ((ProfileFragment) getCurrentFragment()).animateOut();
            return;
        }
        if(getCurrentFragment() instanceof FirstQuizFinishFragment)
            return;
        if (count == 1) finish();
        else if (count > 1) getSupportFragmentManager().popBackStack();
        else super.onBackPressed();

    }

}


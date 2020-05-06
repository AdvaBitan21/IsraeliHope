package com.technion.android.israelihope;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private int first_quiz_rights_amount;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;

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



        initToolBar();
    }


    public int getFirstQuizRightsAmount() {
        return first_quiz_rights_amount;
    }

    public void setFirstQuizRightsAmount(int first_quiz_rights_amount) {
        this.first_quiz_rights_amount = first_quiz_rights_amount;
    }


    public void IncreaseFirstQuizScore(){
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

    @Override
    public void onBackPressed()
    {
       /* if (!Utils.clicksEnabled) {
            return;
        }*/
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 1)
            finish();
        else if (getFragmentManager().getBackStackEntryCount() > 1)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    private void initToolBar() {

        FirebaseUser user = mAuth.getCurrentUser();
        Glide.with(this).load(user.getPhotoUrl()).into((CircleImageView) findViewById(R.id.profile));

    }

    @Override
    protected void onResume() {
        super.onResume();
       // status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}


package com.technion.android.israelihope;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.technion.android.israelihope.Adapters.UserAdapter;
import com.technion.android.israelihope.Objects.User;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class UsersFragment extends Fragment implements androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> diffusersList;
    private ArrayList<User> allUsersList;
    private androidx.appcompat.widget.SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = getActivity().findViewById(R.id.searchView);
        recyclerView = getView().findViewById(R.id.myUserRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupAnimations();
        initSearchView();
        initSpeechButton();
        getUsersToSearch();
        initRandomUsersRefreshButton();
    }


    private void initSpeechButton() {

        ImageView backButton = getActivity().findViewById(R.id.speechButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, ("ניתן לומר שם של ספר או חבר"));
                startActivityForResult(intent, Utils.RECOGNIZE_SPEECH_REQUEST);
            }
        });
    }

    private void initSearchView() {

        searchView.setIconifiedByDefault(false);

        //remove underline
        View v = getActivity().findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));

        //change text size
        LinearLayout linearLayout1 = (LinearLayout) searchView.getChildAt(0);
        LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
        LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
        autoComplete.setTextSize(14);

        searchView.setOnQueryTextListener(this);
    }

    private void initRandomUsersRefreshButton() {
        getView().findViewById(R.id.refresh).setOnClickListener(view -> initRandomDifferentUsers());
    }

    private void initRandomDifferentUsers() {
        ArrayList<User> randomDifferentUsers = getRandomDifferentUsers();
        if (randomDifferentUsers.size() == 0)
            return;
        User user1 = randomDifferentUsers.get(0);
        //if (user1 == null) return;
        Utils.loadProfileImage(getContext(), getView().findViewById(R.id.profileImage1), user1.getEmail());
        ((TextView) getView().findViewById(R.id.username1)).setText(user1.getUserName());
        getView().findViewById(R.id.profileImage1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage(user1);
            }
        });
        getView().findViewById(R.id.username1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage(user1);
            }
        });
        if (randomDifferentUsers.size() == 1)
            return;
        User user2 = randomDifferentUsers.get(1);
        //if (user2 == null) return;
        Utils.loadProfileImage(getContext(), getView().findViewById(R.id.profileImage2), user2.getEmail());
        ((TextView) getView().findViewById(R.id.username2)).setText(user2.getUserName());
        getView().findViewById(R.id.profileImage2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage(user2);
            }
        });
        getView().findViewById(R.id.username2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage(user2);
            }
        });
        if (randomDifferentUsers.size() == 2)
            return;
        User user3 = randomDifferentUsers.get(2);
        //if (user3 == null) return;
        Utils.loadProfileImage(getContext(), getView().findViewById(R.id.profileImage3), user3.getEmail());
        ((TextView) getView().findViewById(R.id.username3)).setText(user3.getUserName());
        getView().findViewById(R.id.profileImage3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage(user3);
            }
        });
        getView().findViewById(R.id.username3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickImage(user3);
            }
        });

    }
    private void onClickImage(User user){
        Intent intent = new Intent(getContext(), MessageActivity.class);
        intent.putExtra("receiver", user);
        intent.putExtra("sender", ((MainActivity) getContext()).getCurrentUser());
        getContext().startActivity(intent);
    }


    private ArrayList<User> getRandomDifferentUsers() {
        ArrayList<User> randList = new ArrayList<>();
        int num_of_random_results = Math.min(3, diffusersList.size());
        Random rand = new Random();
        int i = 0;
        while (i < num_of_random_results) {
            int randomIndex = rand.nextInt(diffusersList.size());
            User u = diffusersList.get(randomIndex);
            if (!randList.contains(u)) {
                i++;
                randList.add(u);
            }
        }
        return randList;
    }

    private void getUsersToSearch() {

        diffusersList = new ArrayList<>();
        allUsersList = new ArrayList<>();

        Utils.enableDisableBackPressed(false);

        User mUser = ((MainActivity) getActivity()).getCurrentUser();

        Query requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
        requestCollectionRef.get().addOnCompleteListener(task -> {
            diffusersList.clear();
            allUsersList.clear();
            for (QueryDocumentSnapshot document : task.getResult()) {
                User user = document.toObject(User.class);
                if (user.getEmail().equals(mUser.getEmail()))
                    continue;
                if (!user.getType().equals(mUser.getType()))
                    diffusersList.add(user);
                allUsersList.add(user);
            }
            userAdapter = new UserAdapter(getContext(), diffusersList, allUsersList);
            recyclerView.setAdapter(userAdapter);
            initRandomDifferentUsers();
            Utils.enableDisableBackPressed(true);
        });
    }

    @Override
    public boolean onQueryTextSubmit(String string) {
        if (userAdapter == null) return false;

        Filter filter = userAdapter.getFilter();
        filter.filter(string);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String string) {
        if (userAdapter == null) return false;

        Filter filter = userAdapter.getFilter();
        filter.filter(string);
        return true;
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.RECOGNIZE_SPEECH_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchView.setQuery(res.get(0), false);
            }
        }
    }
}

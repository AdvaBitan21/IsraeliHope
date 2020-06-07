package com.technion.android.israelihope;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.technion.android.israelihope.Adapters.UserAdapter;
import com.technion.android.israelihope.Objects.User;

import java.util.ArrayList;
import java.util.Locale;

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
    private User mUser;
    FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private Button b;

    private androidx.appcompat.widget.SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, null);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchView = ((androidx.appcompat.widget.SearchView) getActivity().findViewById(R.id.searchView));
        recyclerView = getView().findViewById(R.id.my_user_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        b = (Button) getActivity().findViewById(R.id.diffrent_user_btn);
        initBackButton();
        initSearchView();
        initSpeechButton();
        getUsersToSearch();

    }

    private void getUsersToSearch() {
        diffusersList = new ArrayList<>();
        allUsersList = new ArrayList<>();

        Query requestQuery = db.collection("Users").whereEqualTo("email", firebaseUser.getEmail());
        requestQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mUser = document.toObject(User.class);
                    }
                    Query requestCollectionRef = FirebaseFirestore.getInstance().collection("Users");
                    requestCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            diffusersList.clear();
                            allUsersList.clear();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                User user = document.toObject(User.class);
                                if (user.getEmail().equals(mUser.getEmail()))
                                    continue;
                                if (!user.getType().equals(mUser.getType()))
                                    diffusersList.add(user);
                                allUsersList.add(user);
                            }
                            userAdapter = new UserAdapter(getContext(), diffusersList, allUsersList, false);
                            recyclerView.setAdapter(userAdapter);
                            recyclerView.setVisibility(View.INVISIBLE);
                            initRandomDifferentUsers();


                            //onQueryTextChange(searchView.getQuery().toString());


                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String string) {
        if (userAdapter == null) return false;
        b.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));

        Filter filter = userAdapter.getFilter();
        recyclerView.setVisibility(View.VISIBLE);
        filter.filter(string);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String string) {


        if (userAdapter == null) return false;
        b.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));

        Filter filter = userAdapter.getFilter();
        recyclerView.setVisibility(View.VISIBLE);
        filter.filter(string);
        return true;
    }

    private void initSearchView() {

        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();

        //remove underline
        View v = getActivity().findViewById(androidx.appcompat.R.id.search_plate);
        v.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));

        //remove search icon
        ImageView searchViewIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ((ViewGroup) searchViewIcon.getParent()).removeView(searchViewIcon);

        searchView.setOnQueryTextListener(this);
    }

    private void initRandomDifferentUsers() {

        //remove underline
        b.setBackgroundColor(getActivity().getResources().getColor(R.color.transparent));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.setBackgroundColor(getActivity().getResources().getColor(R.color.lightGrey));

                userAdapter.searchRandomDifferentUsers();
                recyclerView.setVisibility(View.VISIBLE);
            }
        });


    }


    private void initBackButton() {

        ImageView backButton = getActivity().findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.closeKeyboard(getContext());
                getActivity().onBackPressed();
            }
        });
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
                startActivityForResult(intent, 1000);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchView.setQuery(res.get(0), false);
                }
            }
        }
    }


}

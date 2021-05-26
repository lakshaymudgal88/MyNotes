package com.androexp.mynotes.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androexp.mynotes.R;
import com.androexp.mynotes.noteslist.MyNotesFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignInFragment extends Fragment {

    private static final int SIGN_IN_RQ = 100;
    private GoogleSignInClient signInClient;
    public static final String PREFS = "prefs";
    public static final String STORE_KEY = "Not Sign In";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        SignInButton signInButton = view.findViewById(R.id.sign_in_btn);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        initGoogleSignInApi();

        signInButton.setOnClickListener(view1 -> {

            Intent intent = signInClient.getSignInIntent();
            startActivityForResult(intent, SIGN_IN_RQ);

        });
        return view;
    }

    private void initGoogleSignInApi() {

        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        if (getContext() != null) {

            signInClient = GoogleSignIn.getClient(getContext(), signInOptions);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_RQ) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignIn(accountTask);
        }
    }

    private void handleSignIn(Task<GoogleSignInAccount> accountTask) {
        try {
            GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
            updateUI(signInAccount);
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    private void updateUI(GoogleSignInAccount signInAccount) {

        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(STORE_KEY, signInAccount.getDisplayName());
            editor.apply();
            goToNotesFragment();
        }
    }

    private void goToNotesFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_view, new MyNotesFragment())
                    .commit();
        }
    }
}
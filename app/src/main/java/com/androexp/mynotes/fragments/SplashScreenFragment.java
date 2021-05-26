package com.androexp.mynotes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.androexp.mynotes.R;
import com.androexp.mynotes.noteslist.MyNotesFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.androexp.mynotes.fragments.SignInFragment.PREFS;
import static com.androexp.mynotes.fragments.SignInFragment.STORE_KEY;

public class SplashScreenFragment extends Fragment {

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        new Handler(Looper.getMainLooper())
                .postDelayed(() -> {
                    if (!Objects.requireNonNull(getValue()).equals("Not Sign In")) {
                        if (getFragmentManager() != null) {
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.main_view, new MyNotesFragment())
                                    .commit();
                        }
                    } else {
                        if (getFragmentManager() != null) {
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.main_view, new SignInFragment())
                                    .commit();
                        }

                    }

                }, 2500);
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    private String getValue() {
        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            return preferences.getString(STORE_KEY, "Not Sign In");
        }
        return null;
    }


}
package com.androexp.mynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.androexp.mynotes.fragments.SplashScreenFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_view, new SplashScreenFragment())
                .commit();
    }
}
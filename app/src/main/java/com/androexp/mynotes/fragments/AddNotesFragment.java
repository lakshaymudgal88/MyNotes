package com.androexp.mynotes.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.androexp.mynotes.DbHelper;
import com.androexp.mynotes.R;
import com.androexp.mynotes.noteslist.MyNotesFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.androexp.mynotes.fragments.SignInFragment.PREFS;
import static com.androexp.mynotes.fragments.SignInFragment.STORE_KEY;

public class AddNotesFragment extends Fragment {

    private TextInputEditText etTitle, etDesc;
    private MaterialToolbar toolbar;
    private DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_notes, container, false);

        dbHelper = new DbHelper(getContext());
        initUI(view);
        return view;
    }

    private void initUI(View view) {

        etTitle = view.findViewById(R.id.title_et);
        etDesc = view.findViewById(R.id.desc_et);
        toolbar = view.findViewById(R.id.tool_bar);

        handleToolbar();
    }

    private void handleToolbar() {

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.desc_menu_done) {
                checkValidation();
                return true;
            }
            return false;
        });
    }

    private void checkValidation() {

        StringBuffer title = new StringBuffer(Objects.requireNonNull(etTitle.getText()).toString().trim());
        StringBuffer desc = new StringBuffer(Objects.requireNonNull(etDesc.getText()).toString().trim());
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && title.length() <= 50) {
            insertNote(title.toString(), desc.toString());
        }else if(TextUtils.isEmpty(title)){
            etTitle.setError("Title must not be empty!");
        }else if(title.length() >  50){
            etTitle.setError("Title length must be less than 50!");
        }else {
            etDesc.setError("Note must not be empty!");
        }
    }

    private void insertNote(String title, String desc) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy-hh:mm:ss-a", Locale.getDefault());
        String date = dateFormat.format(new Date());
        boolean isInserted = dbHelper.insertNote(title, desc, date, getUserName());
        if(isInserted){
            Snackbar.make(etDesc, "Note added Successfully", Snackbar.LENGTH_SHORT).show();
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_view, new MyNotesFragment())
                        .commit();
            }
        }else {
            Snackbar.make(etDesc, "Note not added, please try again!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public String getUserName(){
        if(getActivity() != null){
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            return preferences.getString(STORE_KEY, "My Notes");
        }
        return "My Notes";
    }

}
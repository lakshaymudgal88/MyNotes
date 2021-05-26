package com.androexp.mynotes.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.androexp.mynotes.DbHelper;
import com.androexp.mynotes.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.androexp.mynotes.noteslist.MyNotesFragment.NOTE_DESC;
import static com.androexp.mynotes.noteslist.MyNotesFragment.NOTE_TITLE;

public class EditDesFragment extends Fragment {

    private TextInputEditText etTitle, etDesc;
    private DbHelper dbHelper;
    private MaterialToolbar toolbar;
    private String lastTitle, lastDesc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_des, container, false);

        etTitle = view.findViewById(R.id.title_et);
        etDesc = view.findViewById(R.id.desc_et);
        toolbar = view.findViewById(R.id.tool_bar);

        dbHelper = new DbHelper(getContext());

        setUpToolBar();
        getData();
        return view;
    }

    private void getData() {

        if (getArguments() != null) {
            lastTitle = getArguments().getString(NOTE_TITLE);
            lastDesc = getArguments().getString(NOTE_DESC);
            etTitle.setText(lastTitle);
            etDesc.setText(lastDesc);
        }
    }


    private void setUpToolBar() {

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.desc_menu_done) {
                checkValidation();
            }
            return false;
        });
    }

    private void checkValidation() {

        StringBuffer title = new StringBuffer(Objects.requireNonNull(etTitle.getText()).toString().trim());
        StringBuffer desc = new StringBuffer(Objects.requireNonNull(etDesc.getText()).toString().trim());

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc)) {
            updateNote(title.toString(), desc.toString());
        } else if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title must not be empty!");
        } else if (TextUtils.isEmpty(desc)) {
            etDesc.setError("Note must not be empty!");
        } else {
            etTitle.setError("Title must not be empty!");
            etDesc.setError("Note must not be empty!");
        }
    }

    private void updateNote(String title, String desc) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy-hh:mm:ss-a", Locale.getDefault());
        String date = dateFormat.format(new Date());

        Integer isNoteUpdate = dbHelper.updateNote(title, desc, date, lastTitle, lastDesc);
        if (isNoteUpdate > 0) {
            Snackbar.make(etDesc, "Note Updated", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(etDesc, "Note not Updated, please try again!", Snackbar.LENGTH_SHORT).show();
        }
    }
}
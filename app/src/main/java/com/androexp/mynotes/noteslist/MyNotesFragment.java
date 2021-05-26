package com.androexp.mynotes.noteslist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.androexp.mynotes.DbHelper;
import com.androexp.mynotes.NoteClick;
import com.androexp.mynotes.R;
import com.androexp.mynotes.fragments.AddNotesFragment;
import com.androexp.mynotes.fragments.EditDesFragment;
import com.androexp.mynotes.fragments.SignInFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.androexp.mynotes.fragments.SignInFragment.PREFS;
import static com.androexp.mynotes.fragments.SignInFragment.STORE_KEY;

public class MyNotesFragment extends Fragment {

    public static final String NOTE_TITLE = "title";
    public static final String NOTE_DESC = "desc";
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private GoogleSignInClient signInClient;
    private FloatingActionButton addNoteBtn;
    private NoteAdapter adapter;
    private List<NotesModel> notesModelList;
    private DbHelper dbHelper;
    private LottieAnimationView notFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_notes, container, false);

        dbHelper = new DbHelper(getContext());
        initUI(view);
        return view;
    }

    private void initUI(View view) {

        toolbar = view.findViewById(R.id.tool_bar);
        addNoteBtn = view.findViewById(R.id.add_note_btn);
        recyclerView = view.findViewById(R.id.recycle_view);
        notFound = view.findViewById(R.id.not_found);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        setUpToolBar();

        addNoteBtn.setOnClickListener(view1 -> {
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_view, new AddNotesFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        initList();

    }

    @Override
    public void onStart() {
        super.onStart();
        toolbar.setTitle(getUserName());
    }

    private void setUpToolBar() {

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_sign_out) {
                if (getActivity() != null) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Sign Out")
                            .setMessage("Are you sure you want to sign out?")
                            .setPositiveButton("Sign Out", (dialogInterface, i) ->
                                    signOutUser()).setNegativeButton("No",
                            (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                    return true;
                }
            }
            return false;
        });
    }

    private void signOutUser() {

        initGoogleSignInApi();
        signInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "sign out successful", Toast.LENGTH_SHORT).show();
                savePrefs();
            }
        })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "e: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void savePrefs() {
        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(STORE_KEY, "Not Sign In");
            editor.apply();
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_view, new SignInFragment())
                        .commit();
            }
        }

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

    private void initList() {
        notesModelList = new ArrayList<>();

        Cursor cursor = dbHelper.fetchNotes();
        if (cursor.getCount() == 0) {
            if (notFound.getVisibility() == View.GONE) {
                notFound.setVisibility(View.VISIBLE);
            }
        }

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DbHelper.USER_NAME));
            if (name.equals(getUserName())) {
                String title = cursor.getString(cursor.getColumnIndex(DbHelper.NOTE_TITLE));
                String desc = cursor.getString(cursor.getColumnIndex(DbHelper.NOTE_DESC));
                String date = cursor.getString(cursor.getColumnIndex(DbHelper.NOTE_TIME));
                String formattedDate = getFormattedDate(date);
                notesModelList.add(new NotesModel(title, desc, formattedDate));
                adapter = new NoteAdapter(notesModelList, click);
                recyclerView.setAdapter(adapter);
            }
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (addNoteBtn.getVisibility() == View.GONE) {
                        addNoteBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy != 0) {
                    if (addNoteBtn.getVisibility() == View.VISIBLE) {
                        addNoteBtn.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private String getFormattedDate(String date) {
        Date convertedDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy-hh:mm:ss-a", Locale.getDefault());
            convertedDate = dateFormat.parse(date);
            SimpleDateFormat sdfnewformat = new SimpleDateFormat("d MMM hh:mm a", Locale.getDefault());
            String finalDateString;
            if (convertedDate != null) {
                finalDateString = sdfnewformat.format(convertedDate);
                return finalDateString;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private final NoteClick click = new NoteClick() {
        @Override
        public void onNoteClick(String title, String desc) {
            EditDesFragment desFragment = new EditDesFragment();
            Bundle bundle = new Bundle();
            bundle.putString(NOTE_TITLE, title);
            bundle.putString(NOTE_DESC, desc);
            desFragment.setArguments(bundle);
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_view, desFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }

        @Override
        public void onNoteLongClick(int position, String title, String desc) {
            if (getActivity() != null) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Note Delete")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes, Delete", (dialogInterface, i) ->
                                deleteNote(position, title, desc))
                        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
            }
        }
    };

    private void deleteNote(int position, String title, String desc) {
        Integer dltNote = dbHelper.dltNote(title, desc);
        if (dltNote > 0) {
            notesModelList.remove(position);
            adapter.notifyItemRemoved(position);
            Snackbar.make(recyclerView, "Note deleted!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public String getUserName() {
        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            return preferences.getString(STORE_KEY, "My Notes");
        }
        return "My Notes";
    }
}
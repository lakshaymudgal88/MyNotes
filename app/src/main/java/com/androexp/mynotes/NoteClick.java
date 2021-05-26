package com.androexp.mynotes;

public interface NoteClick {

    void onNoteClick(String title, String desc);

    void onNoteLongClick(int position, String title, String desc);
}

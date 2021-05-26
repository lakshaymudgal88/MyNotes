package com.androexp.mynotes.noteslist;

public class NotesModel {

    private final String title, desc, time;

    public String getTime() {
        return time;
    }

    public NotesModel(String title, String desc, String time) {
        this.title = title;
        this.desc = desc;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

}

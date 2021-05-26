package com.androexp.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "note.db";
    private static final String TABLE_NAME = "notes";
    private static final int DB_VERSION = 1;
    public static final String NOTE_TITLE = "NoteTitle";
    public static final String NOTE_DESC = "NoteDesc";
    public static final String NOTE_TIME = "NoteTime";
    public static final String USER_NAME = "UserName";

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " NoteTitle TEXT, NoteDesc TEXT, NoteTime TEXT, UserName TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertNote(String title, String desc, String time, String name) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_TITLE, title);
        values.put(NOTE_DESC, desc);
        values.put(NOTE_TIME, time);
        values.put(USER_NAME, name);

        long isInsert = database.insert(TABLE_NAME, "", values);
        return isInsert != -1;
    }

    public Cursor fetchNotes() {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(" SELECT * FROM " + TABLE_NAME + " ORDER BY " + NOTE_TIME + " DESC", null);
    }

    public Integer dltNote(String title, String desc) {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.delete(TABLE_NAME, NOTE_TITLE + "=? and " + NOTE_DESC + "=?"
                , new String[]{title, desc});
    }

    public Integer updateNote(String title, String desc, String time, String lastTitle, String lastDesc) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_TITLE, title);
        values.put(NOTE_DESC, desc);
        values.put(NOTE_TIME, time);
        return database.update(TABLE_NAME, values, NOTE_TITLE + "=? and " + NOTE_DESC + "=?"
                , new String[]{lastTitle, lastDesc});
    }
}

package com.honman.notepad.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.honman.notepad.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    private NoteDbHelper dbHelper;
    private SQLiteDatabase database;

    public NoteDao(Context context) {
        dbHelper = new NoteDbHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Note createNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteDbHelper.COLUMN_TITLE, note.getTitle());
        values.put(NoteDbHelper.COLUMN_CONTENT, note.getContent());
        values.put(NoteDbHelper.COLUMN_CREATE_TIME, note.getCreateTime());
        values.put(NoteDbHelper.COLUMN_UPDATE_TIME, note.getUpdateTime());

        long insertId = database.insert(NoteDbHelper.TABLE_NOTES, null, values);
        note.setId(insertId);
        return note;
    }

    public Note updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteDbHelper.COLUMN_TITLE, note.getTitle());
        values.put(NoteDbHelper.COLUMN_CONTENT, note.getContent());
        values.put(NoteDbHelper.COLUMN_UPDATE_TIME, System.currentTimeMillis());

        database.update(NoteDbHelper.TABLE_NOTES, values,
                NoteDbHelper.COLUMN_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
        return note;
    }

    public void deleteNote(long id) {
        database.delete(NoteDbHelper.TABLE_NOTES,
                NoteDbHelper.COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    public Note getNote(long id) {
        Cursor cursor = database.query(NoteDbHelper.TABLE_NOTES,
                null,
                NoteDbHelper.COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) },
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Note note = cursorToNote(cursor);
            cursor.close();
            return note;
        }
        return null;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = database.query(NoteDbHelper.TABLE_NOTES,
                null, null, null, null, null,
                NoteDbHelper.COLUMN_UPDATE_TIME + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Note note = cursorToNote(cursor);
                notes.add(note);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_TITLE)));
        note.setContent(cursor.getString(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_CONTENT)));
        note.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_CREATE_TIME)));
        note.setUpdateTime(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbHelper.COLUMN_UPDATE_TIME)));
        return note;
    }
}
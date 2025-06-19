package com.honman.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.honman.notepad.adapter.NoteAdapter;
import com.honman.notepad.db.NoteDao;
import com.honman.notepad.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {
    private NoteAdapter adapter;
    private NoteDao noteDao;
    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.notes_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notes = new ArrayList<>();
        adapter = new NoteAdapter(notes, this);
        recyclerView.setAdapter(adapter);

        noteDao = new NoteDao(this);

        FloatingActionButton fab = findViewById(R.id.fab_add_note);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        noteDao.open();
        List<Note> loadedNotes = noteDao.getAllNotes();
        noteDao.close();
        notes.clear();
        notes.addAll(loadedNotes);
        adapter.updateNotes(notes);
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(this, NoteEditActivity.class);
        intent.putExtra(NoteEditActivity.EXTRA_NOTE_ID, note.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete_all) {
            noteDao.open();
            for (Note note : notes) {
                noteDao.deleteNote(note.getId());
            }
            noteDao.close();
            loadNotes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
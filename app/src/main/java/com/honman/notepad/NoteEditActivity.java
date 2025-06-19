package com.honman.notepad;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.honman.notepad.db.NoteDao;
import com.honman.notepad.model.Note;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AlertDialog;

public class NoteEditActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE_ID = "extra_note_id";
    private EditText titleEditText;
    private EditText contentEditText;
    private NoteDao noteDao;
    private Note currentNote;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        titleEditText = findViewById(R.id.edit_title);
        contentEditText = findViewById(R.id.edit_content);
        noteDao = new NoteDao(this);

        long noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, -1);
        if (noteId != -1) {
            loadNote(noteId);
        } else {
            currentNote = new Note("", "");
        }

        titleEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isChanged = true;
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
        contentEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isChanged = true;
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    private void loadNote(long noteId) {
        noteDao.open();
        currentNote = noteDao.getNote(noteId);
        noteDao.close();

        if (currentNote != null) {
            titleEditText.setText(currentNote.getTitle());
            contentEditText.setText(currentNote.getContent());
        }
    }

    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            finish();
            return;
        }

        currentNote.setTitle(title);
        currentNote.setContent(content);

        noteDao.open();
        if (currentNote.getId() == 0) {
            noteDao.createNote(currentNote);
        } else {
            noteDao.updateNote(currentNote);
        }
        noteDao.close();

        Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isChanged) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(this,
                        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                        .setMessage("有未保存的更改，确定要放弃吗？")
                        .setPositiveButton("放弃", (dialog1, which) -> finish())
                        .setNegativeButton("继续编辑", (dialog1, which) -> dialog1.dismiss())
                        .create();
                dialog.setOnShowListener(d -> {
                    if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(getResources().getColor(R.color.purple_700));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(null, android.graphics.Typeface.BOLD);
                    }
                    if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(getResources().getColor(R.color.purple_700));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(null, android.graphics.Typeface.BOLD);
                    }
                    android.widget.TextView msgView = dialog.findViewById(android.R.id.message);
                    if (msgView != null) {
                        msgView.setTypeface(null, android.graphics.Typeface.BOLD);
                    }
                });
                dialog.show();
            } else {
                finish();
            }
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveNote();
            isChanged = false;
            return true;
        } else if (item.getItemId() == R.id.action_delete && currentNote.getId() != 0) {
            androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(
                    this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                    .setMessage("确定要删除这条笔记吗？")
                    .setPositiveButton("删除", (dialog1, which) -> {
                        noteDao.open();
                        noteDao.deleteNote(currentNote.getId());
                        noteDao.close();
                        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss())
                    .create();
            dialog.setOnShowListener(d -> {
                if (dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE) != null) {
                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(R.color.purple_700));
                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTypeface(null,
                            android.graphics.Typeface.BOLD);
                }
                if (dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE) != null) {
                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(R.color.purple_700));
                    dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTypeface(null,
                            android.graphics.Typeface.BOLD);
                }
                android.widget.TextView msgView = dialog.findViewById(android.R.id.message);
                if (msgView != null) {
                    msgView.setTypeface(null, android.graphics.Typeface.BOLD);
                }
            });
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
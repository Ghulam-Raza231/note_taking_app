package com.notepad.notetaking;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.notepad.notetaking.databinding.ActivityAddNoteBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {
    private ActivityAddNoteBinding binding;
    private Note existingNote;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (getIntent().hasExtra("note")) {
            existingNote = (Note) getIntent().getSerializableExtra("note");
            isEditMode = true;
            binding.etTitle.setText(existingNote.getTitle());
            binding.etContent.setText(existingNote.getContent());
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault());
            binding.tvDateTime.setText(sdf.format(new Date(existingNote.getTimestamp())));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault());
            binding.tvDateTime.setText(sdf.format(new Date()));
        }

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.fabSave.setOnClickListener(v -> saveNote());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEditMode) {
            getMenuInflater().inflate(R.menu.menu_add_note, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showDeleteDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    AppDatabase.getInstance(this).noteDao().delete(existingNote);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveNote() {
        String title = binding.etTitle.getText().toString().trim();
        String content = binding.etContent.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, "Empty note discarded", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (isEditMode) {
            existingNote.setTitle(title);
            existingNote.setContent(content);
            existingNote.setTimestamp(System.currentTimeMillis());
            AppDatabase.getInstance(this).noteDao().update(existingNote);
        } else {
            Note newNote = new Note(title, content, System.currentTimeMillis(), 0);
            AppDatabase.getInstance(this).noteDao().insert(newNote);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newNoteTitle", title);
            setResult(RESULT_OK, resultIntent);
        }

        finish();
    }
}

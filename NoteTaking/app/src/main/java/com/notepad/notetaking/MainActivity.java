package com.notepad.notetaking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.notepad.notetaking.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NoteAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private AppDatabase appDatabase;
    private static final int ADD_NOTE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        appDatabase = AppDatabase.getInstance(this);

        setupRecyclerView();
        setupSearch();

        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivityForResult(intent, ADD_NOTE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK && data != null) {
            String noteTitle = data.getStringExtra("note_title");
            if (noteTitle != null && !noteTitle.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Your task '" + noteTitle + "' is added", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new NoteAdapter(noteList, note -> {
            // Handle note click
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("note", note);
            startActivity(intent);
        }, new NoteAdapter.OnNoteMenuItemClickListener() {
            @Override
            public void onEditClick(Note note) {
                // Handle edit click
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                intent.putExtra("note", note);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Note note) {
                // Handle delete click
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            appDatabase.noteDao().delete(note);
                            loadNotes();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterNotes(String query) {
        List<Note> filteredList = new ArrayList<>();
        for (Note note : noteList) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                note.getContent().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(note);
            }
        }
        adapter.setNotes(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        noteList = appDatabase.noteDao().getAllNotes();
        adapter.setNotes(noteList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

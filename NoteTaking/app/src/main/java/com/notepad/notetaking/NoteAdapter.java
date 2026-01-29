package com.notepad.notetaking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;
    private OnNoteClickListener listener;
    private OnNoteMenuItemClickListener menuListener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public interface OnNoteMenuItemClickListener {
        void onEditClick(Note note);
        void onDeleteClick(Note note);
    }

    public NoteAdapter(List<Note> notes, OnNoteClickListener listener, OnNoteMenuItemClickListener menuListener) {
        this.notes = notes;
        this.listener = listener;
        this.menuListener = menuListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note, listener, menuListener);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvContent, tvDate;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(final Note note, final OnNoteClickListener listener, final OnNoteMenuItemClickListener menuListener) {
            tvTitle.setText(note.getTitle());
            tvContent.setText(note.getContent());
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(note.getTimestamp())));

            itemView.setOnClickListener(v -> listener.onNoteClick(note));

            itemView.setOnLongClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.note_menu);
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        menuListener.onEditClick(note);
                        return true;
                    } else if (item.getItemId() == R.id.action_delete) {
                        menuListener.onDeleteClick(note);
                        return true;
                    }
                    return false;
                });
                popup.show();
                return true;
            });
        }
    }
}

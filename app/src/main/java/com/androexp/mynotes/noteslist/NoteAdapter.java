package com.androexp.mynotes.noteslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androexp.mynotes.NoteClick;
import com.androexp.mynotes.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.VH> {

    private final List<NotesModel> notesModelList;
    private final NoteClick noteClick;

    public NoteAdapter(List<NotesModel> notesModelList, NoteClick noteClick) {
        this.notesModelList = notesModelList;
        this.noteClick = noteClick;
    }

    @NonNull
    @NotNull
    @Override
    public VH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_des, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NoteAdapter.VH holder, int position) {
        NotesModel model = notesModelList.get(position);
        holder.title.setText(model.getTitle());
        holder.desc.setText(model.getDesc());
        holder.time.setText(model.getTime());

        holder.itemView.setOnClickListener(view -> noteClick.onNoteClick(model.getTitle(), model.getDesc()));
        holder.itemView.setOnLongClickListener(view -> {
            noteClick.onNoteLongClick(position, model.getTitle(), model.getDesc());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notesModelList.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        private final TextView title, desc, time;

        public VH(@NonNull @NotNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.note_title);
            desc = itemView.findViewById(R.id.note_msg);
            time = itemView.findViewById(R.id.note_time);
        }
    }
}

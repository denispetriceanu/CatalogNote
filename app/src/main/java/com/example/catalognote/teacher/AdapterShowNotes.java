package com.example.catalognote.teacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalognote.Models.ModelToAddNotes;
import com.example.catalognote.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterShowNotes extends RecyclerView.Adapter<AdapterShowNotes.MyViewHolder> {
    ArrayList<ModelToAddNotes> list;
    Context context;

    public AdapterShowNotes(Context context, ArrayList<ModelToAddNotes> notes) {
        this.context = context;
        this.list = notes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.note_show_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String OLD_FORMAT = "dd/MM/yyyy";
        Timestamp stamp = new Timestamp(Long.parseLong(list.get(position).getDate()));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        sdf.applyPattern(OLD_FORMAT);
        String dataString = sdf.format(date);
        String textToShow = "Nota " + list.get(position).getNote() + " din " + dataString;
        holder.textView.setText(textToShow);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView11);
        }
    }
}

package com.example.catalognote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalognote.Models.Message;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterShowMessage extends RecyclerView.Adapter<AdapterShowMessage.ViewHolder> {
    Context context;
    boolean isTeacher;
    private ArrayList<Message> list;

    public AdapterShowMessage(Context context, ArrayList<Message> list, boolean isTeacher) {
        this.context = context;
        this.list = list;
        this.isTeacher = isTeacher;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (isTeacher) {
            if (list.get(position).getIsTeacher().equals("false")) {
                holder.person.setText("Elev");
                holder.person.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                holder.message.setText(list.get(position).getMessage());
            } else {
                holder.person.setText("Tu");
                holder.person.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                holder.message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                holder.message.setText(list.get(position).getMessage());
            }
        } else {
            if (list.get(position).getIsTeacher().equals("true")) {
                holder.person.setText("Profesor");
                holder.person.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                holder.message.setText(list.get(position).getMessage());
            } else {
                holder.person.setText("Tu");
                holder.person.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                holder.message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                holder.message.setText(list.get(position).getMessage());
            }
        }

        final String OLD_FORMAT = "dd/MM/yyyy";
        Timestamp stamp = new Timestamp(Long.parseLong(list.get(position).getDate()));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        sdf.applyPattern(OLD_FORMAT);
        String dataString = sdf.format(date);
        holder.time.setText(dataString);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView person, time, message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            person = itemView.findViewById(R.id.textView14);
            message = itemView.findViewById(R.id.textView13);
            time = itemView.findViewById(R.id.textView12);

        }
    }
}

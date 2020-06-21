package com.example.catalognote.teacher.classWork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalognote.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterListStudents extends RecyclerView.Adapter<AdapterListStudents.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<String> list;
    private ClickListener itemClickListener;

    public AdapterListStudents(Context context, ArrayList<String> list_students, ClickListener itemClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.list = list_students;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_class_teacher, parent, false);
        return new ViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(list.get(position));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                holder.textView.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ClickListener itemClickListener;
        ImageView image;

        public ViewHolder(@NonNull View itemView, ClickListener itemClickListener) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView2);
            image.setImageResource(R.drawable.ic_person_black_24dp);

            textView = itemView.findViewById(R.id.textView3);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }
}

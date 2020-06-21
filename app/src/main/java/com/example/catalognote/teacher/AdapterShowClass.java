package com.example.catalognote.teacher;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.catalognote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterShowClass extends RecyclerView.Adapter<AdapterShowClass.MyViewHolder> {
    private ArrayList<String> id_class;
    private LayoutInflater inflater;
    private Context context;
    private ItemClickListener itemClickListener;
    private String typeUser;

    public AdapterShowClass(Context context, ArrayList<String> id_class, ItemClickListener itemClickListener, String typeUser) {
        this.id_class = id_class;
        this.itemClickListener = itemClickListener;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.typeUser = typeUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_class_teacher, parent, false);
        return new MyViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final String id = id_class.get(position);
        if (typeUser.equals("teacher")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_class")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(id);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String txtToShow = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString() + " - " +
                            Objects.requireNonNull(dataSnapshot.child("mat").getValue()).toString();
                    holder.textView.setText(txtToShow);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_class");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String teacher_id = "";
                    for (DataSnapshot teacher : dataSnapshot.getChildren()) {
                        for (DataSnapshot class_teacher : teacher.getChildren()) {
                            if (Objects.equals(class_teacher.getKey(), id)) {
                                System.out.println("we are here");
                                String txtToShow = Objects.requireNonNull(class_teacher.child("info").getValue()).toString() + " " +
                                        Objects.requireNonNull(class_teacher.child("mat").getValue()).toString();
                                holder.textView.setText(txtToShow);
                                teacher_id = teacher.getKey();
                            }
                        }
                    }
                    assert teacher_id != null;
                    if (!teacher_id.equals("")) {
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("list_class")
                                .child(teacher_id).child(id);
                        reference1.child("list_students").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("images/" + id).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Glide.with(context)
                                    .load(uri)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(holder.image);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return id_class.size();
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView textView;
        ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView2);
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

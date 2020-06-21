package com.example.catalognote.teacher.classWork;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalognote.R;
import com.example.catalognote.teacher.AddNotes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ShowStudents extends AppCompatActivity implements AdapterListStudents.ClickListener {
    AdapterListStudents.ClickListener clickListener;
    ArrayList<String> listStudents;
    String id_class;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id_class = getIntent().getStringExtra("id_class");

        Objects.requireNonNull(getSupportActionBar()).setTitle("Listă elevi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303030")));
        setContentView(R.layout.activity_show_students);
        clickListener = this;
        listStudents = new ArrayList<>();
        getStudents();
    }

    private void getStudents() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("list_class")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(id_class).child("list_students");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot student : dataSnapshot.getChildren()) {
                    listStudents.add(student.getKey());
                }
                if (listStudents.size() != 0) {
                    sendToAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendToAdapter() {
        RecyclerView recyclerView = findViewById(R.id.showStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterListStudents adapterShowClass = new AdapterListStudents(getApplicationContext(), listStudents, clickListener);
        recyclerView.setAdapter(adapterShowClass);
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(ShowStudents.this, AddNotes.class)
                .putExtra("id_class", id_class).putExtra("id_student", listStudents.get(position)));
    }
}

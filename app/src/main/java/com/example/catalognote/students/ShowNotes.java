package com.example.catalognote.students;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalognote.ChatActivity;
import com.example.catalognote.Models.ModelToAddNotes;
import com.example.catalognote.R;
import com.example.catalognote.teacher.AdapterShowNotes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ShowNotes extends AppCompatActivity {
    TextView showNotesView;
    String id_class;
    ArrayList<ModelToAddNotes> listNotes;
    AdapterShowNotes adapterShowClass;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_notes_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miCompose) {
            startActivity(new Intent(ShowNotes.this, ChatActivity.class)
                    .putExtra("id_class", id_class)
                    .putExtra("id_student", FirebaseAuth.getInstance().getUid())
                    .putExtra("isTeacher", "false"));
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shote_notes);

        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Vizualizează notele");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303030")));

        id_class = getIntent().getStringExtra("cod");
        
        listNotes = new ArrayList<>();

        showNotesView = findViewById(R.id.textView8);

        getNotesListFirebase();
    }

    private void getNotesListFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("note")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child(id_class);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());
                for (DataSnapshot note : dataSnapshot.getChildren()) {
                    System.out.println(note.toString());
                    listNotes.add(new ModelToAddNotes(Objects.requireNonNull(note.child("nota").getValue()).toString(),
                            Objects.requireNonNull(note.child("data").getValue()).toString()));
                }
                if (listNotes.size() != 0)
                    showNotesView.setVisibility(View.GONE);
                addToAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    private void addToAdapter() {
        RecyclerView recyclerNotes = findViewById(R.id.viewNotesStudent);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        adapterShowClass = new AdapterShowNotes(getApplicationContext(), listNotes);
        recyclerNotes.setAdapter(adapterShowClass);
        adapterShowClass.notifyDataSetChanged();
    }
}

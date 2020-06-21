package com.example.catalognote.teacher;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalognote.ChatActivity;
import com.example.catalognote.Models.ModelToAddNotes;
import com.example.catalognote.R;
import com.example.catalognote.students.ShowNotes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Objects;

public class AddNotes extends AppCompatActivity {
    TextView textView;
    Button addNote;
    EditText note;
    AdapterShowNotes adapterShowClass;
    String id_class, id_student;
    ArrayList<ModelToAddNotes> listNotes;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_notes_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.miCompose) {
            startActivity(new Intent(AddNotes.this, ChatActivity.class)
                    .putExtra("id_class", id_class)
                    .putExtra("id_student", id_student)
                    .putExtra("isTeacher", "true"));
        } else {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);

        getSupportActionBar().setTitle("Catalog individual");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303030")));

        id_class = getIntent().getStringExtra("id_class");
        id_student = getIntent().getStringExtra("id_student");
        // check if the parameters exists
        if (id_class == null || id_student == null)
            onBackPressed();

        listNotes = new ArrayList<>();
        getNotesListFirebase();
        addNote = findViewById(R.id.button3);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (note.getText().length() > 1 || note.getText().length() < 3) {
                    // ToDo: add note
                    addNoteInFirebase(note.getText().toString());
                } else {
                    FancyToast.makeText(getApplicationContext(), "Nota nu este validă!", Toast.LENGTH_SHORT
                            , FancyToast.ERROR, false).show();
                }
            }
        });
        textView = findViewById(R.id.textView10);
        note = findViewById(R.id.editText);
    }

    private void getNotesListFirebase() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("note").child(id_student).child(id_class);
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
                    textView.setVisibility(View.GONE);
                addToAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }


    private void addToAdapter() {
        RecyclerView recyclerNotes = findViewById(R.id.viewNotes);
        recyclerNotes.setLayoutManager(new LinearLayoutManager(this));
        adapterShowClass = new AdapterShowNotes(getApplicationContext(), listNotes);
        recyclerNotes.setAdapter(adapterShowClass);
        adapterShowClass.notifyDataSetChanged();
    }

    private void addNoteInFirebase(String note) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("note").child(id_student).child(id_class).push();
        reference.child("nota").setValue(note);
        reference.child("data").setValue(System.currentTimeMillis());
        listNotes.add(new ModelToAddNotes(note, String.valueOf(System.currentTimeMillis())));
        adapterShowClass.notifyDataSetChanged();
        textView.setVisibility(View.GONE);
        FancyToast.makeText(getApplicationContext(), "Nota adăugată cu succes!", Toast.LENGTH_SHORT
                , FancyToast.SUCCESS, false).show();
    }


}

package com.example.catalognote;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalognote.Models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    String id_class, id_student, isTeacher;
    Button send;
    EditText message;
    RecyclerView viewMessage;
    ArrayList<Message> messagesList = new ArrayList<>();
    AdapterShowMessage adapterShowMessage;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        id_class = getIntent().getStringExtra("id_class");
        id_student = getIntent().getStringExtra("id_student");
        isTeacher = getIntent().getStringExtra("isTeacher");
        if (id_student == null || id_class == null || isTeacher == null) {
            onBackPressed();
        }

        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Lasă un comentariu");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303030")));

        send = findViewById(R.id.button4);
        message = findViewById(R.id.editText2);

        viewMessage = findViewById(R.id.chat);
        getMessage();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToFirebase();
            }
        });
    }

    private void getMessage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("message")
                .child(id_student).child(id_class);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot message : dataSnapshot.getChildren()) {
                    Message message2 = new Message(
                            Objects.requireNonNull(message.child("message").getValue()).toString(),
                            Objects.requireNonNull(message.child("date").getValue()).toString(),
                            Objects.requireNonNull(message.child("isTeacher").getValue()).toString());
                    messagesList.add(message2);
                }
                callAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void callAdapter() {
        viewMessage.setLayoutManager(new LinearLayoutManager(this));
        adapterShowMessage = new AdapterShowMessage(getApplicationContext(), messagesList, Boolean.parseBoolean(isTeacher));
        viewMessage.setAdapter(adapterShowMessage);
        adapterShowMessage.notifyDataSetChanged();
    }

    private void saveToFirebase() {
        if (message.getText().length() > 0) {
            Message newMessage = new Message(message.getText().toString(),
                    String.valueOf(System.currentTimeMillis()),
                    isTeacher);
            FirebaseDatabase.getInstance().getReference().child("message")
                    .child(id_student).child(id_class).push().setValue(newMessage);
            messagesList.add(newMessage);
            adapterShowMessage.notifyDataSetChanged();
            viewMessage.scrollToPosition(messagesList.size()-1);
            message.setText("");
        } else {
            FancyToast.makeText(getApplicationContext(), "Mesajul este gol", Toast.LENGTH_LONG
                    , FancyToast.ERROR, false).show();
        }
    }
}

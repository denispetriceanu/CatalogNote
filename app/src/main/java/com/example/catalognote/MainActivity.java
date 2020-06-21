package com.example.catalognote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.catalognote.log.FirstActivity;
import com.example.catalognote.log.Login;
import com.example.catalognote.students.ListClassForStudents;
import com.example.catalognote.teacher.ListOfClassTeacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseUser mAuth;
    private TextView home;
    private Button homeButton;
    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, FirstActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // if is not login, go to login else keep here
        if (mAuth == null) {
            startActivity(new Intent(MainActivity.this, FirstActivity.class));
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                    .child(mAuth.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (Objects.requireNonNull(dataSnapshot.child("typeUser").getValue()).toString().equals("teacher")) {
                        FancyToast.makeText(getApplicationContext(), "Acesta este un cont de profesor!", Toast.LENGTH_LONG, FancyToast.INFO,
                                false).show();
                        homeButton.setVisibility(View.VISIBLE);
                        homeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(MainActivity.this, ListOfClassTeacher.class));
                            }
                        });
                    } else {
                        FancyToast.makeText(getApplicationContext(), "Acesta este un cont de elev!", Toast.LENGTH_LONG, FancyToast.INFO,
                                false).show();
                        homeButton.setVisibility(View.VISIBLE);
                        homeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(MainActivity.this, ListClassForStudents.class));
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    FancyToast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG, FancyToast.ERROR,
                            false).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();

        getSupportActionBar().setTitle("ACASĂ");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303030")));

        homeButton = findViewById(R.id.homeButton);

        homeButton.setVisibility(View.GONE);

        home = findViewById(R.id.home_message);
        if (mAuth != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    home.setText("Bună ziua, " + dataSnapshot.child("name").getValue() + "!");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println(databaseError.getMessage());
                }
            });
        }
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                FancyToast.makeText(getApplicationContext(), "Te-ai delogat cu succes", Toast.LENGTH_SHORT, FancyToast.CONFUSING, false).show();
                startActivity(new Intent(MainActivity.this, Login.class));
                home.setText("Nu ești conectat");
            }
        });
    }
}

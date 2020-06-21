package com.example.catalognote.students;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.catalognote.R;
import com.example.catalognote.log.Login;
import com.example.catalognote.teacher.AdapterShowClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Objects;

public class ListClassForStudents extends AppCompatActivity implements AdapterShowClass.ItemClickListener {
    private ArrayList<String> listClass, id_class;
    private String cod_class;
    private EditText nameClass;
    private AdapterShowClass.ItemClickListener itemClickListener;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_class_for_students);

        itemClickListener = this;

        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303030")));
        actionBar.setTitle("Vezi materiile tale");

        if (FirebaseAuth.getInstance().getUid() == null) {
            startActivity(new Intent(ListClassForStudents.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

        FloatingActionButton addClass = findViewById(R.id.addClassStudents);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClassByProf(view);
            }
        });

        listClass = new ArrayList<>();
        id_class = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_class_students")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot myClass : dataSnapshot.getChildren()) {
                    listClass.add(Objects.requireNonNull(myClass.getValue()).toString());
                    id_class.add(myClass.getKey());
                }
                if (listClass.size() != 0) {
                    TextView txt = findViewById(R.id.ifDoNotHaveClassStudents);
                    txt.setVisibility(View.GONE);
                    sendToAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error List of class students: " + databaseError.getMessage());
            }
        });
    }

    private void sendToAdapter() {
        RecyclerView recyclerView = findViewById(R.id.showClassStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterShowClass adapterShowClass = new AdapterShowClass(getApplicationContext(), listClass, itemClickListener, "students");
        recyclerView.setAdapter(adapterShowClass);
    }

    private void addClassByProf(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popView = inflater.inflate(R.layout.popup_add_materie, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popView, width, height, true);

        Button cancel, add;
        nameClass = popView.findViewById(R.id.codMaterie);
        cancel = popView.findViewById(R.id.cancelMaterie);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                assert clipBoard != null;
                ClipData clipData = clipBoard.getPrimaryClip();
                assert clipData != null;
                ClipData.Item item = clipData.getItemAt(0);
                String text = item.getText().toString();
                nameClass.setText(text);
            }
        });
        add = popView.findViewById(R.id.addMaterie);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cod_class = nameClass.getText().toString();
                if (cod_class.length() != 0) {
                    popupWindow.dismiss();
                    addClassInFirebase();
                }
            }
        });

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void addClassInFirebase() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_class_students")
                .child(uid).push();
        reference.setValue(cod_class);
        FancyToast.makeText(getApplicationContext(), "Ai accesat o nouă materie, felicitări", Toast.LENGTH_LONG
                , FancyToast.SUCCESS, false).show();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(ListClassForStudents.this, ShowNotes.class)
                .putExtra("cod", listClass.get(position)));
    }
}

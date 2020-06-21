package com.example.catalognote.teacher;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import com.example.catalognote.Models.ClassOfStudent;
import com.example.catalognote.R;
import com.example.catalognote.log.Login;
import com.example.catalognote.teacher.classWork.EditInfoClass;
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

public class ListOfClassTeacher extends AppCompatActivity implements AdapterShowClass.ItemClickListener {
    private ArrayList<String> listClass;
    private String name, mat, info;
    private EditText nameClass, nameMat, anotherInfo;
    private AdapterShowClass.ItemClickListener itemClickListener;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_class_teacher);

        itemClickListener = this;
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303030")));
        actionBar.setTitle("Vezi clasele");

        if (FirebaseAuth.getInstance().getUid() == null) {
            startActivity(new Intent(ListOfClassTeacher.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }

        FloatingActionButton addClass = findViewById(R.id.addHour);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addClassByProf(view);
            }
        });

        listClass = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_class")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot myClass : dataSnapshot.getChildren()) {
                    listClass.add(myClass.getKey());
                }
                if (listClass.size() != 0) {
                    TextView txt = findViewById(R.id.ifDoNotHaveClass);
                    txt.setVisibility(View.GONE);
                }
                sendToAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error List of class teacher: " + databaseError.getMessage());
            }
        });
    }

    private void sendToAdapter() {
        RecyclerView recyclerView = findViewById(R.id.showClass);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterShowClass adapterShowClass = new AdapterShowClass(getApplicationContext(), listClass, itemClickListener, "teacher");
        recyclerView.setAdapter(adapterShowClass);
    }

    private void addClassByProf(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popView = inflater.inflate(R.layout.popup_add_class, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

        final PopupWindow popupWindow = new PopupWindow(popView, width, height, true);

        Button cancel, add;
        nameClass = popView.findViewById(R.id.className);
        nameMat = popView.findViewById(R.id.nameMateria);
        anotherInfo = popView.findViewById(R.id.moreInfo);
        cancel = popView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        add = popView.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkData()) {
                    popupWindow.dismiss();
                    name = nameClass.getText().toString();
                    mat = nameMat.getText().toString();
                    info = anotherInfo.getText().toString();
                    addClassInFirebase();
                }
            }
        });

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    private void addClassInFirebase() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("list_class")
                .child(uid).push();
        reference.setValue(new ClassOfStudent(name, mat, info, uid));
        FancyToast.makeText(getApplicationContext(), "Class add with success", Toast.LENGTH_LONG
                , FancyToast.SUCCESS, false).show();
        finish();
        startActivity(getIntent());
    }

    private boolean checkData() {
        if (nameClass.getText().length() == 0) {
            FancyToast.makeText(getApplicationContext(), "Nu ai adăugat numele clasei", Toast.LENGTH_LONG
                    , FancyToast.ERROR, false).show();
            return false;
        } else if (nameMat.getText().length() == 0) {
            FancyToast.makeText(getApplicationContext(), "Nu ai adăugat numele materiei", Toast.LENGTH_LONG
                    , FancyToast.ERROR, false).show();
            return false;
        } else if (anotherInfo.getText().length() == 0) {
            FancyToast.makeText(getApplicationContext(), "Nu ai adăugat informații suplimentare", Toast.LENGTH_LONG
                    , FancyToast.ERROR, false).show();
            return false;
        }
        return true;
    }

    @Override
    public void onItemClick(int position) {
        startActivity(new Intent(ListOfClassTeacher.this, EditInfoClass.class).putExtra("id_class", listClass.get(position)));
    }
}

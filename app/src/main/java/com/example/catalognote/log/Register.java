package com.example.catalognote.log;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.catalognote.Models.User;
import com.example.catalognote.R;
import com.example.catalognote.Undefined.UsefulFunction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

public class Register extends AppCompatActivity {
    EditText name, email, age, password;
    Switch check_teacher;
    Button reg;
    LinearLayout goToLogin;
    CheckBox show_pass;
    String userType;
    boolean varCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        name = findViewById(R.id.email_log);
        email = findViewById(R.id.pass_log);
        age = findViewById(R.id.age_reg);
        check_teacher = findViewById(R.id.check_teacher);
        check_teacher.setChecked(false);
        userType = "student";
        reg = findViewById(R.id.btn_login);
        password = findViewById(R.id.passwordReg);
        password.setText("");
        show_pass = findViewById(R.id.show_pass_reg);
        show_pass.setChecked(false);
        show_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(null);
                    password.setSelection(password.getText().length());
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    password.setSelection(password.getText().length());
                }
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check_if_all_completed()) {
                    checkExistAccountEmail();
                }
            }
        });

        goToLogin = findViewById(R.id.go_login);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        // we get the typeUser;
        check_teacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                System.out.println("Am fost apelat, sunt elev" + b);
                if (!b) {
                    System.out.println("Am fost apelat, sunt elev" + b);
                    userType = "student";
                } else {
                    System.out.println("Am fost apelat, sunt profesor" + b);
                    userType = "teacher";
                }
            }
        });

    }

    private boolean check_if_all_completed() {
        if (name.getText().toString().equals("")) {
            FancyToast.makeText(getApplicationContext(), "Completează numele", Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
            return false;
        } else if (email.getText().toString().equals("")) {
            FancyToast.makeText(getApplicationContext(), "Completează emailul", Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
            return false;
        } else if (!UsefulFunction.isEmailValid(email.getText().toString())) {
            FancyToast.makeText(getApplicationContext(), "Email invalid", Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
            return false;
        } else if (password.getText().toString() != "") {
            System.out.println("Parola 1: " + password.getText().toString());
            if (password.getText().toString().length() < 6) {
                FancyToast.makeText(getApplicationContext(),
                        "Parola aleasă este prea scurtă sau nu a fost completată",
                        Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
                return false;
            }
        }
        if (age.getText().toString().length() == 0) {
            FancyToast.makeText(getApplicationContext(), "Completează vârsta", Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
            return false;
        } else {
            if (Integer.parseInt(age.getText().toString()) < 0 ||
                    Integer.parseInt(age.getText().toString()) > 99) {
                FancyToast.makeText(getApplicationContext(), "Vârsta incorectă", Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
                return false;
            }
        }
        return true;
    }

    private void checkExistAccountEmail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    if (users.child("email").getValue().toString().equals(email.getText().toString())) {
                        varCheck = true;
                    }
                }
                if (varCheck) {
                    FancyToast.makeText(getApplicationContext(), "Un cont cu acest email există deja", Toast.LENGTH_LONG,
                            FancyToast.ERROR, false).show();
                } else {
                    registerUser();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void registerUser() {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FancyToast.makeText(getApplicationContext(), "Cont creat cu succes", Toast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                            if (userType.equals("teacher"))
                                addInfoAboutUser(mAuth.getUid(), "teacher");
                            else
                                addInfoAboutUser(mAuth.getUid(), "student");
                            startActivity(new Intent(getApplication(), Login.class));
                        } else {
                            FancyToast.makeText(getApplicationContext(), "Ceva nu a mers bine: " + task.getResult().toString(),
                                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
                        }
                    }
                });
    }

    private void addInfoAboutUser(String user_uid, String userType) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(user_uid).setValue(new User(userType, email.getText().toString(),
                name.getText().toString(), Integer.parseInt(age.getText().toString())));
        // firebase after make register user login automatically the user;
        FirebaseAuth.getInstance().signOut();
    }

}

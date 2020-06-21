package com.example.catalognote.log;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.catalognote.MainActivity;
import com.example.catalognote.R;
import com.example.catalognote.Undefined.UsefulFunction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.Objects;

public class Login extends AppCompatActivity {
    TextView goToReg;
    Button login;
    EditText user, pass;
    Switch show_pass, showPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    @Override
    protected void onDestroy() {
        System.out.println("Call login onDestroy ----");
        super.onDestroy();
    }

    private void init() {
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInformation())
                    login();
            }
        });

        user = findViewById(R.id.email_log);
        pass = findViewById(R.id.pass_log);

        showPass = findViewById(R.id.switch_keep_log);
        showPass.setChecked(false);
        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    pass.setTransformationMethod(null);
                    pass.setInputType(InputType.TYPE_CLASS_TEXT);
                    pass.setSelection(pass.getText().length());
                } else {
                    pass.setTransformationMethod(new PasswordTransformationMethod());
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pass.setSelection(pass.getText().length());
                }
            }
        });

        goToReg = findViewById(R.id.btn_goto_reg);
        goToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    private boolean checkInformation() {
        if (!user.getText().toString().equals("")) {
            if (!UsefulFunction.isEmailValid(user.getText().toString())) {
                FancyToast.makeText(getApplicationContext(), "Emailul nu este valid",
                        Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
                return false;
            }
        } else if (user.getText().toString().equals("") || user.getText().toString().length() == 0) {
            FancyToast.makeText(getApplicationContext(), "Emailul nu a fost completat",
                    Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
            return false;
        }

        if (pass.getText().toString().equals("") || pass.getText().toString().length() == 0 || pass.getText() == null) {
            FancyToast.makeText(getApplicationContext(), "Parola nu a fost completată",
                    Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
            return false;
        } else if (pass.getText().toString().length() < 6) {
            FancyToast.makeText(getApplicationContext(), "Parola este prea scurtă",
                    Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
            return false;
        }
        return true;

    }

    private void login() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(user.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            System.out.println("Call loginFunctionListenerFirebase ----");
                            startActivity(new Intent(Login.this, MainActivity.class));
                            System.out.println("After star MainActivity from login ----");
                        } else
                            FancyToast.makeText(getApplicationContext(), "Eroare: " + Objects.requireNonNull(task.getException().getMessage()),
                                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    }
                });
    }
}

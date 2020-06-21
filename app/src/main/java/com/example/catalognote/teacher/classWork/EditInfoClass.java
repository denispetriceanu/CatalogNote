package com.example.catalognote.teacher.classWork;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.catalognote.Models.ClassOfStudent;
import com.example.catalognote.R;
import com.example.catalognote.teacher.ListOfClassTeacher;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.IOException;
import java.util.Objects;

public class EditInfoClass extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 22;
    Button edit, delete, getCod, showStudents;
    EditText nameClass, nameMat, info;
    String id_class;
    String id_teacher;
    Uri filePath;
    ImageView image;
    private boolean modificate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_class);
        id_class = getIntent().getStringExtra("id_class");
        init();
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle("Informații despre clasa");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#303030")));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setEditableInput(boolean isEditable) {
        if (!isEditable) {
            nameClass.setTag(nameClass.getKeyListener());
            nameClass.setKeyListener(null);
            nameMat.setTag(nameMat.getKeyListener());
            nameMat.setKeyListener(null);
            info.setTag(info.getKeyListener());
            info.setKeyListener(null);
        } else {
            nameClass.setKeyListener((KeyListener) nameClass.getTag());
            nameMat.setKeyListener((KeyListener) nameMat.getTag());
            info.setKeyListener((KeyListener) info.getTag());
        }
    }

    private void getInfoAboutClass() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("list_class")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(id_class);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.toString());
                nameClass.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                nameMat.setText(Objects.requireNonNull(dataSnapshot.child("mat").getValue()).toString());
                info.setText(Objects.requireNonNull(dataSnapshot.child("info").getValue()).toString());
                id_teacher = Objects.requireNonNull(dataSnapshot.child("teacher").getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error Edit info class: " + databaseError.getMessage());
            }
        });
    }

    private void init() {
        image = findViewById(R.id.imageView3);
        nameClass = findViewById(R.id.nameClassAEIC);
        nameMat = findViewById(R.id.nameMatAEIC);
        info = findViewById(R.id.detailsAEIC);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        getImage();
        setEditableInput(false);

        edit = findViewById(R.id.btnEditInfoAEIC);
        edit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (modificate) {
                    setEditableInput(true);
                    edit.setText("Salvează modificările");
                    modificate = false;
                } else {
                    setEditableInput(false);
                    edit.setText("Modifică");
                    modificate = true;
                    if (checkData())
                        saveData();
                }
            }
        });

        getCod = findViewById(R.id.codAccessClass);
        getCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("cod", id_class);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);

                FancyToast.makeText(getApplicationContext(), "Cod copiat",
                        Toast.LENGTH_LONG, FancyToast.INFO, false).show();
            }
        });

        delete = findViewById(R.id.deleteClass);
        // ToDo: make a query if user is sure to delete this class
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("list_class")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(id_class).removeValue();
                FancyToast.makeText(getApplicationContext(), "Această clasă a fost ștearsă",
                        Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
                startActivity(new Intent(EditInfoClass.this, ListOfClassTeacher.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        showStudents = findViewById(R.id.listStudents);
        showStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditInfoClass.this, ShowStudents.class).putExtra("id_class", id_class));
            }
        });
        getInfoAboutClass();
    }

    private void getImage() {
        StorageReference storageRef =
                FirebaseStorage.getInstance().getReference();
//        storageRef.child("images/" + id_class).getDownloadUrl();
        storageRef.child("images/" + id_class).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (uri != null) {
                            Glide.with(getApplicationContext())
                                    .load(uri)
//                                    .apply(RequestOptions.circleCropTransform())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(image);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private boolean checkData() {
        if (nameClass.getText().toString().length() == 0) {
            FancyToast.makeText(getApplicationContext(), "Numele clasei nu a fost completat",
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (nameMat.getText().toString().length() == 0) {
            FancyToast.makeText(getApplicationContext(), "Numele materiei nu a fost completat",
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        } else if (info.getText().toString().length() == 0) {
            FancyToast.makeText(getApplicationContext(), "Detaliile nu a fost adăugate",
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
            return false;
        }

        return true;
    }

    private void saveData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("list_class")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(id_class);
        reference.setValue(new ClassOfStudent(nameClass.getText().toString(), nameMat.getText().toString(),
                info.getText().toString(), id_teacher));

        FancyToast.makeText(getApplicationContext(), "Datele au fost modificate cu succes",
                Toast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
    }

    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                image.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref = FirebaseStorage.getInstance().getReference()
                    .child("images/" + id_class);
            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(
                        UploadTask.TaskSnapshot taskSnapshot) {

                    // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast.makeText(EditInfoClass.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(EditInfoClass.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {
                        // Progress Listener for loading
                        // percentage on the dialog box
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

}

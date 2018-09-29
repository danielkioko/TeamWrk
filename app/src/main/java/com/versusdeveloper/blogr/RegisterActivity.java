package com.versusdeveloper.blogr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.versusdeveloper.blogr.Accounts.MainActivity;
import com.versusdeveloper.blogr.Accounts.RegisterTab;
import com.versusdeveloper.blogr.Accounts.User;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    EditText userName, email, password;
    Button Register;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDBref;
    private ProgressDialog mDialog;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userName = findViewById(R.id.etName);
        email = findViewById(R.id.etNewEmail);
        password = findViewById(R.id.etNewPassword);
        Register = findViewById(R.id.btnRegister);

        imageView = findViewById(R.id.btnChooseImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                if(name.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                }else if(userEmail.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                }else if(userPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    signUpUserWithFirebase(name, userEmail, userPassword);
                }

            }
        });

    }

    private void signUpUserWithFirebase(final String name, String userEmail, String userPassword) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    final FirebaseUser newUser = task.getResult().getUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    newUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(RegisterTab.class.getName(), "User profile updated.");
                                        /***CREATE USER IN FIREBASE DB AND REDIRECT ON SUCCESS**/
                                        createUserInDb(newUser.getUid(), newUser.getDisplayName(), newUser.getEmail());
                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void createUserInDb(String uid, String displayName, String email) {

        if(filePath != null)
        {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }

        String image = storageReference.getPath();

        mUsersDBref = FirebaseDatabase.getInstance().getReference().child("Users");
        User user = new User(uid, displayName, email, image);
        mUsersDBref.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else{

                }
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Photo"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null ) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

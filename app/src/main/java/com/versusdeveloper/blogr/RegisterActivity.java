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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.versusdeveloper.blogr.Accounts.User;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private ImageView imageView;
    private static final int GALLERY_REQUEST_CODE = 2;


    Uri uri = null;

    EditText userName, email, password;
    Button Register;

    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDBref;
    DatabaseReference databaseRef;
    private ProgressDialog mDialog;

    private FirebaseUser mCurrentUser;

    StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance().getReference();

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCurrentUser = mAuth.getCurrentUser();
        mUsersDBref = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

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
                                        Log.d(RegisterActivity.class.getName(), "User profile updated.");
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

    private void createUserInDb(String uid, String displayName, final String email) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference filepath = storage.child("userFiles").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {

                        final Uri downloadUrl = uri;
                        Toast.makeText(getApplicationContext(), "Succesfully Uploaded", Toast.LENGTH_SHORT).show();
                        final DatabaseReference newUser = databaseRef.push();

                            mUsersDBref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    newUser.child("profilePhoto").setValue(downloadUrl.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            signIn();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    });

                }
            });

        mUsersDBref = FirebaseDatabase.getInstance().getReference().child("Users");
        User user = new User(uid, displayName, email);
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

    private void signIn() {

        String emailAdress = email.getText().toString().trim();
        String accPassword = password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(emailAdress, accPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                startActivity(new Intent(RegisterActivity.this, Tabs.class));
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Photo"), GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //image from gallery result
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            imageView.setImageURI(uri);
        }
    }
}

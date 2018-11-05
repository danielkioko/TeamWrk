package com.versusdeveloper.blogr.ImagePosts;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.versusdeveloper.blogr.R;
import com.versusdeveloper.blogr.Tabs;

public class NewImagePost extends AppCompatActivity {

    ImageView postImage;
    EditText topic, caption;
    Button post;

    private static final int GALLERY_REQUEST_CODE = 2;
    private StorageReference storage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseRef;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_image_post);

        postImage = findViewById(R.id.postImage);
        topic = findViewById(R.id.postTitle);
        caption = findViewById(R.id.postCaption);
        post = findViewById(R.id.btnPost);

        storage = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("BlogPosts");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(NewImagePost.this);
                builder.setMessage("Uploading...");
                builder.create().show();

                //Toast.makeText(PostActivity.this, "POSTING…", Toast.LENGTH_LONG).show();
                final String PostTitle = topic.getText().toString().trim();
                final String PostDesc = caption.getText().toString().trim();

                if(!PostDesc.isEmpty() && !PostTitle.isEmpty()){
                    final StorageReference filepath = storage.child("post_images").child(uri.getLastPathSegment());
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    final Uri downloadUrl = uri;
                                    Toast.makeText(getApplicationContext(), "Succesfully Uploaded", Toast.LENGTH_SHORT).show();
                                    final DatabaseReference newPost = databaseRef.push();

                                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            newPost.child("title").setValue(PostTitle);
                                            newPost.child("desc").setValue(PostDesc);
                                            newPost.child("imageUrl").setValue(downloadUrl.toString());
                                            newPost.child("uid").setValue(mCurrentUser.getUid());
                                            newPost.child("username").setValue(dataSnapshot.child("name").getValue())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Intent intent = new Intent(NewImagePost.this, Tabs.class);
                                                                startActivity(intent);
                                                            } else {
                                                            }
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
                } else {
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //image from gallery result
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            uri = data.getData();
            postImage.setImageURI(uri);
        }

    }
}

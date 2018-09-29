package com.versusdeveloper.blogr.ImagePosts;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.versusdeveloper.blogr.R;
import com.versusdeveloper.blogr.Tabs;

public class ViewPostActivity extends AppCompatActivity {

    String post_key = null;

    ImageView singleImage;
    TextView singleTitle, singleDesc;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    FloatingActionButton backFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        backFab = findViewById(R.id.backFromPostBtn);
        backFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(ViewPostActivity.this, Tabs.class);
                startActivity(back);
                finish();
            }
        });

        singleImage = findViewById(R.id.postImage);
        singleTitle = findViewById(R.id.postTitle);
        singleDesc = findViewById(R.id.postDescription);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("BlogPosts");
        mAuth = FirebaseAuth.getInstance();
        post_key = getIntent().getExtras().getString("PostID");

        mDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("imageUrl").getValue();
                singleTitle.setText(post_title);
                singleDesc.setText(post_desc);
                Picasso.with(ViewPostActivity.this).load(post_image).into(singleImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}

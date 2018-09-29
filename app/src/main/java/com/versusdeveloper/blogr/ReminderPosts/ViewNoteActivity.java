package com.versusdeveloper.blogr.ReminderPosts;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.versusdeveloper.blogr.R;

public class ViewNoteActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference fNotesDatabase;
    TextView noteTitle, notes;
    String post_key = null;
    FloatingActionButton backFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        backFab = findViewById(R.id.backFromNoteBtn);
        backFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(firebaseAuth.getCurrentUser().getUid());
        }

        noteTitle = findViewById(R.id.tvTitle);
        notes = findViewById(R.id.tvNotes);

        loadData();

    }

    private void loadData() {

        fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes");

        post_key = getIntent().getExtras().getString("noteId");
        fNotesDatabase.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("title")&&dataSnapshot.hasChild("content")&&dataSnapshot.hasChild("timestamp")) {

                    String title = dataSnapshot.child("title").getValue().toString();
                    String content = dataSnapshot.child("content").getValue().toString();
                    noteTitle.setText(title);
                    notes.setText(content);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}

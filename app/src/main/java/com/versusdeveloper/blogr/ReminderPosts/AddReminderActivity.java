package com.versusdeveloper.blogr.ReminderPosts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.versusdeveloper.blogr.R;
import com.versusdeveloper.blogr.Tabs;

import java.util.HashMap;
import java.util.Map;

public class AddReminderActivity extends AppCompatActivity {

    private Button btnCreate;
    private EditText etTitle, etContent;

    private FirebaseAuth fAuth;
    private DatabaseReference fNotesDatabase;

    private String noteID;

    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        try {
            noteID = getIntent().getStringExtra("noteId");

            //Toast.makeText(this, noteID, Toast.LENGTH_SHORT).show();

            if (!noteID.trim().equals("")) {
                isExist = true;
            } else {
                isExist = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        btnCreate = findViewById(R.id.btnPostReminder);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_notes);

        fAuth = FirebaseAuth.getInstance();
        fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(content)) {
                    createNote(title, content);
                } else {
                    Snackbar.make(view, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        putData();

    }

    private void putData() {
        if (isExist) {
            fNotesDatabase.child(noteID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String content = dataSnapshot.child("content").getValue().toString();

                        etTitle.setText(title);
                        etContent.setText(content);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void createNote(String title, String content) {
        if (fAuth.getCurrentUser() != null) {

            if (isExist) {
                // UPDATE A NOTE
                Map updateMap = new HashMap();
                updateMap.put("title", etTitle.getText().toString().trim());
                updateMap.put("content", etContent.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);


                fNotesDatabase.child(noteID).updateChildren(updateMap);
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            } else {
                // CREATE A NEW NOTE
                final DatabaseReference newNoteRef = fNotesDatabase.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("content", content);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(AddReminderActivity.this, Tabs.class);
                                    startActivity(intent);
                                    Toast.makeText(AddReminderActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddReminderActivity.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
                mainThread.start();
            }



        } else {
            Toast.makeText(this, "USERS IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
        }
    }

}

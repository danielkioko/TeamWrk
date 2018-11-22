package com.versusdeveloper.blogr.ReminderPosts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.versusdeveloper.blogr.Accounts.MainActivity;
import com.versusdeveloper.blogr.ProfileActivity;
import com.versusdeveloper.blogr.R;
import com.versusdeveloper.blogr.VidyoActivity;

public class ReminderTab extends Fragment {

    private FirebaseAuth fAuth;
    private RecyclerView mNotesList;
    private DatabaseReference fNotesDatabase;

    private FloatingActionButton video, resume;
    private ImageView profile;
    private TextView name, email;
    private View view;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reminder_posts, container, false);

        view = rootView.findViewById(R.id.reminderLayout);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        profile = rootView.findViewById(R.id.mainProfileImage);
        name = rootView.findViewById(R.id.mainUsername);
        email = rootView.findViewById(R.id.mainEmail);

        video = rootView.findViewById(R.id.fabVideo);
        resume = rootView.findViewById(R.id.fabResume);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        profile.setImageURI(Uri.parse(FirebaseDatabase.getInstance()
                .getReference().child("Users")
                .child(uid).child("images")
                .toString()));

        profile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setMessage("Sign Out?");
                alert.setPositiveButton("Proceed to Log out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fAuth.signOut();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();

                return true;
            }
        });

        name.setText(userName);
        email.setText(userEmail);

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent blog = new Intent(getActivity(), VidyoActivity.class);
                startActivity(blog);
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reminders = new Intent(getActivity(), ProfileActivity.class);
                startActivity(reminders);
            }
        });
//
//        Snackbar.make(view, "Welcome " + userName, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();

        mNotesList = rootView.findViewById(R.id.rv_reminders);
        mNotesList.setLayoutManager(mLayoutManager);
//        mNotesList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());
        }

        updateUI();
        loadData();

        return rootView;
    }

    private void loadData() {
        Query query = fNotesDatabase.orderByValue();
        FirebaseRecyclerAdapter<Reminder, ReminderViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Reminder, ReminderViewHolder>(
                Reminder.class,
                R.layout.single_card,
                ReminderViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final ReminderViewHolder viewHolder, Reminder model, int position) {
                final String noteId = getRef(position).getKey();

                fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild("title")&&dataSnapshot.hasChild("content")) {

                            String title = dataSnapshot.child("title").getValue().toString();
                            String content = dataSnapshot.child("content").getValue().toString();
                            String timestamp = dataSnapshot.child("timestamp").getValue().toString();

                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            viewHolder.setReminderTime(getTimeAgo.getTimeAgo(Long.parseLong(timestamp), getContext()));
                            viewHolder.setReminderTitle(title);
                            viewHolder.setReminderNotes(content);

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent singleActivity = new Intent(getActivity(), ViewNoteActivity.class);
                                    singleActivity.putExtra("noteId", noteId);
                                    startActivity(singleActivity);
                                }
                            });

                            viewHolder.mView.setLongClickable(true);
                            viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {

                                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

                                    final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                    alert.setMessage("Delete Reminder?");
                                    alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    AlertDialog alertDialog = alert.create();
                                    alertDialog.show();

                                    return true;
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        };
        mNotesList.setAdapter(firebaseRecyclerAdapter);
    }

    private void updateUI() {
        if (fAuth.getCurrentUser() != null){
            Log.i("ReminderTab", "fAuth != null");
        } else {
            Intent startIntent = new Intent(getActivity(), ReminderTab.class);
            startActivity(startIntent);
            Log.i("ReminderTab", "fAuth == null");
            getActivity().finish();
        }
    }

}

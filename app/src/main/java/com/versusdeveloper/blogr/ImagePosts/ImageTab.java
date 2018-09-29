package com.versusdeveloper.blogr.ImagePosts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.versusdeveloper.blogr.Accounts.AccountTabs;
import com.versusdeveloper.blogr.R;

public class ImageTab extends Fragment {

    private RecyclerView recyclerView;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    SwipeRefreshLayout swipeRefreshLayout;
    Vibrator vibrator;
    View view;


    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.image_posts, container, false);

//        fab = rootView.findViewById(R.id.material_design_floating_action_menu_item2);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), NewImagePost.class);
//                startActivity(intent);
//            }
//        });

        recyclerView = rootView.findViewById(R.id.imageRV);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.isSmoothScrollbarEnabled();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("BlogPosts");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser()==null){
                    Intent loginIntent = new Intent(getActivity(), AccountTabs.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };

        recyclerView.setLayoutManager(mLayoutManager);
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Blogzone, BlogzoneViewHolder> FBRA = new FirebaseRecyclerAdapter<Blogzone, BlogzoneViewHolder>(
                Blogzone.class,
                R.layout.image_item_card,
                BlogzoneViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final BlogzoneViewHolder viewHolder, Blogzone model, int position) {
                final String post_key = getRef(position).getKey().toString();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImageUrl(getActivity(), model.getImageUrl());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleActivity = new Intent(getActivity(), ViewPostActivity.class);
                        singleActivity.putExtra("PostID", post_key);
                        startActivity(singleActivity);
                    }
                });
            }
        };

        recyclerView.setAdapter(FBRA);
        return rootView;
    }

}

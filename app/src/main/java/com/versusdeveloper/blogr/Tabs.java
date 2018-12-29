package com.versusdeveloper.blogr;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.versusdeveloper.blogr.ImagePosts.ImageTab;
import com.versusdeveloper.blogr.ImagePosts.NewImagePost;
import com.versusdeveloper.blogr.ReminderPosts.AddReminderActivity;
import com.versusdeveloper.blogr.ReminderPosts.ReminderTab;

public class Tabs extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    int pinkish = Color.parseColor("#ea8685");
    int maroonish = Color.parseColor("#c44569");
    int lynxWhite = Color.parseColor("#f5f6fa");

    com.github.clans.fab.FloatingActionButton note, post;

    Color color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        note = findViewById(R.id.newNote);
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddReminderActivity.class);
                startActivity(intent);
            }
        });

        post = findViewById(R.id.newPost);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewImagePost.class);
                startActivity(intent);
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.setPageTransformer(false, new FadePageTransformer());
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        tabLayout.setTabTextColors(lynxWhite, lynxWhite);



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(getApplicationContext(), NewImagePost.class);
//                startActivity(intent);
//
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });

//        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
//        fab2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getApplicationContext(), AddReminderActivity.class);
//                startActivity(intent);
//
//            }
//        });

    }
//
//    private static class FadePageTransformer implements ViewPager.PageTransformer {
//        public void transformPage(View view, float position) {
//            view.setAlpha(1 - Math.abs(position));
//            if (position < 0) {
//                view.setScrollX((int)((float)(view.getWidth()) * position));
//            } else if (position > 0) {
//                view.setScrollX(-(int) ((float) (view.getWidth()) * -position));
//            } else {
//                view.setScrollX(0);
//            }
//        }
//    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Previous placeholder fragment containing a simple view is deleted!
     */


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){

                case 0 : ReminderTab reminderTab = new ReminderTab();
                    return reminderTab;

                case 1 : ImageTab imageTab = new ImageTab();
                return imageTab;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}

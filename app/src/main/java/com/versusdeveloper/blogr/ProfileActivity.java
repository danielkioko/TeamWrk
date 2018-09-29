package com.versusdeveloper.blogr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    TextView uName, uEmail;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uName = findViewById(R.id.pName);
        uEmail = findViewById(R.id.pEmail);

        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        uName.setText(username);
        uEmail.setText(email);



    }
}

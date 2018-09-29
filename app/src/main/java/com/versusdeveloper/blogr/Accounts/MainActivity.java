package com.versusdeveloper.blogr.Accounts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.versusdeveloper.blogr.R;
import com.versusdeveloper.blogr.RegisterActivity;
import com.versusdeveloper.blogr.Tabs;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button login, createAccount;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);

        createAccount = findViewById(R.id.btnCreateAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        login = findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                String userPassword = password.getText().toString().trim();

                if (userEmail.isEmpty()){
                    Toast.makeText(MainActivity.this, "No Email?", Toast.LENGTH_LONG).show();
                }else if (userPassword.isEmpty()){
                    Toast.makeText(MainActivity.this, "Password?!", Toast.LENGTH_LONG).show();
                }else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Signing In...");
                    builder.setCancelable(true);
                    builder.create();
                    builder.show();
                    logInUsers(userEmail, userPassword);
                }
            }
        });
    }

    private void logInUsers(String userEmail, String userPassword) {
        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent toClass = new Intent(MainActivity.this, Tabs.class);
                    startActivity(toClass);
                    toClass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    toClass.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}

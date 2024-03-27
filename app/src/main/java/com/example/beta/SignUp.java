package com.example.beta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            startActivity(new Intent(SignUp.this, Dashboard.class));

        }
    }

    public void register(View view){
        EditText emailEditText = findViewById(R.id.edittext_email);
        EditText passwordEditText = findViewById(R.id.edittext_password);
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());
        if(TextUtils.isEmpty(email)){
            Toast.makeText(SignUp.this, "Email is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(SignUp.this, "Password is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(
                        emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(SignUp.this, Dashboard.class));
                        } else {
                            Toast.makeText(SignUp.this, "register failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }


    public void login(View view) {
        EditText emailEditText = findViewById(R.id.edittext_email);
        EditText passwordEditText = findViewById(R.id.edittext_password);
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());
        if(TextUtils.isEmpty(email)){
            Toast.makeText(SignUp.this, "Email is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(SignUp.this, "Password is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(SignUp.this, Dashboard.class));
                        } else {
                            Toast.makeText(SignUp.this, "login failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
}
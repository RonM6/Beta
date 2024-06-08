package com.example.beta;

import static com.example.beta.DBref.refUsers;

import android.content.Intent;
import android.content.SharedPreferences;
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
        SharedPreferences sp = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        String fid = sp.getString("fid", "-1");

        if (currentUser != null && !fid.equals("-1")) {
            DBref.fid = fid;
            startActivity(new Intent(SignUp.this, MainActivity.class));
            finish();
        } else if (currentUser != null) {
            startActivity(new Intent(SignUp.this, FamilySetUp.class));
            finish();
        }
    }

    public void register(View view) {
        EditText emailEditText = findViewById(R.id.edittext_fid);
        EditText passwordEditText = findViewById(R.id.edittext_password);
        EditText nameEditText = findViewById(R.id.edittext_name);
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());
        String name = String.valueOf(nameEditText.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignUp.this, "Email is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignUp.this, "Password is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(SignUp.this, "Name is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(
                        emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String fName = null;
                            String uid = user.getUid();
                            User user1 = new User(uid, name, fName);
                            refUsers.child(uid).setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Family node updated successfully
                                        Intent intent = new Intent(SignUp.this, FamilySetUp.class)
                                                .putExtra("name", name);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignUp.this, "Failed to create family", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SignUp.this, "registration failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public void ahu(View view) {
        startActivity(new Intent(SignUp.this, LogIn.class));
    }


}
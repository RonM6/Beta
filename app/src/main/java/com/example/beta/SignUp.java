package com.example.beta;

import static com.example.beta.DBref.refFamilies;
import static com.example.beta.DBref.refUsers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.FirebaseDatabase;

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
        EditText emailEditText = findViewById(R.id.edittext_fid);
        EditText passwordEditText = findViewById(R.id.edittext_password);
        EditText nameEditText = findViewById(R.id.edittext_name);
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());
        String name = String.valueOf(nameEditText.getText());

        if(TextUtils.isEmpty(email)){
            Toast.makeText(SignUp.this, "Email is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(SignUp.this, "Password is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name)){
            Toast.makeText(SignUp.this, "Name is empty",Toast.LENGTH_SHORT).show();
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
                                                Intent intent = new Intent(SignUp.this, Welcome.class)
                                                        .putExtra("name", name);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(SignUp.this, "Failed to create family", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
//                            Family family = new Family(fName);
//                            family.addUser(uid);
//                            FirebaseDatabase.getInstance("https://beta-52e80-default-rtdb.europe-west1.firebasedatabase.app").getReference("Families")
//                                    .child(fName).setValue(family);

                        } else {
                            Toast.makeText(SignUp.this, "register failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    public void ahu(View view) {
        startActivity(new Intent(SignUp.this, LogIn.class));
    }


}
package com.example.beta;

import static com.example.beta.DBref.refFamilies;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

    }

    public void login(View view) {
        EditText emailEditText = findViewById(R.id.edittext_fid);
        EditText passwordEditText = findViewById(R.id.edittext_password);
        String email = String.valueOf(emailEditText.getText());
        String password = String.valueOf(passwordEditText.getText());
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LogIn.this, "Email is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(LogIn.this, "Password is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            DatabaseReference currentUserRef = refUsers.child(uid);
                            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    // Check if the snapshot exists
                                    if (dataSnapshot.exists()) {
                                        // Get the user object
                                        User user = dataSnapshot.getValue(User.class);
                                        // Access the User fields
                                        if(user.getFamily()!= null){
                                            SharedPreferences temp = getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                            SharedPreferences.Editor editor=temp.edit();
                                            editor.putString("fid", user.getFamily());
                                            editor.commit();
                                            DBref.fid = user.getFamily();
                                            Intent intent = new Intent(LogIn.this, Dashboard.class)
                                                    .putExtra("fName", user.getFamily());
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle potential errors here
                                }

                            });

                        } else {
                            Toast.makeText(LogIn.this, "login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
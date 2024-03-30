package com.example.beta;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Welcome extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            startActivity(new Intent(Welcome.this, Dashboard.class));

        }
    }

    public void createFamily(View view) {
        EditText name = findViewById(R.id.edittext_fid);
        String fName = name.getText().toString();

        if(TextUtils.isEmpty(fName)){
            Toast.makeText(Welcome.this, "Name is empty",Toast.LENGTH_SHORT).show();
            return;
        }

        Family family = new Family(fName);
        FirebaseDatabase.getInstance("https://beta-52e80-default-rtdb.europe-west1.firebasedatabase.app").getReference("Families").child(fName)
                .setValue(family).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Welcome.this, "Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Welcome.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        Intent intent = new Intent(Welcome.this, SignUp.class)
                .putExtra("fName", fName);
        startActivity(intent);
        finish();
    }

    public void joinFamily(View view) {

        startActivity(new Intent(Welcome.this, SignUp.class));
        finish();
    }
}
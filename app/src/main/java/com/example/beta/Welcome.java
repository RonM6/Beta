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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null){
//            startActivity(new Intent(Welcome.this, Dashboard.class));
//
//        }
//    }

    public void createFamily(View view) {
        EditText editText = findViewById(R.id.edittext_fid);
        String fName = editText.getText().toString();

        if(TextUtils.isEmpty(fName)){
            Toast.makeText(Welcome.this, "Name is empty",Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser fbUser = mAuth.getCurrentUser();
        String uid = fbUser.getUid();
        String fid = uid.substring(0, 5);
        String name = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            name = bundle.getString("name");
        }
        User user = new User(uid, name, fid);
        refUsers.child(uid).setValue(user);


        Family family = new Family(fName, fid);
        family.addUser(uid);
        SharedPreferences temp = getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=temp.edit();
        editor.putString("fid", fid);
        editor.commit();


        refFamilies.child(fid).setValue(family).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        return;
                    }
                });
        Intent intent = new Intent(Welcome.this, Dashboard.class)
                .putExtra("fid", family.getFid());
        startActivity(intent);
        finish();
    }

    public void joinFamily(View view) {
        EditText editText = findViewById(R.id.edittext_fid);
        String fid = editText.getText().toString();
        if(TextUtils.isEmpty(fid)){
            Toast.makeText(Welcome.this, "Family ID is empty",Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        DatabaseReference currentFamilyRef = refFamilies.child(fid);
        currentFamilyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Toast.makeText(Welcome.this, "Family ID incorrect",Toast.LENGTH_SHORT).show();
                    return;
                }
                Family family = snapshot.getValue(Family.class);
                family.addUser(uid);
                refFamilies.child(fid).setValue(family);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Assuming you have a DatabaseReference object for the user's data
        DatabaseReference currentUserRef = refUsers.child(uid);

        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the snapshot exists
                if (dataSnapshot.exists()) {
                    // Get the user object
                    User user = dataSnapshot.getValue(User.class);
                    // Access the User fields
                    user.setFamily(fid);
                    refUsers.child(uid).setValue(user);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }

        });


        SharedPreferences temp = getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=temp.edit();
        editor.putString("fid", fid);
        editor.commit();

        Intent intent = new Intent(Welcome.this, Dashboard.class)
                .putExtra("fName", fid);
        startActivity(intent);
        finish();
    }
}

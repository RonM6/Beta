package com.example.beta;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.example.beta.DBref.mAuth;
import static com.example.beta.DBref.refChores;
import static com.example.beta.DBref.refFamilies;
import static com.example.beta.DBref.refUsers;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    ImageView uploadImage;
    ImageView dishesIV, laundryIV, vacuumIV, trashIV;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<User> dataList;
    List<String> stringList;
    List<String> userList;
    MyUserAdapter adapter;
    Button saveButton;
    EditText uploadTopic, uploadDesc;
    String imageURL;
    Uri uri;
    String fid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);



        SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        fid = settings.getString("fid", "-1");
        recyclerView = findViewById(R.id.recyclerView1);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(UploadActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        dataList = new ArrayList<>();
        stringList = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new MyUserAdapter(UploadActivity.this, dataList);
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();


        eventListener = refFamilies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                stringList = snapshot.child(fid).getValue(Family.class).getUsers();

                eventListener = refUsers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        for (DataSnapshot item : snap.getChildren()){
                            User user = item.getValue(User.class);
                            if (stringList.contains(user.getUid())) {
                                dataList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        uploadImage = findViewById(R.id.uploadImage);
        uploadDesc = findViewById(R.id.uploadDesc);
        uploadTopic = findViewById(R.id.uploadTopic);
        saveButton = findViewById(R.id.saveButton);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    public void addChoreToUser(String uid){
        userList.add(uid);
        if (userList.contains(uid)){
            Toast.makeText(this, "Added ", Toast.LENGTH_SHORT).show();
        }
    }
    public void removeChoreToUser(String uid){
        userList.remove(uid);
        if (!userList.contains(uid)){
            Toast.makeText(this, "Removed ", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveData(){

        StorageReference storageReference = FirebaseStorage.getInstance("gs://beta-52e80.appspot.com").getReference().child("Android Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData(){

        String title = uploadTopic.getText().toString();
        String desc = uploadDesc.getText().toString();
        String creator = mAuth.getUid();
        String cid = String.valueOf(System.currentTimeMillis());

        Chore chore = new Chore(title, desc, imageURL, creator, "a", cid);

        //We are changing the child from title to currentDate,
        // because we will be updating title as well and it may affect child value.

        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        refUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult();
                    for (DataSnapshot item : snapshot.getChildren()){
                        User user = item.getValue(User.class);
                        if (userList.contains(user.getUid())){
                            user.addChore(cid);
                            refUsers.child(user.getUid()).setValue(user);
                            userList.remove(user.getUid());
                        }
                        if (user.getUid().toString().equals(mAuth.getUid().toString())){
                            user.addMyChore(cid);
                            refUsers.child(user.getUid()).setValue(user);
                        }
                    }
                }
            }

        });

        refChores.child(fid).child(currentDate)
                .setValue(chore).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Dashboard.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void dishes(View view) {
        String packageName = getPackageName();

        int resourceId = R.drawable.washing_dishes;

        uri = Uri.parse("android.resource://" + packageName + "/" + resourceId);
        uploadTopic.setText("Dishes");
        uploadDesc.setText("Wash the dishes");

    }

    public void laundry(View view) {
        String packageName = getPackageName();

        int resourceId = R.drawable.laundry_machine;

        uri = Uri.parse("android.resource://" + packageName + "/" + resourceId);
        uploadTopic.setText("Laundry");
        uploadDesc.setText("Fold the laundry");

    }

    public void vacuum(View view) {
        String packageName = getPackageName();

        int resourceId = R.drawable.vacuum;

        uri = Uri.parse("android.resource://" + packageName + "/" + resourceId);
        uploadTopic.setText("Vacuum");
        uploadDesc.setText("Vacuum the floor");
    }

    public void trash(View view) {
        String packageName = getPackageName();

        int resourceId = R.drawable.trash;

        uri = Uri.parse("android.resource://" + packageName + "/" + resourceId);
        uploadTopic.setText("Trash");
        uploadDesc.setText("Take out the trash");
    }
}
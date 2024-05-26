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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UploadActivity extends AppCompatActivity {

    ImageView uploadImage;
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
    int minute, hour = 0;
    LocalDate date = LocalDate.now();
    DayOfWeek day;
    String sdate;
    Button timePick;
    private AlertDialog.Builder alertDialog;
    private String currentPath;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }



        SharedPreferences sp = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        fid = sp.getString("fid", "-1");
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
        timePick = findViewById(R.id.timePick);

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
                alertDialog = new AlertDialog.Builder(UploadActivity.this);
                alertDialog.setMessage("Take a picture or upload from gallery");
                alertDialog.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String filename = "tempfile";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        try {
                            File imgFile = File.createTempFile(filename,".jpg",storageDir);
                            currentPath = imgFile.getAbsolutePath();
                            uri = FileProvider.getUriForFile(UploadActivity.this,"com.example.beta.fileprovider",imgFile);
                            Intent takePicIntent = new Intent();
                            takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePicIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,uri);
                            if (takePicIntent.resolveActivity(getPackageManager()) != null) {
                                Toast.makeText(UploadActivity.this," create temporary file",Toast.LENGTH_LONG);
                                startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE );
                            }
                        } catch (IOException e) {
                            Toast.makeText(UploadActivity.this,"Failed to create temporary file",Toast.LENGTH_LONG);
                            throw new RuntimeException(e);
                        }
                    }
                });
                alertDialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent photoPicker = new Intent(Intent.ACTION_PICK);
                        photoPicker.setType("image/*");
                        activityResultLauncher.launch(photoPicker);
                    }
                });
                AlertDialog ad = alertDialog.create();
                ad.show();
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
    }
    public void removeChoreToUser(String uid){
        userList.remove(uid);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data_back) {
        super.onActivityResult(requestCode, resultCode, data_back);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPath);
            uploadImage.setImageBitmap(imageBitmap);
        }

    }

    public void saveData(){
        String title = uploadTopic.getText().toString();
        if (title.isEmpty()){
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (uri == null){
            Toast.makeText(this, "Upload Image or choose from provided", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sdate == null){
            Toast.makeText(this, "Select Due Date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (timePick.getText().toString().equals("Pick Due")){
            Toast.makeText(this, "Select Due Time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userList.isEmpty()){
            Toast.makeText(this, "Add Users", Toast.LENGTH_SHORT).show();
            return;
        }

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
        sdate = date.format(formatter);
        chore.setDueDate(sdate);
        chore.setDueTime(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        chore.setStatus("a");


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
                    }
                }
            }

        });

        refChores.child(fid).child(cid).setValue(chore).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

    public void timePick() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                timePick.setText(sdate+" "+day+" "+String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select due Time");
        timePickerDialog.show();
    }

    public void showDatePicker(View view) {
        int year = date.getYear();
        int month = date.getMonthValue() - 1; // Months are zero-based in DatePickerDialog
        int dayOfMonth = date.getDayOfMonth();

        // Create a DatePickerDialog and set the current date as default
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Update selectedDate with the chosen date
                date = LocalDate.of(year, month + 1, dayOfMonth); // Month is zero-based
                day = date.getDayOfWeek();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
                sdate = date.format(formatter);

                timePick();
            }
        }, year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
}
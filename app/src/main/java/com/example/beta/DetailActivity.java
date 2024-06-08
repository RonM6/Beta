package com.example.beta;

import static com.example.beta.DBref.mAuth;
import static com.example.beta.DBref.refChores;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    TextView detailDesc, detailTitle, due;
    ImageView detailImage;
    FloatingActionButton deleteButton;
    String cid = "";
    String imageUrl = "";
    String fid;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        SharedPreferences sp = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        fid = sp.getString("fid", "-1");

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        due = findViewById(R.id.tvDue);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            String dueTime = bundle.getString("dueTime");
            String dueDate = bundle.getString("dueDate");
            status = bundle.getString("status");
            cid = bundle.getString("cid");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
            String dueString = formatDueDate(dueDate, dueTime);
            due.setText(dueString);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference reference = refChores.child(fid);
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://beta-52e80.appspot.com");

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                if (status.equals("d")) {
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            reference.child(cid).removeValue();
                            Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
                if (status.equals("a")) {
                    reference.child(cid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                DataSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    Chore chore = snapshot.getValue(Chore.class);
                                    chore.setWhoEnded(mAuth.getUid());
                                    chore.setStatus("d");
                                    reference.child(cid).setValue(chore).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Chore completed", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private String formatDueDate(String dueDate, String dueTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMyy HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        String formattedDate = "";
        try {
            Date date = inputFormat.parse(dueDate + " " + dueTime);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            formattedDate = "Invalid date";
        }

        return "The chore is due on " + formattedDate;
    }
}
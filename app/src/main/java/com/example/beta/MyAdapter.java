package com.example.beta;

import static com.example.beta.AlarmHelper.cancelAlarm;
import static com.example.beta.DBref.fid;
import static com.example.beta.DBref.mAuth;
import static com.example.beta.DBref.refChores;
import static com.example.beta.DBref.refUsers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<Chore> dataList;

    public MyAdapter(Context context, List<Chore> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getTitle());
        holder.recDesc.setText(dataList.get(position).getDescription());

        Chore currentChore = dataList.get(holder.getAdapterPosition());


        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy HH:mm");

        try {
            // Combine the due date and time into a single string
            String dueDateTimeString = currentChore.getDueDate() + " " + currentChore.getDueTime();
            // Parse the combined string into a Date object
            Date dueDateTime = dateFormat.parse(dueDateTimeString);
            // Get the current date and time
            Date currentDateTime = new Date();

            // Compare current date and time with the due date and time
            if (currentDateTime.after(dueDateTime)) {
                // Due date and time are in the past
                cancelAlarm(context, currentChore.getCid());
            } else {
                // Due date and time are in the future
                if (currentChore.getStatus().equals("a")) {
                    if (!AlarmHelper.isAlarmSet(context, currentChore.getCid())) {
                        AlarmHelper.setAlarm(context, currentChore.getCid(), currentChore.getDueDate(), currentChore.getDueTime(), currentChore.getTitle(), currentChore.getDescription());
                    }
                } else if (currentChore.getStatus().equals("d")) {
                    cancelAlarm(context, currentChore.getCid());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parsing error, perhaps with a log message or default behavior
        }


        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", currentChore.getDataImage());
                intent.putExtra("Description", currentChore.getDescription());
                intent.putExtra("Title", currentChore.getTitle());
                intent.putExtra("cid", currentChore.getCid());
                intent.putExtra("dueTime", currentChore.getDueTime());
                intent.putExtra("dueDate", currentChore.getDueDate());
                intent.putExtra("status", currentChore.getStatus().toString());
                context.startActivity(intent);
            }
        });

        holder.recCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = refChores.child(DBref.fid);
                reference.child(currentChore.getCid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            DataSnapshot snapshot = task.getResult();
                            if (snapshot.exists()){
                                Chore chore = snapshot.getValue(Chore.class);
                                if(chore != null){
                                    if (chore.getStatus().equals("d")) {
                                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://beta-52e80.appspot.com");

                                        StorageReference StstorageReference = storage.getReferenceFromUrl(chore.getDataImage());
                                        StstorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                reference.child(currentChore.getCid()).removeValue();
                                                refUsers.child(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                                        User user = dataSnapshot.getValue(User.class);
                                                        List<String> choreList = user.getChores();
                                                        choreList.remove(chore.getCid());
                                                        refUsers.child(mAuth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(context, "Chore deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    if(chore.getStatus().equals("a")){
                                        cancelAlarm(context, chore.getCid());
                                        chore.setWhoEnded(mAuth.getUid());
                                        chore.setStatus("d");
                                        reference.child(currentChore.getCid()).setValue(chore).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                refUsers.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            User user = snapshot.getValue(User.class);
                                                            if (user.getChores().contains(chore.getCid())){
                                                                user.getChores().remove(chore.getCid());
                                                                refUsers.child(user.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(context, "Chore completed", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            } else {
                                Log.e("MyAdapter", "Snapshot does not exist for key: " + currentChore.getCid());
                            }
                        } else {
                            Log.e("MyAdapter", "Failed to get chore: ", task.getException());
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage, recCheck;
    TextView recTitle, recDesc;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCheck = itemView.findViewById(R.id.recCheck);
        recCard = itemView.findViewById(R.id.recCard);
        recDesc = itemView.findViewById(R.id.recDesc);
        recTitle = itemView.findViewById(R.id.recTitle);
    }
}

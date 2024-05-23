package com.example.beta;

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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class My2ndAdapter extends RecyclerView.Adapter<My2ndViewHolder> {

    private Context context;
    private List<Chore> dataList;

    public My2ndAdapter(Context context, List<Chore> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public My2ndViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new My2ndViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull My2ndViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(dataList.get(position).getTitle());
        holder.recDesc.setText(dataList.get(position).getDescription());

        Chore currentChore = dataList.get(holder.getAdapterPosition());


        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Image", currentChore.getDataImage());
                intent.putExtra("Description", currentChore.getDescription());
                intent.putExtra("Title", currentChore.getTitle());
                intent.putExtra("Key", currentChore.getKey());
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
                reference.child(currentChore.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                                                reference.child(currentChore.getKey()).removeValue();
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
                                        chore.setWhoEnded(mAuth.getUid());
                                        chore.setStatus("d");
                                        reference.child(currentChore.getKey()).setValue(chore).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                Log.e("MyAdapter", "Snapshot does not exist for key: " + currentChore.getKey());
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
class My2ndViewHolder extends RecyclerView.ViewHolder {

    ImageView recImage, recCheck;
    TextView recTitle, recDesc;
    CardView recCard;

    public My2ndViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCheck = itemView.findViewById(R.id.recCheck);
        recCard = itemView.findViewById(R.id.recCard);
        recDesc = itemView.findViewById(R.id.recDesc);
        recTitle = itemView.findViewById(R.id.recTitle);
    }
}
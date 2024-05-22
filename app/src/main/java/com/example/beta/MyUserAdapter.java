package com.example.beta;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyUserAdapter extends RecyclerView.Adapter<MyUserViewHolder> {

    private Context context;
    private List<User> dataList;

    public MyUserAdapter(Context context, List<User> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_user, parent, false);
        return new MyUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyUserViewHolder holder, int position) {
        holder.recName.setText(dataList.get(position).getUName());

        holder.recCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof UploadActivity){
                    if (holder.recCheck.isChecked()){
                        ((UploadActivity) context).addChoreToUser(dataList.get(position).getUid());
                    }else{
                        ((UploadActivity) context).removeChoreToUser(dataList.get(position).getUid());
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}

class MyUserViewHolder extends RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recName;
    CardView recCard;
    CheckBox recCheck;



    public MyUserViewHolder(@NonNull View itemView) {
        super(itemView);

        recName = itemView.findViewById(R.id.recTitle);
        recCard = itemView.findViewById(R.id.recCard);
        recImage = itemView.findViewById(R.id.recImage);
        recCheck = itemView.findViewById(R.id.checkBox);


    }
}
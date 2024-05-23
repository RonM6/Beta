package com.example.beta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            DatabaseReference refChores = FirebaseDatabase.getInstance().getReference().child("chores");

            refChores.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot choreSnapshot : snapshot.getChildren()) {
                        Chore chore = choreSnapshot.getValue(Chore.class);
                        if (chore != null && chore.getStatus().equals("a")) {
                            AlarmHelper.setAlarm(context, chore.getKey(), chore.getDueDate(), chore.getDueTime(), chore.getTitle());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}

package com.example.beta;

import static com.example.beta.DBref.FBDB;
import static com.example.beta.DBref.mAuth;
import static com.example.beta.DBref.refChores;
import static com.example.beta.DBref.refUsers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Dashboard extends Fragment {
    ValueEventListener eventListener, eventListener2;
    RecyclerView recyclerView, myRecyclerView;
    List<Chore> chores;
    List<Chore> myChores;
    List<String> choreList;
    List<String> myChoreList;
    MyAdapter adapter;
    MyAdapter myAdapter;
    String fid;
    String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        SharedPreferences settings = requireActivity().getSharedPreferences("PREFS_NAME", 0);
        fid = settings.getString("fid", "-1");
        uid = mAuth.getUid();

        recyclerView = view.findViewById(R.id.recyclerView);
        myRecyclerView = view.findViewById(R.id.myRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        GridLayoutManager myGridLayoutManager = new GridLayoutManager(getContext(), 1);
        myRecyclerView.setLayoutManager(myGridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        chores = new ArrayList<>();
        myChores = new ArrayList<>();

        adapter = new MyAdapter(getContext(), chores);
        myAdapter = new MyAdapter(getContext(), myChores);

        recyclerView.setAdapter(adapter);
        myRecyclerView.setAdapter(myAdapter);

        eventListener = refUsers.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.show();
                choreList = snapshot.getValue(User.class).getChores();
                myChoreList = snapshot.getValue(User.class).getMyChores();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        eventListener2 = refChores.child(fid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.show();
                chores.clear();
                myChores.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Chore chore = itemSnapshot.getValue(Chore.class);
                    if (choreList.contains(chore.getCid()) & chore.getStatus().equals("a")) {
                        chore.setKey(itemSnapshot.getKey());
                        chores.add(chore);
                    }
                    if (myChoreList.contains(chore.getCid())& chore.getStatus().equals("a")) {
                        chore.setKey(itemSnapshot.getKey());
                        myChores.add(chore);
                    }
                }
                adapter.notifyDataSetChanged();
                myAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        return view;
    }

}

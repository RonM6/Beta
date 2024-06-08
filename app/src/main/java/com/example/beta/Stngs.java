package com.example.beta;

import static com.example.beta.DBref.mAuth;
import static com.example.beta.DBref.refUsers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

public class Stngs extends Fragment {
    Button logoutBT;
    private AlertDialog.Builder alertDialog;
    String name = "-1";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_stngs, container, false);

        logoutBT = view.findViewById(R.id.loguotBT);
        logoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Are you sure?");
                alertDialog.setMessage("Do you really want to logout?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        SharedPreferences temp;
                        temp = requireActivity().getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = temp.edit();
                        editor.putString("fid", "-1");
                        editor.commit();
                        Intent intent = new Intent(getContext(), SignUp.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog ad = alertDialog.create();
                ad.show();
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sp = requireActivity().getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
        String fid = sp.getString("fid", "-1");

        refUsers.child(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        name = user.getName();
                        TextView textView2 = view.findViewById(R.id.textView2);
                        textView2.setText("Hello there " + name + "!");
                    }
                }
            }
        });

        TextView textView = view.findViewById(R.id.fidtv);


        textView.setText(fid);


    }
}

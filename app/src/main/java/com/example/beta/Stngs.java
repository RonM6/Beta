package com.example.beta;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;

public class Stngs extends Fragment {
    Button logoutBT;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_stngs, container, false);

        logoutBT = view.findViewById(R.id.loguotBT);
        logoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences temp;
                temp = requireActivity().getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=temp.edit();
                editor.putString("fid", "-1");
                editor.commit();
                Intent intent = new Intent(getContext(), SignUp.class);
                startActivity(intent);
                requireActivity().finish();
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

        SharedPreferences settings = requireActivity().getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
        String fid = settings.getString("fid", "-1");

        TextView textView = view.findViewById(R.id.fidtv);
        textView.setText(fid);



    }
}

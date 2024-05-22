package com.example.beta;

import androidx.appcompat.app.AppCompatActivity;
import com.example.beta.R;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.beta.Dashboard;
import com.example.beta.Stngs;
import com.example.beta.UpdateActivity;
import com.example.beta.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new Dashboard());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            String id = item.toString();

            switch (id) {
                case "Home":
                    replaceFragment(new Dashboard());
                    break;
                case "Family Chores":
                    replaceFragment(new FamilyChores());
                    break;
                case "Done Chores":
                    replaceFragment(new DoneChores());
                    break;
                case "Setting":
                    replaceFragment(new Stngs());
                    break;
            }

            return true;

        });

    }


    public void onTaskComplete() {
        // Navigate to the home fragment or perform any other action here
        replaceFragment(new Dashboard()); // Replace HomeFragment with your actual home fragment class
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }



    public void newChore(View view) {
        startActivity(new Intent(this, UploadActivity.class));
    }
}
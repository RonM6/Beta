package com.example.beta;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.beta.Dashboard;
import com.example.beta.Stngs;
import com.example.beta.UpdateActivity;
import com.example.beta.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int MENU_HOME = 1;
    private static final int MENU_FAMILY = 2;
    private static final int MENU_DONE = 3;
    private static final int MENU_SETTINGS = 4;


    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new Dashboard());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            switch (itemId) {
                case MENU_HOME:
                    replaceFragment(new Dashboard());
                    break;
                case MENU_FAMILY:
                    replaceFragment(new UploadActivity());
                    break;
                case MENU_DONE:

                case MENU_SETTINGS:
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


    public void addChoreToUser(String uid){
        UploadActivity.userList.add(uid);
        if (UploadActivity.userList.contains(uid)){
            Toast.makeText(this, "Added ", Toast.LENGTH_SHORT).show();
        }
    }
    public void removeChoreToUser(String uid){
        UploadActivity.userList.remove(uid);
        if (!UploadActivity.userList.contains(uid)){
            Toast.makeText(this, "Removed ", Toast.LENGTH_SHORT).show();
        }
    }

    public void newChore(View view) {
        replaceFragment(new UploadActivity());
    }
}
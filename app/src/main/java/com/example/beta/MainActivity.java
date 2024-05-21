package com.example.beta;

import androidx.appcompat.app.AppCompatActivity;
import com.example.beta.R;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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


    public void addChoreToUser(String uid){
        UploadActivity.userList.add(uid);
        if (UploadActivity.userList.contains(uid)){
            Toast.makeText(this, "Added ", Toast.LENGTH_SHORT).show();
        }
    }
    public void dishes(View view) {
        String packageName = getPackageName();

        int resourceId = R.drawable.washing_dishes;

        UploadActivity.uri = Uri.parse("android.resource://" + packageName + "/" + resourceId);
        UploadActivity.uploadTopic.setText("Dishes");
        UploadActivity.uploadDesc.setText("Wash the dishes");


    }
    public void laundry(View view) {
        String packageName = getPackageName();

        int resourceId = R.drawable.laundry_machine;

        UploadActivity.uri = Uri.parse("android.resource://" + packageName + "/" + resourceId);
        UploadActivity.uploadTopic.setText("Laundry");
        UploadActivity.uploadDesc.setText("Fold the laundry");
    }

    public void vacuum(View view) {
        String packageName = getPackageName();

        int resourceId = R.drawable.vacuum;

        UploadActivity.uri = Uri.parse("android.resource://" + packageName + "/" + resourceId);
        UploadActivity.uploadTopic.setText("Vacuum");
        UploadActivity.uploadDesc.setText("Vacuum the floor");
    }

    public void trash(View view) {
        String packageName = getPackageName();

        int resourceId = R.drawable.trash;

        UploadActivity.uri = Uri.parse("android.resource://" + packageName + "/" + resourceId);
        UploadActivity.uploadTopic.setText("Trash");
        UploadActivity.uploadDesc.setText("Take out the trash");
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
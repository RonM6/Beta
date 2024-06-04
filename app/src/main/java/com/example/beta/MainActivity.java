package com.example.beta;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.beta.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static final int REQUEST_CODE_SCHEDULE_EXACT_ALARM = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new Dashboard());
        binding.bottomNavigationView.setBackground(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkAndRequestExactAlarmPermission();
        }

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

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkAndRequestExactAlarmPermission() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivityForResult(intent, REQUEST_CODE_SCHEDULE_EXACT_ALARM);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCHEDULE_EXACT_ALARM) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
package com.example.beta;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBref {
    public static String fid;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://beta-52e80-default-rtdb.europe-west1.firebasedatabase.app");
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    public static DatabaseReference refFamilies = FBDB.getReference("Families");
    public static DatabaseReference refChores = FBDB.getReference("Chores");
    public static DatabaseReference refDChores = FBDB.getReference("Dead_Chores");
}

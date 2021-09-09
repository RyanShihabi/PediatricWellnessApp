package com.ryanshihabi.choc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseAuth mAuth;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            Log.d("SignInActivity", "current user: " + user.getDisplayName());
        }

        db = FirebaseFirestore.getInstance();

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(user != null){
                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if(doc.getString("theme") != null){
                            Intent intent = new Intent(MainActivity.this, BoardActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                            startActivity(intent);
                        }
                    }else{
                        Log.d("MainActivity", Objects.requireNonNull(task.getException()).toString());
                    }
                });
            }else{
                Intent SignIn = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(SignIn);
            }
            finish();
        }, 500);
    }
}
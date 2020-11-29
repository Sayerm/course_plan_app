package com.example.coursePlan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread() {
            @Override
            public void run() {

                try {


                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    String uid = firebaseAuth.getCurrentUser().getUid();

                    if (firebaseAuth.getCurrentUser() != null) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (snapshot.hasChild("type")) {
                                        String type = snapshot.child("type").getValue(String.class);
                                        try {
                                            if (type.equals("Admin")) {
                                                startActivity(new Intent(SplashScreen.this, AdminPanel.class));
                                                finish();
                                            } else {
                                                startActivity(new Intent(SplashScreen.this, HomePage.class));
                                                finish();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                error.toException().printStackTrace();
                                Toast.makeText(SplashScreen.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        sendToStart();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sendToStart();
                }


            }
        };
        thread.start();

    }

    private void sendToStart() {
        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
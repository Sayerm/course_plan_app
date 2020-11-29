package com.example.coursePlan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursePlan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class StudentLogin extends AppCompatActivity {

    TextInputEditText studentIdEditText, passwordEditText;
    String studentId, password;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Button loginButton;
    TextView forgotPasswordTextView, signUpTextView;
    FirebaseAuth mAuth;
    String resetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        studentIdEditText = findViewById(R.id.studentId);
        passwordEditText = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginBt);
        forgotPasswordTextView = findViewById(R.id.forgotPass);
        signUpTextView = findViewById(R.id.notHaveAnAcc);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentLogin.this, RegisterActivity.class));
                finish();
            }
        });

    }

    public void SignInBt(View view) {

        studentId = studentIdEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        authorising(studentId,password);




    }

    private void authorising(final String studentId, final String password) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if (dataSnapshot.hasChild("student_id")){
                        try {
                            if(Objects.equals(dataSnapshot.child("student_id").getValue(String.class), studentId)){
                                if (Objects.equals(dataSnapshot.child("password").getValue(String.class), password)){
                                   String email= dataSnapshot.child("email").getValue(String.class);
                                    progressDialog.setTitle("logging in...");
                                    progressDialog.show();
                                    progressDialog.setMessage("Please wait...\n Be patient");
                                    progressDialog.setCancelable(false);
                                    LoginUser(email, password);
                                }else {
                                    Toast.makeText(StudentLogin.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(StudentLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(StudentLogin.this, "No student account registered", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentLogin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            final String uid = firebaseUser.getUid();
                            final String device_token = FirebaseInstanceId.getInstance().getToken();
                            DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                                    .child("device_token");
                            databaseReference.setValue(device_token);

                            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        if (snapshot.hasChild("type")) {
                                            String type = snapshot.child("type").getValue(String.class);
                                            try {
                                                if (type.equals("Admin")) {
                                                    // Sign in success, update UI with the signed-in user's information
                                                    startActivity(new Intent(StudentLogin.this, AdminPanel.class));
                                                    finish();
                                                } else {
                                                    startActivity(new Intent(StudentLogin.this, HomePage.class));
                                                    finish();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(StudentLogin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    error.toException().printStackTrace();
                                    Toast.makeText(StudentLogin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(StudentLogin.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}

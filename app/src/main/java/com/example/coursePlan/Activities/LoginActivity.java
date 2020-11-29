package com.example.coursePlan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.coursePlan.DialogFragment.ForgotPasswordDialogue;
import com.example.coursePlan.Fragments.LoginFragment;
import com.example.coursePlan.Fragments.StudentLoginFragment;
import com.example.coursePlan.Fragments.TeacherLoginFragment;
import com.example.coursePlan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class LoginActivity extends AppCompatActivity implements ForgotPasswordDialogue.SendResetPassword {

    TextInputEditText emailEditText, passwordEditText;
    String email, password;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Button loginButton;
    Spinner loginTypeSpinner;
    TextView forgotPasswordTextView, signUpTextView;
    FirebaseAuth mAuth;
    String resetEmail;
    FrameLayout frameLayout;
    LoginFragment loginFragment=new LoginFragment();
    StudentLoginFragment studentLoginFragment=new StudentLoginFragment();
    TeacherLoginFragment teacherLoginFragment=new TeacherLoginFragment();
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        loginTypeSpinner=findViewById(R.id.loginTypeSpinner);
        frameLayout=findViewById(R.id.frameLt);
        emailEditText = findViewById(R.id.loginEmail);
        passwordEditText = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginBt);
        forgotPasswordTextView = findViewById(R.id.forgotPass);
        signUpTextView = findViewById(R.id.notHaveAnAcc);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordDialogue forgotPasswordDialogue = new ForgotPasswordDialogue();
                forgotPasswordDialogue.show(getSupportFragmentManager(), forgotPasswordDialogue.getTag());
            }
        });
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.frameLt, loginFragment).commit();

        loginTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type=loginTypeSpinner.getSelectedItem().toString();
                if (type.contains("Student")){
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.frameLt, studentLoginFragment).commit();
                }else if (type.contains("Teacher")){
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.frameLt, teacherLoginFragment).commit();
                }else {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.frameLt, loginFragment).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void SignInBt(View view) {

        email = emailEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        progressDialog.setTitle("logging in...");
        progressDialog.show();
        progressDialog.setMessage("Please wait...\n Be patient");
        progressDialog.setCancelable(false);
        LoginUser(email, password);

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
                                                    startActivity(new Intent(LoginActivity.this, AdminPanel.class));
                                                    finish();
                                                } else {
                                                    startActivity(new Intent(LoginActivity.this, HomePage.class));
                                                    finish();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    error.toException().printStackTrace();
                                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void ResetEmail(String email) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending email");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
//       resetEmail=email;
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "We have sent you a password reset link in your email link. Please check your inbox or spam box", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error!!! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
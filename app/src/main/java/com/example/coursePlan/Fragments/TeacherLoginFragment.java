package com.example.coursePlan.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coursePlan.Activities.AdminPanel;
import com.example.coursePlan.Activities.HomePage;
import com.example.coursePlan.Activities.RegisterActivity;
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


public class TeacherLoginFragment extends Fragment {

    TextInputEditText studentIdEditText, passwordEditText;
    String teacherId, password;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Button loginButton;
    TextView forgotPasswordTextView, signUpTextView;
    FirebaseAuth mAuth;
    public TeacherLoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(view.getContext());

        studentIdEditText = view.findViewById(R.id.studentId);
        passwordEditText = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.loginBt);
        forgotPasswordTextView = view.findViewById(R.id.forgotPass);
        signUpTextView = view.findViewById(R.id.notHaveAnAcc);

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RegisterActivity.class));
                try {
                    getActivity().finish();
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInBt();
            }
        });

        return view;
    }

    public void SignInBt() {

        teacherId = studentIdEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        authorising(teacherId, password);
    }

    private void authorising(final String teacherId, final String password) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.hasChild("teacher_id")) {
                        try {
                            if (Objects.equals(dataSnapshot.child("teacher_id").getValue(String.class), teacherId)) {
                                if (Objects.equals(dataSnapshot.child("password").getValue(String.class), password)) {
                                    String email = dataSnapshot.child("email").getValue(String.class);
                                    progressDialog.setTitle("logging in...");
                                    progressDialog.show();
                                    progressDialog.setMessage("Please wait...\n Be patient");
                                    progressDialog.setCancelable(false);
                                    LoginUser(email, password);
                                } else {
                                    Toast.makeText(getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else if (!dataSnapshot.hasChild("teacher_id")){
                        Toast.makeText(getContext(), "No teacher account registered against this id", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                                                    startActivity(new Intent(getContext(), AdminPanel.class));
                                                    try {
                                                        getActivity().finish();
                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {
                                                    startActivity(new Intent(getContext(), HomePage.class));
                                                    try {
                                                        getActivity().finish();
                                                    } catch (Exception e) {
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    error.toException().printStackTrace();
                                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
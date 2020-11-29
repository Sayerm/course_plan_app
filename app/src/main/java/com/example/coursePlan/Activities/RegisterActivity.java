package com.example.coursePlan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.coursePlan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import static com.example.coursePlan.Constans.BaseUrl.BASE_URL;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText fullNameEt, phoneEt, emailEt, passwordEt, batchEt, studentIdNoEt, semesterIdNoEt,teacherIdEt;
    Spinner userTypeSpinner;
    String[] type = {"Teacher", "Student"};
    String types;
    TextView login;
    LinearLayout batchLayout;
    TextInputLayout teacherIdLt;
    FirebaseAuth mAuth;
    String name, email, phone, batchNo, studentIdNo, password,semesterNo,teacherId;
    Button signUpBt;

    DatabaseReference firebaseDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.haveAnAcc);
        signUpBt = findViewById(R.id.signupBt);
        studentIdNoEt = findViewById(R.id.studentIdNo);
        batchLayout = findViewById(R.id.batchLayout);
        userTypeSpinner = findViewById(R.id.userType);
        fullNameEt = findViewById(R.id.fullNameEt);
        phoneEt = findViewById(R.id.signUpPhone);
        batchEt = findViewById(R.id.batch);
        semesterIdNoEt=findViewById(R.id.semesterNo);
        emailEt = findViewById(R.id.signUpEmail);
        passwordEt = findViewById(R.id.signUpPassword);
        teacherIdEt=findViewById(R.id.teacherIdEt);
        teacherIdLt=findViewById(R.id.teacherIdLt);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, type);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        userTypeSpinner.setAdapter(arrayAdapter);*/
        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                types = userTypeSpinner.getSelectedItem().toString();
                if (types.contains("Teacher")) {
                    batchLayout.setVisibility(View.GONE);
                    teacherIdLt.setVisibility(View.VISIBLE);
                    batchLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                }
                if (types.contains("Student")) {
                    batchLayout.setVisibility(View.VISIBLE);
                    teacherIdLt.setVisibility(View.GONE);
                    batchLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void SignUpBt(View view) {
        name = fullNameEt.getText().toString().trim();
        phone = phoneEt.getText().toString().trim();
        email = emailEt.getText().toString().trim();
        batchNo = batchEt.getText().toString().trim();
        studentIdNo = studentIdNoEt.getText().toString().trim();
        semesterNo=semesterIdNoEt.getText().toString().trim();
        teacherId=teacherIdEt.getText().toString().trim();
        password = passwordEt.getText().toString().trim();

        String types = userTypeSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            fullNameEt.setError("Enter a name");
            fullNameEt.requestFocus();
        } else if (TextUtils.isEmpty(phone)) {
            phoneEt.setError("Put valid phone number");
            phoneEt.requestFocus();
        } else if (TextUtils.isEmpty(email)) {
            emailEt.setError("Enter an email");
            emailEt.requestFocus();
        } else if (types.equals("Student") && TextUtils.isEmpty(batchNo)) {
            batchEt.setError("Enter batch number");
            batchEt.requestFocus();
        } else if (types.equals("Student") && TextUtils.isEmpty(semesterNo)) {
            semesterIdNoEt.setError("Enter semester number");
            semesterIdNoEt.requestFocus();
        } else if (types.equals("Student") && TextUtils.isEmpty(studentIdNo)) {
            studentIdNoEt.setError("Enter Student id");
            studentIdNoEt.requestFocus();
        } else if (types.equals("Teacher") && TextUtils.isEmpty(teacherId)) {
            studentIdNoEt.setError("Enter Teacher id");
            studentIdNoEt.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Enter a password");
            passwordEt.requestFocus();
        } else if (!(password.length() > 5)) {
            passwordEt.setError("Password must 6 character or larger");
            passwordEt.requestFocus();
        } else {
            progressDialog.setTitle("Creating account...");
            progressDialog.show();
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            RegisterUser(email, password, types);
        }
    }

    private void RegisterUser(final String email, final String password, final String types) {
        mAuth.createUserWithEmailAndPassword(this.email, this.password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
//                            Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();


                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = firebaseUser.getUid();

                            firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            final String device_token = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String, String> map = new HashMap<>();

                            map.put("name", name);
                            map.put("image", "Default");
                            map.put("phone", phone);
                            map.put("batch", batchNo);
                            map.put("type", types);
                            map.put("student_id", studentIdNo);
                            map.put("teacher_id", teacherId);
                            map.put("semester_number", semesterNo);
                            map.put("password", password);
                            map.put("thumb_image", "Default");
                            map.put("device_token", device_token);
                            map.put("email", email);
                            map.put("online", "false");
                            map.put("status", "Hey there🖐");


                            firebaseDatabase.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        StringRequest request = new StringRequest(Request.Method.POST, BASE_URL + "upload_users.php", new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if (response.equals("{\"response\":\"submitted\"}")) {
                                                    Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {

                                                Map<String, String> params = new HashMap<>();
                                                params.put("user_id", firebaseUser.getUid());
                                                params.put("name", name);
                                                params.put("email", email);
                                                params.put("phone", phone);
                                                params.put("batch", batchNo);
                                                params.put("type", types);
                                                params.put("semester_number", semesterNo);
                                                params.put("student_id", studentIdNo);
                                                params.put("profile_image", "default");
                                                params.put("device_token", device_token);
                                                params.put("password", password);

                                                return params;
                                            }
                                        };
                                        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                                        requestQueue.add(request);

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
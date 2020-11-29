package com.example.coursePlan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursePlan.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {
    private static final String TAG = "UPDATE_INFO";
    LinearLayout studentInfoLayout, teacherInfoLayout, noInfoLayout;
    ImageView closeImageView, saveImageView;
    TextInputEditText teacherStatusEditText,studentStatusEditText, studyingAtEditText, batchEditText, studentIdEditText, graduatedFromEditText, expertInputEditText, teachingAtEditText, teacherIdEditText, experienceOfEditText;

    String userName, teacherStatus,studentStatus, studyingAt, batch, studentId, graduatedFrom, expertIn, teachingAt, teacherId, experienceOf, profileImage, type;
    DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        studyingAt = intent.getStringExtra("studyingAt");
        batch = intent.getStringExtra("batch");
        studentId = intent.getStringExtra("studentId");
        graduatedFrom = intent.getStringExtra("graduatedFrom");
        expertIn = intent.getStringExtra("expertIn");
        teachingAt = intent.getStringExtra("teachingAt");
        teacherId = intent.getStringExtra("teacherId");
        experienceOf = intent.getStringExtra("experience");
        teacherStatus = intent.getStringExtra("ts");
        studentStatus = intent.getStringExtra("ss");

        studyingAtEditText=findViewById(R.id.studyingAt);
        batchEditText=findViewById(R.id.batch);
        studentIdEditText=findViewById(R.id.studentId);
        graduatedFromEditText=findViewById(R.id.graduatedFrom);
        expertInputEditText=findViewById(R.id.expertIn);
        teachingAtEditText=findViewById(R.id.teachingAT);
        teacherIdEditText=findViewById(R.id.teachersId);
        experienceOfEditText=findViewById(R.id.experience);
        studentInfoLayout = findViewById(R.id.studentInfoUpdateBoxs);
        teacherInfoLayout = findViewById(R.id.teachersInfoUpdateBoxs);
        noInfoLayout = findViewById(R.id.noType);
        closeImageView = findViewById(R.id.closeEditing);
        saveImageView = findViewById(R.id.saveEditing);
        teacherStatusEditText = findViewById(R.id.teacherStatus);
        studentStatusEditText = findViewById(R.id.studentStatus);

        studentInfoLayout.setVisibility(View.GONE);
        teacherInfoLayout.setVisibility(View.GONE);
        noInfoLayout.setVisibility(View.GONE);

        if ("Student".equals(type)) {
            studentInfoLayout.setVisibility(View.VISIBLE);
            teacherInfoLayout.setVisibility(View.GONE);
            noInfoLayout.setVisibility(View.GONE);
            studyingAtEditText.setText(studyingAt);
            batchEditText.setText(batch);
            studentIdEditText.setText(studentId);
            studentStatusEditText.setText(studentStatus);
        } else if ("Teacher".equals(type)) {
            teacherInfoLayout.setVisibility(View.VISIBLE);
            studentInfoLayout.setVisibility(View.GONE);
            noInfoLayout.setVisibility(View.GONE);
            graduatedFromEditText.setText(graduatedFrom);
            teachingAtEditText.setText(teachingAt);
            expertInputEditText.setText(expertIn);
            teachingAtEditText.setText(teachingAt);
            teacherIdEditText.setText(teacherId);
            experienceOfEditText.setText(experienceOf);
            teacherStatusEditText.setText(teacherStatus);
        } else {
            studentInfoLayout.setVisibility(View.GONE);
            teacherInfoLayout.setVisibility(View.GONE);
            noInfoLayout.setVisibility(View.VISIBLE);
        }

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfileActivity.this, HomePage.class));
            }
        });
        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateInfo(studyingAt, batch, studentId, graduatedFrom, expertIn, teachingAt, teacherId, experienceOf, profileImage, type,studentStatus,teacherStatus);
//                startActivity(new Intent(UpdateProfileActivity.this, HomePage.class));
            }
        });

    }

    private void UpdateInfo(String studyingAt, String batch, String studentId, String graduatedFrom, String expertIn, String teachingAt, String teacherId, String experienceOf, String profileImage, String type,String studentStatus,String teacherStatus) {

        Map<String, Object>uploadInfo=new HashMap<>();

        uploadInfo.put("batch",batchEditText.getText().toString());
        uploadInfo.put("student_id",studentIdEditText.getText().toString());
        uploadInfo.put("studyingAt",studyingAtEditText.getText().toString());
        uploadInfo.put("expertIn",expertInputEditText.getText().toString());
        uploadInfo.put("teachingAt",teachingAtEditText.getText().toString());
        uploadInfo.put("graduatedFrom",graduatedFromEditText.getText().toString());
        uploadInfo.put("teacherId",teacherIdEditText.getText().toString());
        uploadInfo.put("experience",experienceOfEditText.getText().toString());
        uploadInfo.put("teachersStatus",teacherStatusEditText.getText().toString());
        uploadInfo.put("studentStatus",studentStatusEditText.getText().toString());

        try {
            userDatabaseReference=FirebaseDatabase.getInstance().getReference().child("Users");
            userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(uploadInfo, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error!=null){
                        Toast.makeText(UpdateProfileActivity.this, "Your info is updated", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onComplete: Worked");

                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "UpdateInfo: "+e.getMessage());
        }

        startActivity(new Intent(UpdateProfileActivity.this,HomePage.class));

    }
}
package com.example.coursePlan.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coursePlan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddSemester extends AppCompatActivity {

    Spinner semesterSpinner;
    TextInputLayout subjectLayout;
    String semester,subject;
    TextInputEditText subjectEt;
    ArrayAdapter<String>adapter;
    ArrayList<String>arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_semester);

        semesterSpinner=findViewById(R.id.semesterNoSp);
        subjectLayout=findViewById(R.id.subjectLayout);
        subjectEt=findViewById(R.id.subjectEt);

        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semester=semesterSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void Submit(View view) {
        subject=subjectEt.getText().toString();
        subject=subjectEt.getText().toString().trim();

       if (semesterSpinner.getSelectedItemPosition() == 0){
           Toast.makeText(this, "Please select a semester", Toast.LENGTH_SHORT).show();
       }else if (subject.isEmpty()){
           subjectLayout.setError("Please enter a subject");
           subjectLayout.requestFocus();
       }else {
           uploadSubject(subject,semester);
       }

    }

    private void uploadSubject(String subject, String semester) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Semester").child(semester).child("Subjects").push();
        databaseReference.setValue(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddSemester.this, "Subject added", Toast.LENGTH_SHORT).show();
                    subjectEt.setText("");
                }else {
                    Toast.makeText(AddSemester.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
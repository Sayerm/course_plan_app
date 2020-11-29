package com.example.coursePlan.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.coursePlan.Fragments.QuestionsListFragment;
import com.example.coursePlan.R;

public class QuestionApproval extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_approval);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, new QuestionsListFragment()).commit();




    }
}
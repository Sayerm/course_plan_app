package com.example.coursePlan.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.coursePlan.Fragments.QuestionsListFragment;
import com.example.coursePlan.R;

public class AllQuestionActivity extends AppCompatActivity {

    QuestionsListFragment questionsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_question);

        questionsListFragment=new QuestionsListFragment(AllQuestionActivity.this,"All Question");
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .addToBackStack(null).replace(R.id.frame, questionsListFragment).commit();
        Bundle bundle = new Bundle();
        bundle.putString("from", "From");
        questionsListFragment.setArguments(bundle);




    }
}
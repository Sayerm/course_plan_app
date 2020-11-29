package com.example.coursePlan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursePlan.DialogFragment.logoutDialogue;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class AdminPanel extends AppCompatActivity implements logoutDialogue.ApplyExitApp {

    ScrollView scrollView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        getSupportActionBar().setTitle("Admin Panel");

        firebaseAuth = FirebaseAuth.getInstance();
        scrollView = findViewById(R.id.scrollView);

        OverScrollDecoratorHelper.setUpOverScroll(scrollView);

    }

    public void AllUsers(View view) {
        Intent intent = new Intent(this, AllUsersActivity.class);
        intent.putExtra("from", "admin");
        startActivity(intent);
    }

    public void QsApproval(View view) {
        startActivity(new Intent(this, QuestionApproval.class));
    }

    public void BestQuestion(View view) {
        startActivity(new Intent(this,BestQuestionActivity.class));
    }

    public void ResourceApproval(View view) {
        startActivity(new Intent(this,ResourceApproval.class));
    }

    public void AllQuestion(View view) {
        startActivity(new Intent(this,AllQuestionActivity.class));
    }

    public void logout(View view) {
        logoutDialogue logoutDialogue = new logoutDialogue();
        logoutDialogue.show(getSupportFragmentManager(), logoutDialogue.getTag());
    }

    @Override
    public void exitApp(TextView textView) {
        firebaseAuth.signOut();
        sendToStart();
    }

    private void sendToStart() {
        Intent intent = new Intent(AdminPanel.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void AddUser(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
    }

    public void AddSubject(View view) {
        startActivity(new Intent(this,AddSemester.class));
    }
}
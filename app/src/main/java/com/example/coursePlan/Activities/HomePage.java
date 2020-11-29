package com.example.coursePlan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursePlan.DialogFragment.ExitDialogue;
import com.example.coursePlan.DialogFragment.logoutDialogue;
import com.example.coursePlan.Fragments.AskQuestionFragment;
import com.example.coursePlan.Fragments.ProfileFragment;
import com.example.coursePlan.Fragments.QuestionsListFragment;
import com.example.coursePlan.Fragments.ResourceFragment;
import com.example.coursePlan.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomePage extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, ExitDialogue.ApplyExitApp, logoutDialogue.ApplyExitApp {

    FirebaseAuth firebaseAuth;

    DatabaseReference userDatabase;
    FirebaseUser firebaseUser;
    String currentUser;
    BottomNavigationView bottomNavigationView;
    AskQuestionFragment askQuestionFragment = new AskQuestionFragment();
    QuestionsListFragment questionsListFragment = new QuestionsListFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    ResourceFragment resourceFragment = new ResourceFragment();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null) {
            currentUser = firebaseAuth.getCurrentUser().getUid();
            userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser);
        } else {
            sendToStart();
        }


        //==================STARTING UI WORK==================================

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.questionssListMenuId);


    }

    private void sendToStart() {
        Intent intent = new Intent(HomePage.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        ExitDialogue exitDialogue = new ExitDialogue();
        exitDialogue.show(getSupportFragmentManager(), exitDialogue.getTag());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.askQsMenuId) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.bottom_nav_frame_layout, askQuestionFragment).commit();
        }if (item.getItemId() == R.id.resMenuId) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.bottom_nav_frame_layout, resourceFragment).commit();
        }
        if (item.getItemId() == R.id.questionssListMenuId) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.bottom_nav_frame_layout, questionsListFragment).commit();
        }
        if (item.getItemId() == R.id.profileMenuId) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).addToBackStack(null).replace(R.id.bottom_nav_frame_layout, profileFragment).commit();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout_main) {
            logoutDialogue logoutDialogue = new logoutDialogue();
            logoutDialogue.show(getSupportFragmentManager(), logoutDialogue.getTag());
        }
        if (item.getItemId() == R.id.all_users) {
            startActivity(new Intent(HomePage.this, AllUsersActivity.class));
        } if (item.getItemId() == R.id.conversation) {
            startActivity(new Intent(HomePage.this, ConversationActivity.class));
        }
        return true;
    }


    @Override
    public void exitApp() {
        finishAffinity();
        finish();
    }

    @Override
    public void exitApp(TextView textView) {
        firebaseAuth.signOut();
        sendToStart();
    }
}
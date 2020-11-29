package com.example.coursePlan.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.coursePlan.Adapters.AllUsersAdapter;
import com.example.coursePlan.Models.Users;
import com.example.coursePlan.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllUsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AllUsersAdapter adapter;
    List<Users> usersList;
    String name, profileImage, userId, myType, batch, type, myBatch;
    DatabaseReference userDatabaseReference;
    SwipeRefreshLayout swipeRefreshLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabaseReference.keepSynced(true);

        usersList = new ArrayList<>();

        relativeLayout = findViewById(R.id.rel);
        shimmerFrameLayout = findViewById(R.id.shimmerContentView);
        relativeLayout.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.allUsersList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myType = snapshot.child("type").getValue(String.class);
                myBatch = snapshot.child("batch").getValue(String.class);
                if (Objects.equals(myType, "Student")) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Batch: " + myBatch);
                } else if (Objects.equals(myType, "Teacher")) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("All Users");
                } else if(getIntent().getStringExtra("from").equals("admin")){
                    Objects.requireNonNull(getSupportActionBar()).setTitle("All Users");
                } else {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("All " + myType);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RetrieveUsers();
            }
        });


        RetrieveUsers();

    }

    private void RetrieveUsers() {

        shimmerFrameLayout.showShimmer(true);
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                String from=getIntent().getStringExtra("from");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    batch = dataSnapshot.child("batch").getValue(String.class);
                    type = dataSnapshot.child("type").getValue(String.class);
                    if (myType.equals(type)) {

                        if (myBatch != null && myBatch.equals(batch)) {
                            userId = dataSnapshot.getKey();
                            name = dataSnapshot.child("name").getValue(String.class);
                            String status = dataSnapshot.child("status").getValue(String.class);
                            profileImage = dataSnapshot.child("image").getValue(String.class);
                            Users users = new Users(name, profileImage, userId,status);
                            users.setStatus("status");
                            usersList.add(users);
                        }
                    } else if (myType.equals("Teacher")) {
                        userId = dataSnapshot.getKey();
                        name = dataSnapshot.child("name").getValue(String.class);
                        String status = dataSnapshot.child("status").getValue(String.class);
                        profileImage = dataSnapshot.child("image").getValue(String.class);
                        Users users = new Users(name, profileImage, userId,status);
                        usersList.add(users);
                    }else if (from!=null&&from.equals("admin")){
                        userId = dataSnapshot.getKey();
                        name = dataSnapshot.child("name").getValue(String.class);
                        String status = dataSnapshot.child("status").getValue(String.class);
                        profileImage = dataSnapshot.child("image").getValue(String.class);
                        Users users = new Users(name, profileImage, userId,status);
                        usersList.add(users);
                    }
                }
                adapter = new AllUsersAdapter(AllUsersActivity.this, usersList);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
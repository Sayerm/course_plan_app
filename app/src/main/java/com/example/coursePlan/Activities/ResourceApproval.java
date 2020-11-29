package com.example.coursePlan.Activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.coursePlan.Adapters.ResourceListAdapter;
import com.example.coursePlan.Models.ResourceModel;
import com.example.coursePlan.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ResourceApproval extends AppCompatActivity {
    List<ResourceModel> resourceModelList;
    ResourceListAdapter resourceListAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String postKey;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_approval);

        resourceModelList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Resource");

        recyclerView = findViewById(R.id.resourceList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ResourceApproval.this));
        swipeRefreshLayout = findViewById(R.id.srl);

        RetrieveResource();

    }

    private void RetrieveResource() {
        final String postKey = databaseReference.getKey();

        reference = databaseReference.child(postKey);

        FirebaseDatabase.getInstance().getReference().child("Resource").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resourceModelList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        if (postSnapshot.hasChild("status") && postSnapshot.child("status").getValue(String.class).equals("Pending")) {
                            String description = postSnapshot.child("rDescription").getValue(String.class);
                            String title = postSnapshot.child("rTitle").getValue(String.class);
                            String time = postSnapshot.child("rUpTime").getValue(String.class);
                            String userId = postSnapshot.child("uploadedBy").getValue(String.class);
                            String postId = postSnapshot.child("resourceID").getValue(String.class);
                            String resourceCount = String.valueOf(postSnapshot.child("downloadUrl").getChildrenCount());
                            String subject=postSnapshot.child("subject").getValue(String.class);
                            ResourceModel resourceModel = new ResourceModel(userId, postId, description, title, time, resourceCount,subject);
                            resourceModel.setStatus("Pending");
                            resourceModelList.add(resourceModel);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                resourceListAdapter = new ResourceListAdapter(resourceModelList, ResourceApproval.this);
                resourceListAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(resourceListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
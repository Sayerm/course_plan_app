package com.example.coursePlan.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.coursePlan.Adapters.ResourceListAdapter;
import com.example.coursePlan.Models.ResourceModel;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FilterResource extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    DatabaseReference reference;
    List<ResourceModel> resourceModelList;
    ResourceListAdapter resourceListAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String type;
    String semester, semester2, subject;
    int semesterInt = 0;
    Spinner semesterSpinner, subjectSpinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_resource);
        resourceModelList = new ArrayList<>();
        arrayList = new ArrayList<>();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("Resource").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Resource");

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                type = snapshot.child("type").getValue(String.class);
                semester = snapshot.child("semester_number").getValue(String.class);
                /*try {
                    if (semester != null) {
                        if (semester.equals("1")) {

                        } else if (semester.equals("2")) {

                        } else if (semester.equals("3")) {

                        } else if (semester.equals("4")) {

                        } else if (semester.equals("5")) {

                        } else if (semester.equals("6")) {

                        } else if (semester.equals("7")) {

                        } else if (semester.equals("8")) {

                        }
                    }

                }catch (Exception e){
                    Toast.makeText(FilterResource.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FilterResource.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                error.toException().printStackTrace();

            }
        });

        recyclerView = findViewById(R.id.resourceList);
        semesterSpinner = findViewById(R.id.semesterSpinner);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout = findViewById(R.id.srl);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        try {
            semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    semester2 = semesterSpinner.getSelectedItem().toString();
                    adapter = new ArrayAdapter<>(FilterResource.this, R.layout.support_simple_spinner_dropdown_item, arrayList);
                    subjectSpinner.setAdapter(adapter);
                    if (position == 0) {
                        arrayList.clear();
                        Toast.makeText(FilterResource.this, "Please select a semester", Toast.LENGTH_SHORT).show();
                    } else if (position == 1) {
                        RetrieveSubject("1");
                        resourceModelList.clear();
                        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subject = subjectSpinner.getSelectedItem().toString();
                                resourceModelList.clear();
                                RetrieveResourceBySemesterAndSubj(semester, subject);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (position == 2) {
                        RetrieveSubject("2");
                        resourceModelList.clear();
                        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subject = subjectSpinner.getSelectedItem().toString();
                                resourceModelList.clear();
                                RetrieveResourceBySemesterAndSubj(semester, subject);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (position == 3) {
                        RetrieveSubject("3");
                        resourceModelList.clear();
                        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subject = subjectSpinner.getSelectedItem().toString();
                                resourceModelList.clear();
                                RetrieveResourceBySemesterAndSubj(semester, subject);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (position == 4) {
                        RetrieveSubject("4");
                        resourceModelList.clear();
                        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subject = subjectSpinner.getSelectedItem().toString();
                                resourceModelList.clear();
                                RetrieveResourceBySemesterAndSubj(semester, subject);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (position == 5) {
                        RetrieveSubject("5");
                        resourceModelList.clear();
                        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subject = subjectSpinner.getSelectedItem().toString();
                                resourceModelList.clear();
                                RetrieveResourceBySemesterAndSubj(semester, subject);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (position == 6) {
                        RetrieveSubject("6");
                        resourceModelList.clear();
                        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subject = subjectSpinner.getSelectedItem().toString();
                                resourceModelList.clear();
                                RetrieveResourceBySemesterAndSubj(semester, subject);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (position == 7) {
                        RetrieveSubject("7");
                        resourceModelList.clear();
                        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subject = subjectSpinner.getSelectedItem().toString();
                                resourceModelList.clear();
                                RetrieveResourceBySemesterAndSubj(semester, subject);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (position == 8) {
                        RetrieveSubject("8");
                        resourceModelList.clear();
                        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                subject = subjectSpinner.getSelectedItem().toString();
                                resourceModelList.clear();
                                RetrieveResourceBySemesterAndSubj(semester, subject);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
//                Toast.makeText(view.getContext(), "Please select a semester", Toast.LENGTH_SHORT).show();
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    RetrieveResourceBySemester(semester);
                }
            });
            RetrieveResourceBySemester(semester);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void RetrieveResourceBySemesterAndSubj(final String semester, final String subjectF) {
        try {
            semesterInt = Integer.parseInt(semester);

        } catch (Exception e) {
            e.printStackTrace();
        }
        final String postKey = databaseReference.getKey();

        reference = databaseReference.child(postKey);

        FirebaseDatabase.getInstance().getReference().child("Resource").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                swipeRefreshLayout.setRefreshing(false);
                resourceModelList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        if (postSnapshot.hasChild("status") && postSnapshot.child("status").getValue(String.class).equals("Approved")) {
                            String postSemester = postSnapshot.child("semester").getValue(String.class);
                            int postSemesterInt = 0;
                            if (postSemester != null) {
                                postSemesterInt = Integer.parseInt(postSemester);
                            }
                            String subject = postSnapshot.child("subject").getValue(String.class);
                            if (type.equals("Student") && subject.equals(subjectF)) {
                                String description = postSnapshot.child("rDescription").getValue(String.class);
                                String title = postSnapshot.child("rTitle").getValue(String.class);
                                String time = postSnapshot.child("rUpTime").getValue(String.class);
                                String userId = postSnapshot.child("uploadedBy").getValue(String.class);
                                String postId = postSnapshot.child("resourceID").getValue(String.class);
                                String resourceCount = String.valueOf(postSnapshot.child("downloadUrl").getChildrenCount());
                                ResourceModel resourceModel = new ResourceModel(userId, postId, description, title, time, resourceCount, subject);
                                resourceModelList.add(resourceModel);
                            } else if (type.equals("Teacher") && subject.equals(subjectF)) {
                                String description = postSnapshot.child("rDescription").getValue(String.class);
                                String title = postSnapshot.child("rTitle").getValue(String.class);
                                String time = postSnapshot.child("rUpTime").getValue(String.class);
                                String userId = postSnapshot.child("uploadedBy").getValue(String.class);
                                String postId = postSnapshot.child("resourceID").getValue(String.class);
                                String resourceCount = String.valueOf(postSnapshot.child("downloadUrl").getChildrenCount());
//                                String subject = postSnapshot.child("subject").getValue(String.class);
                                ResourceModel resourceModel = new ResourceModel(userId, postId, description, title, time, resourceCount, subject);
                                resourceModelList.add(resourceModel);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                resourceListAdapter = new ResourceListAdapter(resourceModelList, FilterResource.this);
                resourceListAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(resourceListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FilterResource.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void RetrieveSubject(final String semester) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Semester").child(semester).child("Subjects");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.getValue(String.class);
                    arrayList.add(name);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FilterResource.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void RetrieveResourceBySemester(String semester) {
        try {
            semesterInt = Integer.parseInt(semester);

        } catch (Exception e) {
            e.printStackTrace();
        }
        final String postKey = databaseReference.getKey();

        reference = databaseReference.child(postKey);

        FirebaseDatabase.getInstance().getReference().child("Resource").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                swipeRefreshLayout.setRefreshing(false);
                resourceModelList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        if (postSnapshot.hasChild("status") && postSnapshot.child("status").getValue(String.class).equals("Approved")) {
                            String postSemester = postSnapshot.child("semester").getValue(String.class);
                            int postSemesterInt = 0;
                            if (postSemester != null) {
                                postSemesterInt = Integer.parseInt(postSemester);
                            }
                            if (type.equals("Student") && semesterInt >= postSemesterInt) {
                                String description = postSnapshot.child("rDescription").getValue(String.class);
                                String title = postSnapshot.child("rTitle").getValue(String.class);
                                String time = postSnapshot.child("rUpTime").getValue(String.class);
                                String userId = postSnapshot.child("uploadedBy").getValue(String.class);
                                String postId = postSnapshot.child("resourceID").getValue(String.class);
                                String resourceCount = String.valueOf(postSnapshot.child("downloadUrl").getChildrenCount());
                                String subject = postSnapshot.child("subject").getValue(String.class);
                                ResourceModel resourceModel = new ResourceModel(userId, postId, description, title, time, resourceCount, subject);
                                resourceModelList.add(resourceModel);
                            } else if (type.equals("Teacher")) {
                                String description = postSnapshot.child("rDescription").getValue(String.class);
                                String title = postSnapshot.child("rTitle").getValue(String.class);
                                String time = postSnapshot.child("rUpTime").getValue(String.class);
                                String userId = postSnapshot.child("uploadedBy").getValue(String.class);
                                String postId = postSnapshot.child("resourceID").getValue(String.class);
                                String resourceCount = String.valueOf(postSnapshot.child("downloadUrl").getChildrenCount());
                                String subject = postSnapshot.child("subject").getValue(String.class);
                                ResourceModel resourceModel = new ResourceModel(userId, postId, description, title, time, resourceCount, subject);
                                resourceModelList.add(resourceModel);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                resourceListAdapter = new ResourceListAdapter(resourceModelList, FilterResource.this);
                resourceListAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(resourceListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}

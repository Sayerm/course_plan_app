package com.example.coursePlan.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.coursePlan.Adapters.ResourceListAdapter;
import com.example.coursePlan.Models.ResourceModel;
import com.example.coursePlan.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ResourceFragment extends Fragment {

    private static final int PICK_FILE = 1;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<Uri> fileList = new ArrayList<Uri>();
    Button uploadFile, chooseFile, upVisBt, cancel;
    LinearLayout postLayout;
    TextView fileCount;
    TextInputEditText resourceType, resourceDescription;
    DatabaseReference reference;
    List<ResourceModel> resourceModelList;
    ResourceListAdapter resourceListAdapter;
    String select;
    SwipeRefreshLayout swipeRefreshLayout;
    String postKey;
    String type;
    String semester, semester2, subject;
    int semesterInt = 0;
    int semesterInt2 = 0;
    Spinner semesterSpinner, subjectSpinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList;
    Button button1, button2, button3, button4, button5, button6, button7, button8;
    private StorageReference mStorageRef;
     ProgressBar semesterLoader;
     HorizontalScrollView buttonView;

    public ResourceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_resource, container, false);

        resourceModelList = new ArrayList<>();
        arrayList = new ArrayList<>();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("Resource").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Resource");

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        button1 = view.findViewById(R.id.btn1);
        button2 = view.findViewById(R.id.btn2);
        button3 = view.findViewById(R.id.btn3);
        button4 = view.findViewById(R.id.btn4);
        button5 = view.findViewById(R.id.btn5);
        button6 = view.findViewById(R.id.btn6);
        button7 = view.findViewById(R.id.btn7);
        button8 = view.findViewById(R.id.btn8);
        semesterLoader=view.findViewById(R.id.semesterLoader);
        buttonView=view.findViewById(R.id.buttonView);

        final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                type = snapshot.child("type").getValue(String.class);
                semester = snapshot.child("semester_number").getValue(String.class);
                semesterLoader.setVisibility(View.GONE);
                buttonView.setVisibility(View.VISIBLE);
                try {
                    if (semester != null) {
                        if (semester.equals("1")) {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.GONE);
                            button3.setVisibility(View.GONE);
                            button4.setVisibility(View.GONE);
                            button5.setVisibility(View.GONE);
                            button6.setVisibility(View.GONE);
                            button7.setVisibility(View.GONE);
                            button8.setVisibility(View.GONE);
                        } else if (semester.equals("2")) {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.GONE);
                            button4.setVisibility(View.GONE);
                            button5.setVisibility(View.GONE);
                            button6.setVisibility(View.GONE);
                            button7.setVisibility(View.GONE);
                            button8.setVisibility(View.GONE);
                        } else if (semester.equals("3")) {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.VISIBLE);
                            button4.setVisibility(View.GONE);
                            button5.setVisibility(View.GONE);
                            button6.setVisibility(View.GONE);
                            button7.setVisibility(View.GONE);
                            button8.setVisibility(View.GONE);
                        } else if (semester.equals("4")) {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.VISIBLE);
                            button4.setVisibility(View.VISIBLE);
                            button5.setVisibility(View.GONE);
                            button6.setVisibility(View.GONE);
                            button7.setVisibility(View.GONE);
                            button8.setVisibility(View.GONE);
                        } else if (semester.equals("5")) {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.VISIBLE);
                            button4.setVisibility(View.VISIBLE);
                            button5.setVisibility(View.VISIBLE);
                            button6.setVisibility(View.GONE);
                            button7.setVisibility(View.GONE);
                            button8.setVisibility(View.GONE);
                        } else if (semester.equals("6")) {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.VISIBLE);
                            button4.setVisibility(View.VISIBLE);
                            button5.setVisibility(View.VISIBLE);
                            button6.setVisibility(View.VISIBLE);
                            button7.setVisibility(View.GONE);
                            button8.setVisibility(View.GONE);
                        } else if (semester.equals("7")) {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.VISIBLE);
                            button4.setVisibility(View.VISIBLE);
                            button5.setVisibility(View.VISIBLE);
                            button6.setVisibility(View.VISIBLE);
                            button7.setVisibility(View.VISIBLE);
                            button8.setVisibility(View.GONE);
                        } else if (semester.equals("8")) {
                            button1.setVisibility(View.VISIBLE);
                            button2.setVisibility(View.VISIBLE);
                            button3.setVisibility(View.VISIBLE);
                            button4.setVisibility(View.VISIBLE);
                            button5.setVisibility(View.VISIBLE);
                            button6.setVisibility(View.VISIBLE);
                            button7.setVisibility(View.VISIBLE);
                            button8.setVisibility(View.VISIBLE);
                        }
                    }

                }catch (Exception e){
                    Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                semesterLoader.setVisibility(View.GONE);
                buttonView.setVisibility(View.VISIBLE);
                error.toException().printStackTrace();

            }
        });



        uploadFile = view.findViewById(R.id.uploadFile);
        chooseFile = view.findViewById(R.id.chooseFile);
        upVisBt = view.findViewById(R.id.upBt);
        cancel = view.findViewById(R.id.cancel);
        postLayout = view.findViewById(R.id.postLt);
        resourceType = view.findViewById(R.id.resourceType);
        resourceDescription = view.findViewById(R.id.resourceDescription);
        fileCount = view.findViewById(R.id.fileCount);
        recyclerView = view.findViewById(R.id.resourceList);
        semesterSpinner = view.findViewById(R.id.semesterSpinner);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = view.findViewById(R.id.srl);
        subjectSpinner = view.findViewById(R.id.subjectSpinner);

        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semester2 = semesterSpinner.getSelectedItem().toString();

                adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, arrayList);
                subjectSpinner.setAdapter(adapter);
                if (position == 0) {
                    arrayList.clear();
                    Toast.makeText(getContext(), "Please select a semester", Toast.LENGTH_SHORT).show();
                } else if (position == 1) {
                    RetrieveSubject("1");
                } else if (position == 2) {
                    RetrieveSubject("2");
                } else if (position == 3) {
                    RetrieveSubject("3");
                } else if (position == 4) {
                    RetrieveSubject("4");
                } else if (position == 5) {
                    RetrieveSubject("5");
                } else if (position == 6) {
                    RetrieveSubject("6");
                } else if (position == 7) {
                    RetrieveSubject("7");
                } else if (position == 8) {
                    RetrieveSubject("8");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Toast.makeText(view.getContext(), "Please select a semester", Toast.LENGTH_SHORT).show();
            }
        });
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = subjectSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RetrieveResource(semester);
            }
        });

        upVisBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upVisBt.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                postLayout.setVisibility(View.VISIBLE);
                postLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upVisBt.setVisibility(View.VISIBLE);
                postLayout.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                postLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                String type = resourceType.getText().toString();
                String description = resourceDescription.getText().toString();
                if (semester2.equals("Select Semester")) {
                    Toast.makeText(getContext(), "Select a semester", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(type)) {
                    resourceType.setError("Enter your resource type");
                    resourceType.requestFocus();
                } else if (TextUtils.isEmpty(description)) {
                    resourceDescription.setError("Enter a description of your resource");
                } else if (TextUtils.isEmpty(select)) {
                    chooseFile.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    Toast.makeText(getContext(), "Add some resource", Toast.LENGTH_SHORT).show();
                } else {
                    postLayout.setVisibility(View.GONE);
                    upVisBt.setVisibility(View.VISIBLE);
                    UploadFile(type, description);
                }
            }
        });

        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseFile();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveResourceBySemester("1");
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveResourceBySemester("2");
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveResourceBySemester("3");
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveResourceBySemester("4");
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveResourceBySemester("5");
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveResourceBySemester("6");
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveResourceBySemester("7");
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveResourceBySemester("8");
            }
        });

        RetrieveResource(semester);

        return view;
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
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void RetrieveResource(String semester) {
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
                resourceListAdapter = new ResourceListAdapter(resourceModelList, getContext());
                resourceListAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(resourceListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                            if (type.equals("Student") && semesterInt == postSemesterInt) {
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
                resourceListAdapter = new ResourceListAdapter(resourceModelList, getContext());
                resourceListAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(resourceListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void ChooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_FILE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE) {
            if (resultCode == RESULT_OK) {
                if (data.getData() != null) {
                    select = " dj";
                    Uri file = data.getData();
                    fileList.add(file);
                    Toast.makeText(getContext(), "You have selected " + fileList.size() + " files", Toast.LENGTH_SHORT).show();
                    fileCount.setText(fileList.size() + " files selected");
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                } else {
                    select = "";
                    Toast.makeText(getContext(), "Please select a file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void UploadFile(String type, String description) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading your resource");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait while your resource upload");
        progressDialog.show();
        postKey = databaseReference.push().getKey();
        reference = databaseReference.child(postKey);


        HashMap<String, Object> map = new HashMap<>();
        map.put("resourceID", postKey);
        map.put("rUpTime", String.valueOf(System.currentTimeMillis()));
        map.put("rTitle", type);
        map.put("rDescription", description);
        map.put("status", "Pending");
        map.put("semester", semester2);
        map.put("subject", subject);
        map.put("uploadedBy", FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.updateChildren(map);

        for (int i = 0; i < fileList.size(); i++) {
            final Uri perFile = fileList.get(i);

            final String fileKey = reference.push().getKey();
            final StorageReference fieName = mStorageRef.child(getFileName(perFile) /*+ "." + getFileExtension(perFile)*/);
            fieName.putFile(perFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fieName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("fileUrl", String.valueOf(uri));
                            map.put("type", getFileExtension(perFile));
                            map.put("fileName", getFileName(perFile));
                            /*reference.child("downloadUrl").child(fileKey).setValue(map);*/
                            reference.child("downloadUrl").setValue(map);
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            fileList.clear();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = Objects.requireNonNull(getContext()).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
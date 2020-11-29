package com.example.coursePlan.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.coursePlan.Adapters.AnswersAdapter;
import com.example.coursePlan.Constans.GetTimeAgo;
import com.example.coursePlan.Models.AnswersModel;
import com.example.coursePlan.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.coursePlan.Constans.BaseUrl.BASE_URL;

public class FullResourceActivity extends AppCompatActivity {
    private static final int PERMISION_STORAGE = 1000;
    RecyclerView recyclerView;
    TextInputEditText answerEditText;
    TextInputLayout answerInputLayout;
    String questionId, ownerId, questionTime,title, description;
    String ownerName, answerDescription;
    String ownerProfile, answerOwnerId;
    TextView userNameTextView, timeTextView, titleTextView, descriptionTextView,resourceNameTextView;
    CircularImageView profileImageView;
    LinearLayout likeButton,answerButton,resourceLayout;
    ImageView likeView,answerView,followButton,download;
    TextView likeTV,answerTV,subjectTV;

    //-------FV==FROM VOLLEY--------
    String answerId, answerOwnerIdFV, questionIdFV, questionOwnerId, answerTime, answer, images, likes,intentType;
    List<AnswersModel> answersModelList;
    AnswersAdapter adapter;
    AVLoadingIndicatorView loadingIndicatorView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView uploadMsg;
    String subject;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    String fileName,fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_resource);

        answerOwnerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        answersModelList=new ArrayList<>();

        Intent intent = getIntent();
        questionId = intent.getStringExtra("question_id");
        ownerId = intent.getStringExtra("owner_id");
        questionTime = intent.getStringExtra("time");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        ownerName = intent.getStringExtra("name");
        ownerProfile = intent.getStringExtra("profile_image");
        intentType=intent.getStringExtra("type");
        subject=intent.getStringExtra("subject");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);


        subjectTV=findViewById(R.id.subjectTV);
        answerButton=findViewById(R.id.answerId);
        answerView=findViewById(R.id.answer);
        answerTV=findViewById(R.id.answerTv);
        likeButton=findViewById(R.id.likeId);
        likeView=findViewById(R.id.like);
        likeTV=findViewById(R.id.likeTv);
        swipeRefreshLayout=findViewById(R.id.srl);
        loadingIndicatorView=findViewById(R.id.loading);
        userNameTextView = findViewById(R.id.postProfileName);
        timeTextView = findViewById(R.id.postTime);
        titleTextView = findViewById(R.id.titleTV);
        descriptionTextView = findViewById(R.id.questionDescriptionTV);
        profileImageView = findViewById(R.id.postProfileImageView);
        answerEditText = findViewById(R.id.leaveAnAnswer);
        answerInputLayout = findViewById(R.id.textin);
        followButton=findViewById(R.id.followButton);
        uploadMsg=findViewById(R.id.uploadMsg);
        resourceLayout=findViewById(R.id.resourceLayout);
        resourceNameTextView=findViewById(R.id.resourceName);
        download=findViewById(R.id.downloadRes);

        subjectTV.setText(subject);
        if (subject==null){
            subjectTV.setVisibility(View.GONE);
        }
        answerInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PostAnswer();
//                RetrieveAns();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RetrieveAns();
                uploadMsg.setVisibility(View.GONE);
            }
        });
        uploadMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrieveAns();
                uploadMsg.setVisibility(View.GONE);
            }
        });

        recyclerView = findViewById(R.id.answersList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lManager=new LinearLayoutManager(this);
//        lManager.setReverseLayout(true);
        recyclerView.setLayoutManager(lManager);
        userNameTextView.setText(ownerName);
        timeTextView.setText(GetTimeAgo.getTimeAgo1(Long.parseLong(questionTime), getApplicationContext()));
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        Picasso.get().load(ownerProfile).placeholder(R.drawable.ic_man).into(profileImageView);

        if (ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            followButton.setVisibility(View.INVISIBLE);
        }


        isLoved(questionId,likeView);
        lovesCount(likeTV,questionId);

        isFollowed(FirebaseAuth.getInstance().getCurrentUser().getUid(), ownerId, followButton);

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followButton.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(ownerId).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue("Followed");
                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(ownerId)
                            .setValue("Following");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FullResourceActivity.this);
                    builder.setCancelable(false)
                            .setMessage("Do you want to unfollow?")
                            .setIcon(R.drawable.ic_round_exit_to_app_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(ownerId).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(ownerId)
                                            .removeValue();
                                }
                            })
                            .setNeutralButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = builder.create();

                    alert.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                        }
                    });

                    alert.show();

                }
            }
        });

        DatabaseReference databaseReference2=FirebaseDatabase.getInstance().getReference().child("Resource").child(questionId).child("downloadUrl");

        databaseReference2.keepSynced(true);
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String fileName2=snapshot.child("fileName").getValue(String.class);
                final String fileUrl2=snapshot.child("fileUrl").getValue(String.class);
                fileName=fileName2;
                fileUrl=fileUrl2;
                resourceNameTextView.setText(fileName2);
                resourceNameTextView.setPaintFlags(resourceNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){

                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                                    PackageManager.PERMISSION_DENIED){
                                String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};

                                requestPermissions(permissions,PERMISION_STORAGE);

                            }else {
                                StartDownloading(fileName2,fileUrl2);
                            }

                        }else {
                            StartDownloading(fileName2,fileUrl2);
                        }
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (likeView.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue("Liked");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();
                }
            }
        });

        answerCount(answerTV,questionId);

        RetrieveAns();
    }

    private void StartDownloading(String fileName, String fileUrl) {
        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(fileUrl));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI|DownloadManager.Request.NETWORK_MOBILE);

        request.setTitle(fileName);
        request.setDescription("Downloading...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("/Skill Developement Forum",""+fileName);

        DownloadManager manager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISION_STORAGE:{
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    StartDownloading(fileName,fileUrl);
                }else {
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void isFollowed(String myUserId, String userId, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FollowCount").child(userId).child("follower");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.RED));
                    }
                    imageView.setTag("Liked");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                    }
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void isLiked(String questionId, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LoveCount").child(questionId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.RED));
                    }
                    imageView.setTag("Liked");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                    }
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void isLoved(String questionId, final ImageView imageView){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId);

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.RED));
                    }
                    imageView.setTag("Liked");
                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                    }
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void likesCount(final TextView likes, String questionId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " Loves");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void lovesCount(final TextView likes, String questionId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId);
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" loves");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void RetrieveAns() {
        final TextView textView=findViewById(R.id.pls);
        textView.startAnimation(AnimationUtils.loadAnimation(FullResourceActivity.this,android.R.anim.fade_in));
        textView.setVisibility(View.VISIBLE);
        loadingIndicatorView.smoothToShow();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL+"retrieve_ans.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                answersModelList.clear();
                textView.startAnimation(AnimationUtils.loadAnimation(FullResourceActivity.this,android.R.anim.fade_out));
                textView.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                loadingIndicatorView.smoothToHide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("answers");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        questionIdFV = object.getString("question_id");
                        if (questionIdFV.equals(questionId)) {
                            answerId = object.getString("answer_id");
                            answerOwnerIdFV = object.getString("ans_owner_id");
                            answerTime = object.getString("ans_time");
                            answer = object.getString("answer");
//                            aDescription = object.getString("description");

                            AnswersModel answersModel=new AnswersModel(answerId,answerOwnerIdFV,questionIdFV,questionOwnerId,answerTime, answer,images,likes);
                            answersModelList.add(answersModel);
                        }

                    }
                    adapter=new AnswersAdapter(FullResourceActivity.this,answersModelList);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(FullResourceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FullResourceActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void answerCount(final TextView answerTv, String questionId) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("totalAnsCount").child(questionId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                answerTv.setText(snapshot.getChildrenCount()+" answers");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void commentCount(final TextView answerTv, String questionId) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("totalAnsCount").child(questionId);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                answerTv.setText(snapshot.getChildrenCount()+" comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    DatabaseReference databaseReference,total_ans_count;

    private void PostAnswer() {

        databaseReference= FirebaseDatabase.getInstance().getReference().child("ans_count").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        total_ans_count= FirebaseDatabase.getInstance().getReference().child("totalAnsCount").child(questionId);
        final String answerId=databaseReference.push().getKey();
        final String answerPush=total_ans_count.push().getKey();
        answerDescription = answerEditText.getText().toString();


        if (!TextUtils.isEmpty(answerDescription)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "post_dynamic_ans.php", new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
//                    Toast.makeText(FullPostActivity.this, response, Toast.LENGTH_SHORT).show();

                    answerEditText.setText("");
                    Map<String, Object> map=new HashMap<>();
                    map.put(answerId,"done");
                    databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            total_ans_count.child(answerPush).setValue(answerDescription).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(FullResourceActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                                    uploadMsg.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FullResourceActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    String time = String.valueOf(System.currentTimeMillis());

                    Map<String, String> map = new HashMap<>();
                    map.put("answer_id", answerId);
                    map.put("ans_owner_id", answerOwnerId);
                    map.put("ans_time", time);
                    map.put("question_id", questionId);
                    map.put("question_owner_id", ownerId);
                    map.put("answer", answerDescription);
                    map.put("title", title);
                    map.put("description", description);
                    map.put("question_time", questionTime);
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

}
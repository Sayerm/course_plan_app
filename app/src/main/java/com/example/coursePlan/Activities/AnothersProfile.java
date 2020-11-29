package com.example.coursePlan.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.coursePlan.Adapters.QuestionListAdapter;
import com.example.coursePlan.Models.PostsModel;
import com.example.coursePlan.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.coursePlan.Constans.BaseUrl.BASE_URL;

public class AnothersProfile extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    ProgressDialog progressDialog;
    StorageReference storageReference;
    QuestionListAdapter questionListAdapter;
    String questionId, ownerId, questionTime, title, description;
    String ansId, ansOwnerId, questId, quesOwnerId, qsTime, qTitle, qDescription;
    RecyclerView questionRecyclerView, ansRecyclerView;
    LinearLayout answersButton, questionsButton, teachersInfo, studentInfo, studyingAtLayout, batchLayout, followButton, followingButton, loveButton,
            studentIdLayout, graduatedFromLayout, expertInLayout, teachingAtLayout, teacherIdLayout, experienceOfLayout;
    TextView answersText, questionsText, answerCount, questionCount, userNameTextView, statusTextView, followCount, followingCount, loveCount,
            studyingAtTextView, batchTextView, studentIdTextView, graduatedFromTextView, expertInTextView,
            teachingAtTextView, teacherIdTextView, experienceOfTextView;
    String userName, status, studyingAt, batch, studentId, graduatedFrom, expertIn, teachingAt, teacherId, experienceOf, profileImage, type;
    DatabaseReference userDatabaseReference;
    CircularImageView profileImageView;
    ImageView followView, followingView, loveView;
    String Uid, name, profileImageIn;
    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout relativeLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView gotoChatImageView;
    List<PostsModel> postsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anothers_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        Uid = intent.getStringExtra("uid");
        name = intent.getStringExtra("name");
        profileImageIn = intent.getStringExtra("profileImage");

        progressDialog = new ProgressDialog(this);
        postsModelList = new ArrayList<>();
        shimmerFrameLayout =findViewById(R.id.shimmerContentView);

        relativeLayout = findViewById(R.id.rel);
//        relativeLayout.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.srl);

        storageReference = FirebaseStorage.getInstance().getReference("profile_image");
        ansRecyclerView = findViewById(R.id.aRV);
        questionRecyclerView = findViewById(R.id.qRV);
        ansRecyclerView.setHasFixedSize(true);
        questionRecyclerView.setHasFixedSize(true);
        ansRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileImageView = findViewById(R.id.profile_imageView_id);
        teachersInfo = findViewById(R.id.teachersInfo);
        studentInfo = findViewById(R.id.studentInfo);
        teachersInfo.setVisibility(View.GONE);
        studentInfo.setVisibility(View.GONE);
        questionsButton = findViewById(R.id.questionsBTLayout);
        answersButton = findViewById(R.id.answersBtLayout);
        answersText = findViewById(R.id.answertext);
        answerCount = findViewById(R.id.answerCount);
        questionsText = findViewById(R.id.questionText);
        questionCount = findViewById(R.id.questionCount);
        userNameTextView = findViewById(R.id.profileName);
        statusTextView = findViewById(R.id.profileStatus);
        studentIdTextView = findViewById(R.id.studentIdTV);
        graduatedFromTextView = findViewById(R.id.graduatedFromTV);
        expertInTextView = findViewById(R.id.expertInTV);
        teachingAtTextView = findViewById(R.id.teachingTV);
        teacherIdTextView = findViewById(R.id.teachersIdTV);
        experienceOfTextView = findViewById(R.id.experienceTV);
        studyingAtTextView = findViewById(R.id.studyingTV);
        batchTextView = findViewById(R.id.batchTV);
        studyingAtLayout = findViewById(R.id.studyingAtLayout);
        studentIdLayout = findViewById(R.id.studentIdLayout);
        batchLayout = findViewById(R.id.batchLayout);
        graduatedFromLayout = findViewById(R.id.graduatedLayout);
        expertInLayout = findViewById(R.id.expertinLayout);
        teacherIdLayout = findViewById(R.id.teachersIdLayout);
        teachingAtLayout = findViewById(R.id.teachingAtLayout);
        experienceOfLayout = findViewById(R.id.experienceLayout);
        followButton = findViewById(R.id.followBtn);
        followCount = findViewById(R.id.followCount);
        followView = findViewById(R.id.followView);
        followingButton = findViewById(R.id.followingButton);
        followingView = findViewById(R.id.followingView);
        followingCount = findViewById(R.id.followingCount);
        loveButton = findViewById(R.id.loveButton);
        loveView = findViewById(R.id.loveView);
        loveCount = findViewById(R.id.loveCount);
        gotoChatImageView=findViewById(R.id.goToChat);

        gotoChatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AnothersProfile.this,ChatActivity.class);
                intent.putExtra("uid",Uid);
                startActivity(intent);
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followView.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(Uid).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue("Followed");
                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(Uid)
                            .setValue("Following");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AnothersProfile.this);
                    builder.setCancelable(false)
                            .setMessage("Do you want to unfollow?")
                            .setIcon(R.drawable.ic_round_exit_to_app_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(Uid).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(Uid)
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

        loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loveView.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("LoveCount").child(Uid).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue("Love");
                } else {
                    FirebaseDatabase.getInstance().getReference().child("LoveCount").child(Uid).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();
                }
            }
        });

        followingCount(followingCount, Uid);
        loveCount(loveCount, Uid);
        isLoved(FirebaseAuth.getInstance().getCurrentUser().getUid(), Uid, loveView);

//        retrievingUserInfo(Uid);

        questionsButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                questionsButton.setBackgroundResource(R.drawable.rounded_selected);
                answersButton.setBackgroundResource(R.drawable.rounded_blue);
                questionsText.setTextColor(Color.BLACK);
                questionCount.setTextColor(Color.parseColor("#1F6ADD"));
                answersText.setTextColor(Color.WHITE);
                answerCount.setTextColor(Color.BLACK);
                /*FragmentManager fragManager = myContext.getSupportFragmentManager();
                fragManager.beginTransaction().addToBackStack(null).replace(R.id.fragmentQuestion, questionsFragment).commit();*/

                questionRecyclerView.setVisibility(View.VISIBLE);
                ansRecyclerView.setVisibility(View.GONE);
                RetrieveQuestion();

            }
        });

        RetrieveQuestion();

        answersButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                answersButton.setBackgroundResource(R.drawable.rounded_selected);
                questionsButton.setBackgroundResource(R.drawable.rounded_blue);
                answersText.setTextColor(Color.BLACK);
                answerCount.setTextColor(Color.parseColor("#1F6ADD"));
                questionsText.setTextColor(Color.WHITE);
                questionCount.setTextColor(Color.BLACK);

                /*FragmentManager fragManager = myContext.getSupportFragmentManager();
                fragManager.beginTransaction().addToBackStack(null).replace(R.id.fragmentQuestion, answersFragment).commit();*/

                ansRecyclerView.setVisibility(View.VISIBLE);
                questionRecyclerView.setVisibility(View.GONE);
                RetrieveAnswer();

            }
        });

        RetrieveAnswer();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RetrieveQuestion();
                RetrieveAnswer();
            }
        });


        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        userDatabaseReference.keepSynced(true);


        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userName = snapshot.child("name").getValue(String.class);
                    batch = snapshot.child("batch").getValue(String.class);
                    studentId = snapshot.child("student_id").getValue(String.class);
                    status = snapshot.child("status").getValue(String.class);
                    type = snapshot.child("type").getValue(String.class);
                    profileImage = snapshot.child("image").getValue(String.class);
                    studyingAt = snapshot.child("studyingAt").getValue(String.class);
                    graduatedFrom = snapshot.child("graduatedFrom").getValue(String.class);
                    expertIn = snapshot.child("expertIn").getValue(String.class);
                    teachingAt = snapshot.child("teachingAt").getValue(String.class);
                    teacherId = snapshot.child("teacherId").getValue(String.class);
                    experienceOf = snapshot.child("experience").getValue(String.class);
                    profileImage = snapshot.child("image").getValue(String.class);

                    if (!"Teacher".equals(type)) {
                        studentInfo.setVisibility(View.VISIBLE);
                        teachersInfo.setVisibility(View.GONE);
                        studentIdTextView.setText(studentId);
                        if (TextUtils.isEmpty(studyingAt)) {
//                            studyingAtTextView.setText(R.string.not_added);
                            studyingAtLayout.setVisibility(View.GONE);
                        } else {
                            studyingAtLayout.setVisibility(View.VISIBLE);
                            studyingAtTextView.setText(studyingAt);
                        }
                        batchTextView.setText(batch);
                    } else {
                        teachersInfo.setVisibility(View.VISIBLE);
                        studentInfo.setVisibility(View.GONE);

                        if (TextUtils.isEmpty(graduatedFrom)) {
                            graduatedFromLayout.setVisibility(View.GONE);
                        } else {
                            graduatedFromLayout.setVisibility(View.VISIBLE);
                            graduatedFromTextView.setText(graduatedFrom);
                        }
                        if (TextUtils.isEmpty(expertIn)) {
                            expertInLayout.setVisibility(View.GONE);
                        } else {
                            expertInLayout.setVisibility(View.VISIBLE);
                            expertInTextView.setText(expertIn);

                        }
                        if (TextUtils.isEmpty(teachingAt)) {
                            teachingAtLayout.setVisibility(View.GONE);
                        } else {
                            teachingAtLayout.setVisibility(View.VISIBLE);
                            teachingAtTextView.setText(teachingAt);
                        }
                        if (TextUtils.isEmpty(teacherId)) {
                            teacherIdLayout.setVisibility(View.GONE);
                        } else {
                            teacherIdLayout.setVisibility(View.VISIBLE);
                            teacherIdTextView.setText(teacherId);
                        }
                        if (TextUtils.isEmpty(experienceOf)) {
                            experienceOfLayout.setVisibility(View.GONE);
                        } else {
                            experienceOfLayout.setVisibility(View.VISIBLE);
                            experienceOfTextView.setText(experienceOf);
                        }

                    }
                    userNameTextView.setText(userName);
                    Objects.requireNonNull(getSupportActionBar()).setTitle(userName);
                    Picasso.get().load(profileImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_man).into(profileImageView,
                            new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_man).into(profileImageView);
                                }
                            });

                    if (TextUtils.isEmpty(status)) {
                        statusTextView.setText(R.string.default_status);
                    } else {
                        statusTextView.setText(status);
                    }


                } else {
                    Toast.makeText(AnothersProfile.this, "Snapshot null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isFollowed(FirebaseAuth.getInstance().getCurrentUser().getUid(), Uid, followView);
        followersCount(followCount, Uid);

    }

    private void isFollowed(String myUserId, String userId, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FollowCount").child(userId).child("follower");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.BLUE));
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

    private void followersCount(final TextView followTV, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FollowCount").child(userId).child("follower");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String followerCount = String.valueOf(snapshot.getChildrenCount());
                if (followerCount.equals("0")){
                    followTV.setText("");
                }else {
                    followTV.setText(followerCount + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isLoved(String myUserId, String userId, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LoveCount").child(userId);

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


    private void followingCount(final TextView followTV, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FollowCount").child(userId).child("following");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String followingCount = String.valueOf(snapshot.getChildrenCount());
                if (followingCount.equals("0")){
                    followTV.setText("");
                }else {
                    followTV.setText(followingCount + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loveCount(final TextView followTV, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("LoveCount").child(userId);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String loveCount = String.valueOf(snapshot.getChildrenCount());
                int loveCountInt=Integer.parseInt(loveCount);
                if (loveCount.equals("0")) {
                    followTV.setText("");
                }else if (loveCountInt>=1000 && loveCountInt<1000000){
                    followTV.setText(loveCountInt/1000+"K");
                }else if (loveCountInt>=1000000){
                    followTV.setText(loveCountInt/1000000+"M");
                }
                else {
                    followTV.setText(loveCount + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void RetrieveAnswer() {
        shimmerFrameLayout.showShimmer(true);
        DatabaseReference postItemReference = FirebaseDatabase.getInstance().getReference().child("ans_count").child(Uid);

        postItemReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String questionsCount = String.valueOf(snapshot.getChildrenCount());
                answerCount.setText("(" + questionsCount + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL + "retrieve_ans.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                postsModelList.clear();
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("answers");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        ansOwnerId = object.getString("ans_owner_id");
                        if (ansOwnerId.equals(Uid)) {
                            ansId = object.getString("answer_id");
                            questId = object.getString("question_id");
                            quesOwnerId = object.getString("question_owner_id");
                            qDescription = object.getString("description");
                            qsTime = object.getString("question_time");
                            qTitle = object.getString("title");

                            PostsModel postsModel = new PostsModel(questionId, quesOwnerId, questionTime, title, description);
                            postsModel.setAnswerId(ansId);
                            postsModelList.add(postsModel);

                        }
                    }
                    questionListAdapter = new QuestionListAdapter(AnothersProfile.this, postsModelList);
                    questionListAdapter.notifyDataSetChanged();
                    ansRecyclerView.setAdapter(questionListAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AnothersProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(AnothersProfile.this);
        requestQueue.add(stringRequest);

    }


    private void RetrieveQuestion() {

        shimmerFrameLayout.showShimmer(true);

        DatabaseReference postItemReference = FirebaseDatabase.getInstance().getReference().child("Post_Count").child(Uid);

        postItemReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String questionsCount = String.valueOf(snapshot.getChildrenCount());
                questionCount.setText("(" + questionsCount + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL + "retrieve_questions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                postsModelList.clear();
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("questions");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        ownerId = object.getString("owner_id");
                        if (ownerId.equals(Uid)) {
                            questionId = object.getString("ques_id");
                            questionTime = object.getString("time");
                            title = object.getString("title");
                            description = object.getString("description");
                            /*MyQuestionsModel postsModel = new MyQuestionsModel(questionId, ownerId, questionTime, title, description);
                            myQuestionsModelList.add(postsModel);*/
                            PostsModel postsModel = new PostsModel(questionId, ownerId, questionTime, title, description);
                            postsModelList.add(postsModel);

                        }
                    }
                    questionListAdapter = new QuestionListAdapter(AnothersProfile.this, postsModelList);
                    questionListAdapter.notifyDataSetChanged();
                    questionRecyclerView.setAdapter(questionListAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AnothersProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(AnothersProfile.this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }
}
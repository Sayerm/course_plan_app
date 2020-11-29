package com.example.coursePlan.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.coursePlan.Activities.UpdateProfileActivity;
import com.example.coursePlan.Adapters.ProfileAnswersAdapter;
import com.example.coursePlan.Adapters.ProfileQuestionsAdapter;
import com.example.coursePlan.Models.MyAnsModel;
import com.example.coursePlan.Models.MyQuestionsModel;
import com.example.coursePlan.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.coursePlan.Constans.BaseUrl.BASE_URL;

public class ProfileFragment extends Fragment {
    private static final int PICK_FILE_REQUEST = 1;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    ProfileQuestionsAdapter profileQuestionsAdapter;
    List<MyQuestionsModel> myQuestionsModelList;
    List<MyAnsModel> myAnsModelList;
    String questionId, ownerId, questionTime, title, description;
    String ansId, ansOwnerId, questId, quesOwnerId, qsTime, qTitle, qDescription;
    Uri path;
    RecyclerView questionRecyclerView, ansRecyclerView;
    LinearLayout answersButton, questionsButton, teachersInfo, studentInfo, studyingAtLayout, followingButton, loveButton, batchLayout,
            studentIdLayout, graduatedFromLayout, expertInLayout, teachingAtLayout, teacherIdLayout, experienceOfLayout, followButton;
    TextView answersText, questionsText, answerCount, questionCount, userNameTextView, statusTextView,
            studyingAtTextView, batchTextView, studentIdTextView, graduatedFromTextView, expertInTextView,
            teachingAtTextView, teacherIdTextView, experienceOfTextView;
    String userName, teacherStatus, studentStatus, studyingAt, batch, studentId, graduatedFrom, expertIn, teachingAt, teacherId, experienceOf, profileImage, type;
    DatabaseReference userDatabaseReference;
    CircularImageView profileImageView, updateProfileImageView;
    ImageView followView, followingView, loveView;
    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout relativeLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView followCount, followingCount, loveCount;
    String Uid;
    Button updateProfileButton;
    ProfileAnswersAdapter profileAnswersAdapter;
    private FragmentActivity myContext;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressDialog = new ProgressDialog(myContext);
        myQuestionsModelList = new ArrayList<>();
        myAnsModelList = new ArrayList<>();

        relativeLayout = view.findViewById(R.id.rel);
//        relativeLayout.setVisibility(View.GONE);
        swipeRefreshLayout = view.findViewById(R.id.srl);
        shimmerFrameLayout = view.findViewById(R.id.shimmerContentView);
        followButton = view.findViewById(R.id.followBtn);
        followCount = view.findViewById(R.id.followCount);
        followView = view.findViewById(R.id.followView);

        followingButton = view.findViewById(R.id.followingButton);
        followingView = view.findViewById(R.id.followingView);
        followingCount = view.findViewById(R.id.followingCount);
        loveButton = view.findViewById(R.id.loveButton);
        loveView = view.findViewById(R.id.loveView);
        loveCount = view.findViewById(R.id.loveCount);
        storageReference = FirebaseStorage.getInstance().getReference("profile_image");
        ansRecyclerView = view.findViewById(R.id.aRV);
        questionRecyclerView = view.findViewById(R.id.qRV);
        ansRecyclerView.setHasFixedSize(true);
        questionRecyclerView.setHasFixedSize(true);
        ansRecyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        profileImageView = view.findViewById(R.id.profile_imageView_id);
        updateProfileImageView = view.findViewById(R.id.updateProfileImage);
        teachersInfo = view.findViewById(R.id.teachersInfo);
        studentInfo = view.findViewById(R.id.studentInfo);
        teachersInfo.setVisibility(View.GONE);
        studentInfo.setVisibility(View.GONE);
        questionsButton = view.findViewById(R.id.questionsBTLayout);
        answersButton = view.findViewById(R.id.answersBtLayout);
        answersText = view.findViewById(R.id.answertext);
        answerCount = view.findViewById(R.id.answerCount);
        questionsText = view.findViewById(R.id.questionText);
        questionCount = view.findViewById(R.id.questionCount);
        userNameTextView = view.findViewById(R.id.profileName);
        statusTextView = view.findViewById(R.id.profileStatus);
        studentIdTextView = view.findViewById(R.id.studentIdTV);
        graduatedFromTextView = view.findViewById(R.id.graduatedFromTV);
        expertInTextView = view.findViewById(R.id.expertInTV);
        teachingAtTextView = view.findViewById(R.id.teachingTV);
        teacherIdTextView = view.findViewById(R.id.teachersIdTV);
        experienceOfTextView = view.findViewById(R.id.experienceTV);
        studyingAtTextView = view.findViewById(R.id.studyingTV);
        batchTextView = view.findViewById(R.id.batchTV);
        studyingAtLayout = view.findViewById(R.id.studyingAtLayout);
        studentIdLayout = view.findViewById(R.id.studentIdLayout);
        batchLayout = view.findViewById(R.id.batchLayout);
        graduatedFromLayout = view.findViewById(R.id.graduatedLayout);
        expertInLayout = view.findViewById(R.id.expertinLayout);
        teacherIdLayout = view.findViewById(R.id.teachersIdLayout);
        teachingAtLayout = view.findViewById(R.id.teachingAtLayout);
        experienceOfLayout = view.findViewById(R.id.experienceLayout);
        updateProfileButton = view.findViewById(R.id.updateProfileInfo);

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
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        databaseReference.keepSynced(true);

        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    userName = snapshot.child("name").getValue(String.class);
                    batch = snapshot.child("batch").getValue(String.class);
                    studentId = snapshot.child("student_id").getValue(String.class);
                    teacherStatus = snapshot.child("teachersStatus").getValue(String.class);
                    studentStatus = snapshot.child("studentStatus").getValue(String.class);
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

                    if (TextUtils.isEmpty(studentStatus) && TextUtils.isEmpty(studentStatus)) {
                        statusTextView.setText(R.string.default_status);
                    } else {
                        if (type.equals("Student")) {
                            statusTextView.setText(studentStatus);
                        } else if (type.equals("Teacher")) {
                            statusTextView.setText(teacherStatus);
                        }
                    }


                } else {
                    Toast.makeText(myContext, "Snapshot null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myContext, UpdateProfileActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("studyingAt", studyingAt);
                intent.putExtra("batch", batch);
                intent.putExtra("studentId", studentId);
                intent.putExtra("graduatedFrom", graduatedFrom);
                intent.putExtra("expertIn", expertIn);
                intent.putExtra("teachingAt", teachingAt);
                intent.putExtra("teacherId", teacherId);
                intent.putExtra("experience", experienceOf);
                intent.putExtra("ss", studentStatus);
                intent.putExtra("ts", teacherStatus);
                startActivity(intent);
            }
        });

        updateProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryIntent();

            }
        });


        return view;
    }

    private void isFollowed(String questionId, final ImageView imageView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AnsLikes").child(questionId);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
        followersCount(followCount, FirebaseAuth.getInstance().getCurrentUser().getUid());
        followingCount(followingCount, Uid);
        loveCount(loveCount, Uid);

    }

    private void followersCount(final TextView likes, String questionId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FollowerCount").child(questionId);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() == 0) {
                    likes.setText("");
                } else {
                    likes.setText(snapshot.getChildrenCount() + "");
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
                if (followingCount.equals("0")) {
                    followTV.setText("");
                } else {
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
                int loveCountInt = Integer.parseInt(loveCount);
                if (loveCount.equals("0")) {
                    followTV.setText("");
                } else if (loveCountInt >= 1000 && loveCountInt < 1000000) {
                    followTV.setText(loveCountInt / 1000 + "K");
                } else if (loveCountInt >= 1000000) {
                    followTV.setText(loveCountInt / 1000000 + "M");
                } else {
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
        DatabaseReference postItemReference = FirebaseDatabase.getInstance().getReference().child("ans_count").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
                myAnsModelList.clear();
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
                        if (ansOwnerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            ansId = object.getString("answer_id");
                            questId = object.getString("question_id");
                            quesOwnerId = object.getString("question_owner_id");
                            qDescription = object.getString("description");
                            qsTime = object.getString("question_time");
                            qTitle = object.getString("title");
                            MyAnsModel myAnsModel = new MyAnsModel();
                            myAnsModel.setQuestionId(questId);
                            myAnsModel.setQuestionOwnerId(quesOwnerId);
                            myAnsModel.setTitle(qTitle);
                            myAnsModel.setDescription(qDescription);
                            myAnsModel.setAnsId(ansId);
                            myAnsModel.setQuestionTime(qsTime);
                            myAnsModelList.add(myAnsModel);

                        }
                    }
                    profileAnswersAdapter = new ProfileAnswersAdapter(myAnsModelList, getContext());
                    profileAnswersAdapter.notifyDataSetChanged();
                    ansRecyclerView.setAdapter(profileAnswersAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        requestQueue.add(stringRequest);

    }

    private void RetrieveQuestion() {
        shimmerFrameLayout.showShimmer(true);

        DatabaseReference postItemReference = FirebaseDatabase.getInstance().getReference().child("Post_Count").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

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
                myQuestionsModelList.clear();
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
                        if (ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            questionId = object.getString("ques_id");
                            questionTime = object.getString("time");
                            title = object.getString("title");
                            description = object.getString("description");
                            MyQuestionsModel postsModel = new MyQuestionsModel(questionId, ownerId, questionTime, title, description);
                            myQuestionsModelList.add(postsModel);

                        }
                    }
                    profileQuestionsAdapter = new ProfileQuestionsAdapter(getContext(), myQuestionsModelList);
                    profileQuestionsAdapter.notifyDataSetChanged();
                    questionRecyclerView.setAdapter(profileQuestionsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        requestQueue.add(stringRequest);
    }


    private void galleryIntent() {
        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_GET_CONTENT);//
        Intent intent2 = CropImage.activity()
                .setScaleType(CropImageView.ScaleType.CENTER)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1)
                .getIntent(myContext);

        startActivityForResult(intent2, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            path = data.getData();
            CropImage.activity(path).setAspectRatio(1, 1)
                    .start(myContext);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog.setTitle("Uploading image...");
                progressDialog.setMessage("Please wait while uploading your image...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                final StorageReference filePath = storageReference.child(Uid + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();

                                Picasso.get().load(downloadUrl).into(profileImageView);

                                databaseReference.child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(myContext, "Profile picture Uploaded", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(myContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(myContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onAttach(@NotNull Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }

}
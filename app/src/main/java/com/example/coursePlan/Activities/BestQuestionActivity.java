package com.example.coursePlan.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.coursePlan.Constans.BaseUrl.BASE_URL;

public class BestQuestionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    QuestionListAdapter adapter;
    List<PostsModel> postsModelList;
    String questionId, ownerId, questionTime, title, description;
    String owner_id;
    DatabaseReference userReference;
    SwipeRefreshLayout swipeRefreshLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    RelativeLayout relativeLayout;
    String myType;
    String myBatch;
    String err;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_question);

        postsModelList = new ArrayList<>();
        owner_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("type");


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    myType = snapshot.getValue(String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("QUESTION_LIST_FRAGMENT", "onDataChange: " + e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        relativeLayout = findViewById(R.id.rel);
        shimmerFrameLayout = findViewById(R.id.shimmerContentView);
        relativeLayout.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.srl);
        recyclerView = findViewById(R.id.posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BestQuestionActivity.this));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RetrievePosts();
            }
        });
        RetrievePosts();
    }

    private void RetrievePosts() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("batch");
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myBatch = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        shimmerFrameLayout.showShimmer(true);
//        relativeLayout.setVisibility(View.INVISIBLE);
//        relativeLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL + "retrieve_questions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                postsModelList.clear();
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                try {
                    relativeLayout.startAnimation(AnimationUtils.loadAnimation(BestQuestionActivity.this, android.R.anim.fade_in));
                } catch (Exception e) {
                    Log.i("QUESTION_LIST_FRAGMENT", "onResponse: " + e.getMessage());
                }

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("questions");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        if (!TextUtils.isEmpty(object.getString("ques_id"))) {

                            if (Objects.equals(myType, "Admin")) {
                                if (object.getString("status").equals("Approved")) {
                                    {
                                        try {
                                            questionId = object.getString("ques_id");
                                            ownerId = object.getString("owner_id");
                                            questionTime = object.getString("time");
                                            title = object.getString("title");
                                            description = object.getString("description");
                                            err = "";
                                            PostsModel postsModel = new PostsModel(questionId, ownerId, questionTime, title, description);
                                            postsModel.setStatus("status");
                                            postsModelList.add(postsModel);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.i("QUESTION_LIST_FRAGMENT", "onResponse: " + e.getMessage());
                                        }

                                    }
                                }
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(err)) {
                        Toast.makeText(BestQuestionActivity.this, "Something went wrong. Please refresh", Toast.LENGTH_SHORT).show();
                        Log.i("QUESTION_LIST_FRAGMENT", "onResponse: " + "Something went wrong. Please refresh");
                    }
                    Collections.shuffle(postsModelList);
                    adapter = new QuestionListAdapter(BestQuestionActivity.this, postsModelList);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(BestQuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(BestQuestionActivity.this);
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
package com.example.coursePlan.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class QuestionsListFragment extends Fragment {

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
    String err, from, fromFinal;
    Context context;

    public QuestionsListFragment() {
        // Required empty public constructor
    }

    public QuestionsListFragment(Context context, String from) {
        this.context = context;
        this.from = from;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions_list, container, false);

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

        relativeLayout = view.findViewById(R.id.rel);
        shimmerFrameLayout = view.findViewById(R.id.shimmerContentView);
        relativeLayout.setVisibility(View.GONE);
        swipeRefreshLayout = view.findViewById(R.id.srl);
        recyclerView = view.findViewById(R.id.posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RetrievePosts(fromFinal);
            }
        });
        if (getArguments() != null) {
            fromFinal = getArguments().getString("from");
        }
        RetrievePosts(fromFinal);


        return view;
    }

    private void RetrievePosts(final String fromFinal) {

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
                    relativeLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                } catch (Exception e) {
                    Log.i("QUESTION_LIST_FRAGMENT", "onResponse: " + e.getMessage());
                }

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("questions");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        if (!TextUtils.isEmpty(object.getString("ques_id"))) {

                            if (Objects.equals(myType, "Admin") && fromFinal != null) {
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
                            } else {

                                if (object.getString("status").equals("Approved") && !Objects.equals(myType, "Admin")) {

                                    if (Objects.equals(myType, "Teacher")) {
                                        questionId = object.getString("ques_id");
                                        ownerId = object.getString("owner_id");
                                        questionTime = object.getString("time");
                                        title = object.getString("title");
                                        description = object.getString("description");
                                        PostsModel postsModel = new PostsModel(questionId, ownerId, questionTime, title, description);
                                        postsModel.setStatus(object.getString("status"));
                                        postsModelList.add(postsModel);
                                        err = "";
                                    } else if (Objects.equals(myType, "Student")) {
                                        try {
                                            if (Objects.equals(myBatch, object.getString("batch"))) {
                                                questionId = object.getString("ques_id");
                                                ownerId = object.getString("owner_id");
                                                questionTime = object.getString("time");
                                                title = object.getString("title");
                                                description = object.getString("description");
                                                err = "";
                                                PostsModel postsModel = new PostsModel(questionId, ownerId, questionTime, title, description);
                                                postsModel.setStatus(object.getString("status"));
                                                postsModelList.add(postsModel);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.i("QUESTION_LIST_FRAGMENT", "onResponse: " + e.getMessage());
                                        }

                                    } else {

                                        err = "not null";

                                    }

                                } else if (object.getString("status").equals("Pending") && Objects.equals(myType, "Admin")) {
                                    try {
                                        questionId = object.getString("ques_id");
                                        ownerId = object.getString("owner_id");
                                        questionTime = object.getString("time");
                                        title = object.getString("title");
                                        description = object.getString("description");
                                        err = "";
                                        PostsModel postsModel = new PostsModel(questionId, ownerId, questionTime, title, description);
                                        postsModel.setStatus(object.getString("status"));
                                        postsModelList.add(postsModel);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.i("QUESTION_LIST_FRAGMENT", "onResponse: " + e.getMessage());
                                    }

                                }
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(err)) {
                        Toast.makeText(getContext(), "Something went wrong. Please refresh", Toast.LENGTH_SHORT).show();
                        Log.i("QUESTION_LIST_FRAGMENT", "onResponse: " + "Something went wrong. Please refresh");
                    }
                    Collections.shuffle(postsModelList);
                    adapter = new QuestionListAdapter(getContext(), postsModelList);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
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
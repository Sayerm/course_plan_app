package com.example.coursePlan.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.coursePlan.Adapters.ProfileQuestionsAdapter;
import com.example.coursePlan.Models.MyQuestionsModel;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.coursePlan.Constans.BaseUrl.BASE_URL;

public class QuestionsFragment extends Fragment {

    RecyclerView recyclerView;
    List<MyQuestionsModel> myQuestionsModelList;
    ProfileQuestionsAdapter profileQuestionsAdapter;
    String questionId, ownerId, questionTime, title, description;

    public QuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions, container, false);

        myQuestionsModelList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.questionRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        profileQuestionsAdapter = new ProfileQuestionsAdapter(getContext(), myQuestionsModelList);
        RetrieveQuestions();
        recyclerView.setAdapter(profileQuestionsAdapter);

        return view;
    }

    private void RetrieveQuestions() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BASE_URL + "retrieve_questions.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                myQuestionsModelList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("questions");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        ownerId = object.getString("owner_id");
                        if (ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            questionId = object.getString("quest_id");
                            questionTime = object.getString("time");
                            title = object.getString("title");
                            description = object.getString("description");

                            MyQuestionsModel postsModel = new MyQuestionsModel(questionId, ownerId, questionTime, title, description);
                            myQuestionsModelList.add(postsModel);

                        }
                    }
                    profileQuestionsAdapter = new ProfileQuestionsAdapter(getContext(), myQuestionsModelList);
                    profileQuestionsAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(profileQuestionsAdapter);
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
}
package com.example.coursePlan.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.coursePlan.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.example.coursePlan.Constans.BaseUrl.BASE_URL;

public class AskQuestionFragment extends Fragment {

    TextInputEditText descriptionEditText;
    Button postButton;
    String description;
    QuestionsListFragment questionsListFragment = new QuestionsListFragment();
    DatabaseReference databaseReference, userDatabaseReference;
    String uid;
    Spinner titleSpinner;
    String titleTextFSpinner, myBatch;
    String[] type = {"Data Structure", "Algorithm", "Math 3", "Discrete Math", "Math 4",
            "Organizational Behaviour",  "Signal & System", "Neural Network",
            "Communication Engineering", "Neural Network", "Artificial Intelligent",
            "Computer Architecture", "Machine Learning"};
    private FragmentActivity myContext;

    public AskQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ask_question, container, false);

        titleSpinner = view.findViewById(R.id.userType);
        descriptionEditText = view.findViewById(R.id.qDescription);
        postButton = view.findViewById(R.id.uploadPost);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post_Count").child(uid);
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("batch");
        databaseReference.keepSynced(true);
        userDatabaseReference.keepSynced(true);

        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myBatch = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(myContext, R.layout.support_simple_spinner_dropdown_item, type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        titleSpinner.setAdapter(arrayAdapter);
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                titleTextFSpinner = titleSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = descriptionEditText.getText().toString().trim();
                if (TextUtils.isEmpty(description)) {
                    descriptionEditText.setError("Enter description of question");
                    descriptionEditText.requestFocus();
                } else {
                    UploadPost(titleTextFSpinner, description);
                    descriptionEditText.setText("");
                }
            }
        });

        return view;
    }

    private void UploadPost(final String titleTextFSpinner, final String description) {

        final String s = databaseReference.push().getKey();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "post_question_d.php", new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {

                Map<String, Object> map = new HashMap<>();
                map.put(s, "Posted");

                databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if (response.equals("{\"response\":\"Question submitted\"}")) {
                            Toast.makeText(myContext, "Posted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(myContext, "Posting failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(myContext, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(myContext, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String time = String.valueOf(System.currentTimeMillis());
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Map<String, String> map = new HashMap<>();
                map.put("ques_id", s);
                map.put("owner_id", uid);
                map.put("time", time);
                map.put("title", titleTextFSpinner);
                map.put("description", description);
                map.put("batch", myBatch);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(myContext);
        requestQueue.add(stringRequest);

        /*FragmentManager fragManager = myContext.getSupportFragmentManager();
        fragManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.bottom_nav_frame_layout, questionsListFragment).commit();
*/
    }

    @Override
    public void onAttach(@NotNull Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

}
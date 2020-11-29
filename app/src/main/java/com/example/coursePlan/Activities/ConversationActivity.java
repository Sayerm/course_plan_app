package com.example.coursePlan.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ConversationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    String currentUser;
    ConversationAdapter conversationAdapter;
    DatabaseReference conversation, messageDatabase, userDatabase;
    List<Conversation> conversationList;
    LinearLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convarsation);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser().getUid();

        conversationList = new ArrayList<>();

        conversation = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUser);
        messageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUser);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = findViewById(R.id.chat_fragment_recycler_id);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getApplicationContext());
//        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        getSupportActionBar().setTitle("Conversation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        recyclerView.setAdapter(new ConversationAdapter(view.getContext(),conversationList));

        Query conversationQuery=conversation.orderByChild("timeStamp");
        conversationQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                conversationList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    if (!Objects.equals(key, currentUser)) {
                        Conversation conversation = new Conversation();
                        conversation.setKey(key);
                        conversationList.add(conversation);
                    }

                }
                Collections.reverse(conversationList);
                conversationAdapter = new ConversationAdapter(getApplicationContext(), conversationList);
                conversationAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(conversationAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
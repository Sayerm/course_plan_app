package com.example.coursePlan.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "RETRIEVELOG";
    String image;
    String user_name, chatUser;
    ImageView backImageView, sendButton;
    TextView userNameTextView;
    EditText messageEditText;
    MessagesAdapter messagesAdapter;
    ChatAdapter chatAdapter;
    DatabaseReference userDatabase, rootReference;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    CircularImageView profileImageView;

    RecyclerView recyclerView;
    List<Messages> messagesList;
    LinearLayoutManager layoutManager;
    DatabaseReference messageReference, reference;
    ValueEventListener seenListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        chatUser = intent.getStringExtra("uid");
        user_name = intent.getStringExtra("name");

        firebaseAuth = FirebaseAuth.getInstance();

        currentUserId = firebaseAuth.getUid();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(chatUser);
        rootReference = FirebaseDatabase.getInstance().getReference();

        messageReference = FirebaseDatabase.getInstance().getReference().child("messages/" + currentUserId + "/" + chatUser);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        messagesList = new ArrayList<>();

        backImageView = findViewById(R.id.back);
        userNameTextView = findViewById(R.id.displayNameId);
        sendButton = findViewById(R.id.send);
        messageEditText = findViewById(R.id.content_id);
        recyclerView = findViewById(R.id.recyclerViewId);
        profileImageView=findViewById(R.id.topProfile);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        retrieveMessage();

        if (user_name == null) {
            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        String user_name2 = snapshot.child("name").getValue().toString();
                        userNameTextView.setText(user_name2);
//                        getSupportActionBar().setTitle(user_name2);
                    } catch (Exception e) {
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    image = snapshot.child("image").getValue(String.class);
                    Glide.with(getApplicationContext()).load(image).into(profileImageView);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            userNameTextView.setText(user_name);
//            getSupportActionBar().setTitle(user_name);
        }
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void retrieveMessage() {

        messageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();


                if (dataSnapshot.exists()) {
                    messagesList.clear();
                    for (DataSnapshot catSnapshot : dataSnapshot.getChildren()) {
                        if (dataSnapshot.exists()) {

//                        String name=catSnapshot.getKey();
                            String message = catSnapshot.child("message").getValue(String.class);
                            String seen = catSnapshot.child("seen").getValue(String.class);
                            String from = catSnapshot.child("from").getValue(String.class);
                            String profile = catSnapshot.child("profile").getValue(String.class);
                            try {
                                String timeLong = catSnapshot.child("time").getValue(String.class);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i(TAG, "onDataChange: " + e.getMessage());
                                Toast.makeText(ChatActivity.this, e.getStackTrace().toString(), Toast.LENGTH_SHORT).show();
                            }
//                            long timeLong = catSnapshot.child("time").getValue(long.class);
//                            String timeLong = catSnapshot.child("time").getValue(String.class);

                            String time = catSnapshot.child("timeStamp").getValue(String.class);
//                            String time2 = String.valueOf(timeLong);

                            String push_id = catSnapshot.getKey();
                            if (push_id != null) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("seen", "true");
//                                    messageReference.child(push_id).updateChildren(map);
//                                messageReference.child(push_id).child("seen").setValue("true");
                                Messages model = new Messages(message, from, time, seen);
                                messagesList.add(model);

                            } else {
//                            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                                Messages model = new Messages("Welcome to this app. Thanks for being a new member", currentUserId, "profile_image", "1599497112397");
                                messagesList.add(model);
                            }
                            DatabaseReference currentUserSeenReference = FirebaseDatabase.getInstance().getReference().child("isSeen?").child(currentUserId).child(chatUser);

                            currentUserSeenReference.child("seen").setValue("true");
                            final DatabaseReference chatUserSeenReference = FirebaseDatabase.getInstance().getReference().child("isSeen?").child(chatUser).child(currentUserId);
                        }

                    }
                }

                messagesAdapter = new MessagesAdapter(messagesList);
                chatAdapter = new ChatAdapter(messagesList, getApplicationContext());
                messagesAdapter.notifyDataSetChanged();
                chatAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage() {
        final String message = messageEditText.getText().toString();
        if (!TextUtils.isEmpty(message)) {

            final String current_user_ref = "messages/" + currentUserId + "/" + chatUser;
            final String current_user_chat_ref = "Chat/" + currentUserId + "/" + chatUser;
            final String chat_user_ref = "messages/" + chatUser + "/" + currentUserId;
            final String chat_user_chat_ref = "Chat/" + chatUser + "/" + currentUserId;

            DatabaseReference userMassagePush = rootReference.child(current_user_ref).push();
            final String push_id = userMassagePush.getKey();

            DatabaseReference currentUserSeenReference = FirebaseDatabase.getInstance().getReference().child("isSeen?").child(currentUserId).child(chatUser);
            DatabaseReference chatUserSeenReference = FirebaseDatabase.getInstance().getReference().child("isSeen?").child(chatUser).child(currentUserId);

            currentUserSeenReference.child("seen").setValue("false");
            chatUserSeenReference.child("seen").setValue("false");

            rootReference.child("Users").child(currentUserId).child("thumb_image").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(System.currentTimeMillis()));
                    String timeStamp1 = String.valueOf(System.currentTimeMillis());

                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("message", message);
                    messageMap.put("seen", "false");
                    messageMap.put("type", "text");
                    messageMap.put("timeStamp", timeStamp1);
                    messageMap.put("from", currentUserId);

                    Map<String, Object> messageUSerMap = new HashMap<>();
                    messageUSerMap.put(current_user_chat_ref + "/lastMessage", message);
//                    messageUSerMap.put(current_user_chat_ref + "/seen", true);
                    messageUSerMap.put(current_user_chat_ref + "/timeStamp", timeStamp1);
//                    messageUSerMap.put(current_user_chat_ref + "/seen", "true");
                    messageUSerMap.put(chat_user_chat_ref + "/lastMessage", message);
                    messageUSerMap.put(chat_user_chat_ref + "/timeStamp", timeStamp1);
//                    messageUSerMap.put(chat_user_chat_ref + "/seen", "false");
                    messageUSerMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUSerMap.put(chat_user_ref + "/" + push_id, messageMap);

                    rootReference.updateChildren(messageUSerMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Log.e("MESSAGE_LOG", "onComplete: " + error.getMessage().toString());
                            }
                        }
                    });
                    messageEditText.setText("");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


}
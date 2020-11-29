package com.example.coursePlan.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursePlan.Constans.GetTimeAgo;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    Context context;
    List<Conversation> conversationList;
    String currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ConversationAdapter(Context context, List<Conversation> conversationList) {
        this.context = context;
        this.conversationList = conversationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.conversation_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String uid = conversationList.get(position).getKey();

//        final boolean isSeen=conversationList.get(position).isSeen();
        final DatabaseReference lastMessageReference = FirebaseDatabase.getInstance().getReference().child("Chat/" + currentUserId + "/" + uid);
        final DatabaseReference seenReference = FirebaseDatabase.getInstance().getReference().child("isSeen?/" + currentUserId + "/" + uid);

        lastMessageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


//                String seen=snapshot.child("seen").getValue(String.class);
                String lastMessage=snapshot.child("lastMessage").getValue(String.class);
                try {
                    long lastActionTimeL= Long.parseLong(snapshot.child("timeStamp").getValue().toString());
                    String lastActionTime = GetTimeAgo.getTimeAgo1(lastActionTimeL, context);
                    holder.lastMessage.setText(lastMessage);
                    holder.lastActionTime.setText(lastActionTime);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }



         /*       seenReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String seen=snapshot.child("seen").getValue(String.class);
                        if (Objects.requireNonNull(seen).equals("false")){
//                            Toast.makeText(context, seenOrNot, Toast.LENGTH_SHORT).show();
                            holder.lastMessage.setTypeface(holder.lastMessage.getTypeface(),Typeface.BOLD);
                        }else if (seen.equals("true")){
//                            Toast.makeText(context, seenOrNot, Toast.LENGTH_SHORT).show();
                            holder.lastMessage.setTypeface(holder.lastMessage.getTypeface(),Typeface.NORMAL);
                        }else {
                            Toast.makeText(context, seen, Toast.LENGTH_SHORT).show();
//                            holder.lastActionTime.setText(snapshot.getValue().toString());
                            holder.lastMessage.setTypeface(holder.lastMessage.getTypeface(),Typeface.NORMAL);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (uid!=null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final String name = snapshot.child("name").getValue(String.class);
                    final String profile_image = snapshot.child("image").getValue(String.class);
                    holder.displayName.setText(name);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ChatActivity.class);
                            intent.putExtra("uid", uid);
                            context.startActivity(intent);
                        }
                    });
                    Picasso.get().load(profile_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_man).into(holder.profileImageView,
                            new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(profile_image).placeholder(R.drawable.ic_man).into(holder.profileImageView);
                                }
                            });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


//        holder.displayName.setText(uid);


    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView displayName, lastMessage,lastActionTime;
        CircularImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            displayName = itemView.findViewById(R.id.displayNameConversation);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            profileImageView = itemView.findViewById(R.id.profile_conversation);
            lastActionTime=itemView.findViewById(R.id.lastActionTimeTV);

        }
    }
}

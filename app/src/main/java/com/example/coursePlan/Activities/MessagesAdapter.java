package com.example.coursePlan.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursePlan.Constans.GetTimeAgo;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    Context context;
    List<Messages> messagesList;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String currentUserId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

    public MessagesAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_message_item, parent, false));
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Messages messages = messagesList.get(position);

        /*if (position==messagesList.size()-1){
            Conversation conversation=new Conversation();
            conversation.setSeen(messages.getSeen());
        }*/

        if (messagesList.size() == 0) {
            Toast.makeText(context, "Messagelist null", Toast.LENGTH_SHORT).show();
        } else {
            if (messages.getFrom() == currentUserId) {
                holder.messageTextView.setText(messages.getMessage());

//                holder.profileImageView.setVisibility(View.VISIBLE);
//                holder.messageTextView.setBackgroundColor(Color.BLACK);
                holder.messageTextView.setTextColor(Color.WHITE);
                holder.messageTextView.setBackgroundResource(R.drawable.my_text_background);
//                String lastActionTime = GetTimeAgo.getTimeAgo1(messages.getTime(), context);
//                holder.chatTime.setText(messages.getTime());
                holder.chatTime.setVisibility(View.GONE);
            } else if (!messages.getFrom().equals(currentUserId)) {
                holder.linearLayout.setGravity(Gravity.RIGHT);
                holder.messageTextView.setText(messages.getMessage());
                holder.messageTextView.setBackgroundResource(R.drawable.another_text_background);
//                final String profile_image = messages.getProfile();
                holder.messageTextView.setTextColor(Color.BLACK);

                try {
                    String lastActionTime = GetTimeAgo.getTimeAgo1(Long.parseLong(messages.getTime()), context);
                    holder.chatTime.setText(lastActionTime);
                } catch (Exception e) {
                    Log.i("DJIH", "onBindViewHolder: " + e.getMessage());
                }


            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, chatTime, seenTextView;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            linearLayout = itemView.findViewById(R.id.messageSingleLayout);
            messageTextView = itemView.findViewById(R.id.messagesText);
            chatTime = itemView.findViewById(R.id.chatTime);
//            seenTextView=itemView.findViewById(R.id.seen);

        }
    }
}

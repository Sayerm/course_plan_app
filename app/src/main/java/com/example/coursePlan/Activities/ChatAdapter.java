package com.example.coursePlan.Activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursePlan.Constans.GetTimeAgo;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "SOMETAG";
    private static int TYPE_NULL = 3;
    private static int TYPE_SEND = 1;
    private static int TYPE_RECEIVE = 2;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String currentUserId = firebaseAuth.getCurrentUser().getUid();
    List<Messages> messagesList;
    Context context;

    public ChatAdapter(List<Messages> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.send1, parent, false);
            return new SendMessageViewHolder(view);
        } else if (viewType == TYPE_RECEIVE) {
            return new ReceivedMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.recieve1, parent, false));
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages = messagesList.get(position);

        String message = messages.getMessage();
        String seen = messages.getSeen();
        String sendTime = messages.getTime();
//        String profile_image = messages.getProfile();


        SendMessageViewHolder sendMessageViewHolder = new SendMessageViewHolder(holder.itemView);
        ReceivedMessageViewHolder receivedMessageViewHolder = new ReceivedMessageViewHolder(holder.itemView);

        sendMessageViewHolder.setMessage(messages, position);
        receivedMessageViewHolder.setReceiveMsg(messages, position);
        try {
        if (position == messagesList.size() - 1) {
            if (seen.equals("true")) {
                sendMessageViewHolder.sendSeenTextView.setText(R.string.seen);
                sendMessageViewHolder.sendSeenTextView.setVisibility(View.VISIBLE);
            } else {

                    sendMessageViewHolder.sendSeenTextView.setText(R.string.delivered);
                    sendMessageViewHolder.sendSeenTextView.setVisibility(View.VISIBLE);

            }
        }
        }catch (Exception e){
            Log.i(TAG, "onBindViewHolder: "+e.getMessage());
        }

//        sendMessageViewHolder.sendMessageTextView.setText(messages.getMessage());
//        sendMessageViewHolder.setSendDetails(message, seen, sendTime, position);
//        receivedMessageViewHolder.setReceiveDetails(message, profile_image, sendTime);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getFrom().equals(currentUserId)) {
            return TYPE_SEND;

        } else if (messagesList.size() == 0) {
            return TYPE_NULL;
        } else {
            return TYPE_RECEIVE;
        }
    }

    private class SendMessageViewHolder extends RecyclerView.ViewHolder {

        TextView sendMessageTextView, sendSeenTextView, sendTimeTextView;
        String currentState = "unClicked";

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessageTextView = itemView.findViewById(R.id.txtName);
            sendTimeTextView = itemView.findViewById(R.id.txtAddress);
            sendSeenTextView = itemView.findViewById(R.id.txtSeen);

//            messageTextView.setText(messagesList.get.getMessage());

        }

        private void setMessage(Messages messages, int position) {
            sendMessageTextView.setText(messages.getMessage());

            String time = GetTimeAgo.getTimeAgo1(Long.parseLong(messages.getTime()), context);
            sendTimeTextView.setText(time);
            String seen = messages.getSeen();

            sendMessageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentState.equals("unClicked")) {
                        sendTimeTextView.setVisibility(View.VISIBLE);
                        currentState = "clicked";
                    } else if (currentState.equals("clicked")) {
                        sendTimeTextView.setVisibility(View.GONE);
                        currentState = "unClicked";
                    }
                }
            });
        }

        /*private void setSendDetails(String message, String seen, String sendTime, int position) {
            try {
//               messageTextView.setText(message);
               *//*
               }*//*
//               timeTextView.setText(sendTime);
            } catch (Exception e) {
                Log.i(TAG, "setSendDetails: " + e.getMessage());
            }
//            seenTextView.setText();

        }
*/
    }

    private class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timeTextView;
        String currentState = "unClicked";

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.txtName);
            timeTextView = itemView.findViewById(R.id.txtAddress);

//            messageTextView.setText(messagesList.get(getAdapterPosition()).getMessage());

        }

        private void setReceiveMsg(final Messages messages, int position) {
            messageTextView.setText(messages.getMessage());
            String time = GetTimeAgo.getTimeAgo1(Long.parseLong(messages.getTime()), context);
            timeTextView.setText(time);

            messageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentState.equals("unClicked")) {
                        timeTextView.setVisibility(View.VISIBLE);
                        currentState = "clicked";
                    } else if (currentState.equals("clicked")) {
                        timeTextView.setVisibility(View.GONE);
                        currentState = "unClicked";
                    }
                }
            });

            /*Picasso.get().load(messages.getProfile()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_man).into(profileImageView,
                    new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(messages.getProfile()).placeholder(R.drawable.ic_man).into(profileImageView);
                        }
                    });*/
        }

        /*private void setReceiveDetails(String message, final String profile_image, String sendTime) {
//            messageTextView.setText(message);
//            timeTextView.setText(sendTime);

        }*/
    }

    private class NullMessageViewHolder extends RecyclerView.ViewHolder {
        TextView welcomingTextView;

        public NullMessageViewHolder(@NonNull View itemView) {
            super(itemView);


        }

    }

}

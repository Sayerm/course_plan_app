package com.example.coursePlan.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursePlan.Activities.AnothersProfile;
import com.example.coursePlan.Activities.MyProfileActivity;
import com.example.coursePlan.Constans.GetTimeAgo;
import com.example.coursePlan.Models.AnswersModel;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswersViewHolder> {

    Context context;
    List<AnswersModel>answersModelList;

    public AnswersAdapter(Context context, List<AnswersModel> answersModelList) {
        this.context = context;
        this.answersModelList = answersModelList;
    }

    @NonNull
    @Override
    public AnswersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AnswersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.answers_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AnswersViewHolder holder, int position) {
        final String answerId,answerOwnerId,questionId,questionOwnerId,answerTime,description,images,likes;
        AnswersModel answersModel=answersModelList.get(position);
        answerId=answersModel.getAnswerId();
        answerOwnerId=answersModel.getAnswerOwnerId();
        questionId=answersModel.getQuestionId();
        questionOwnerId=answersModel.getQuestionOwnerId();
        answerTime=answersModel.getAnswerTime();
        description=answersModel.getDescription();
        images=answersModel.getImages();
        likes=answersModel.getLikes();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(answerOwnerId);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.answerItem.setVisibility(View.VISIBLE);
                    holder.answerItem.startAnimation(AnimationUtils.loadAnimation(context,android.R.anim.fade_in));
                    String ownerName = snapshot.child("name").getValue(String.class);
                    final String ownerProfile = snapshot.child("image").getValue(String.class);
                    holder.userName.setText(ownerName);
                    Picasso.get().load(ownerProfile).placeholder(R.drawable.ic_man).into(holder.profileImageView);
                    holder.setValue(answerId,answerOwnerId,questionId,questionOwnerId,answerTime,description,images,likes,ownerProfile,ownerName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.descriptionTextView.setText(description);
        holder.ansTime.setText(GetTimeAgo.getTimeAgo1(Long.parseLong(answerTime),context));
        isLiked(answerId,holder.upVoteView);
        likesCount(holder.upVoteCount,answerId);

        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerOwnerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent intent = new Intent(context, MyProfileActivity.class);
                    intent.putExtra("uid", answerOwnerId);
                    context.startActivity(intent);
                } else {

                    Intent intent = new Intent(context, AnothersProfile.class);
                    intent.putExtra("uid", answerOwnerId);
                    context.startActivity(intent);
                }
            }
        });
        if (answerOwnerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.followImageView.setVisibility(View.INVISIBLE);
        }


        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerOwnerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent intent = new Intent(context, MyProfileActivity.class);
                    intent.putExtra("uid", answerOwnerId);
                    context.startActivity(intent);
                } else {

                    Intent intent = new Intent(context, AnothersProfile.class);
                    intent.putExtra("uid", answerOwnerId);
                    context.startActivity(intent);
                }
            }
        });

        holder.upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.upVoteView.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("AnsLikes").child(answerId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue("Liked");
                }else {
                    FirebaseDatabase.getInstance().getReference().child("AnsLikes").child(answerId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();
                }
            }
        });

        isFollowed(FirebaseAuth.getInstance().getCurrentUser().getUid(), answerOwnerId, holder.followImageView);

        holder.followImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.followImageView.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(answerOwnerId).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue("Followed");
                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(answerOwnerId)
                            .setValue("Following");
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false)
                            .setMessage("Do you want to unfollow?")
                            .setIcon(R.drawable.ic_round_exit_to_app_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(answerOwnerId).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(answerOwnerId)
                                            .removeValue();
                                }
                            })
                            .setNeutralButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = builder.create();

                    alert.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                        }
                    });

                    alert.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return answersModelList.size();
    }

    public class AnswersViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView;
        LinearLayout answerItem,upVoteButton;
        TextView upVoteCount,userName,ansTime;
        ImageView upVoteView,followImageView;
        CircularImageView profileImageView;
        public AnswersViewHolder(@NonNull View itemView) {
            super(itemView);

            descriptionTextView=itemView.findViewById(R.id.ansDescriptionTV);
            upVoteCount=itemView.findViewById(R.id.ansUpVoteCount);
            userName=itemView.findViewById(R.id.ansProfileName);
            ansTime=itemView.findViewById(R.id.ansTime);
            upVoteView=itemView.findViewById(R.id.ansUpVoteView);
            followImageView=itemView.findViewById(R.id.ansFollowButton);
            profileImageView=itemView.findViewById(R.id.ansProfileImageView);
            answerItem=itemView.findViewById(R.id.ans_item);
            upVoteButton=itemView.findViewById(R.id.upVoteId);

        }

        public void setValue(String answerId, String answerOwnerId, String questionId, String questionOwnerId, String answerTime, String description, String images, String likes, String ownerProfile, String ownerName) {

        }
    }

    private void isLiked(String questionId, final ImageView imageView){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("AnsLikes").child(questionId);

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.BLUE));
                    }
                    imageView.setTag("Liked");
                }else{
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

    }

    private void likesCount(final TextView likes, String questionId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("AnsLikes").child(questionId);
        final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+" Vote");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void isFollowed(String myUserId, String userId, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FollowCount").child(userId).child("follower");

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

    }
}

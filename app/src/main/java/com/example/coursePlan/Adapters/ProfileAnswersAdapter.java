package com.example.coursePlan.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
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
import com.example.coursePlan.Activities.FullQuestionActivity;
import com.example.coursePlan.Activities.MyProfileActivity;
import com.example.coursePlan.Constans.GetTimeAgo;
import com.example.coursePlan.Models.MyAnsModel;
import com.example.coursePlan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ProfileAnswersAdapter extends RecyclerView.Adapter<ProfileAnswersAdapter.ViewHolder> {

    List<MyAnsModel> myAnsModelList;
    Context context;

    public ProfileAnswersAdapter(List<MyAnsModel> myAnsModelList, Context context) {
        this.myAnsModelList = myAnsModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.questions_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        try {
            final String questionId, ownerId, questionTime, title, description;
            MyAnsModel postsModel = myAnsModelList.get(position);
            questionId = postsModel.getQuestionId();
            ownerId = postsModel.getQuestionOwnerId();

            questionTime = postsModel.getQuestionTime();
            title = postsModel.getTitle();
            description = postsModel.getDescription();

            holder.questionTv.setVisibility(View.VISIBLE);
            holder.descriptionTv.setVisibility(View.VISIBLE);
            holder.questionTv.setText(title);
            holder.descriptionTv.setText(description);

            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(ownerId);
            userReference.keepSynced(true);

            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String ownerName = snapshot.child("name").getValue(String.class);
                        final String ownerProfile = snapshot.child("image").getValue(String.class);
                        holder.userNameTv.setVisibility(View.VISIBLE);
                        holder.profileImageView.setVisibility(View.VISIBLE);
                        holder.userNameTv.setText(ownerName);
                        Picasso.get().load(ownerProfile).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.ic_man).into(holder.profileImageView,
                                new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(ownerProfile).placeholder(R.drawable.ic_man).into(holder.profileImageView);
                                    }
                                });
                        holder.wholeItemId.setVisibility(View.VISIBLE);
                        holder.wholeItemId.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
                        holder.setValue(ownerId, questionId, questionTime, title, description, ownerName, ownerProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            try {
                holder.postTimeTv.setVisibility(View.VISIBLE);
                holder.postTimeTv.setText(GetTimeAgo.getTimeAgo1(Long.parseLong(String.valueOf(questionTime)), context));
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("QUESTION_LIST_ADAPTER", "onBindViewHolder: " + e.getMessage());
            }
            isLiked(questionId, holder.likeView);
            likesCount(holder.likeTv, questionId);


            holder.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.likeView.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue("Liked");
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();
                    }
                }
            });

            holder.answerCount(holder.answerTv,questionId);

            holder.profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Intent intent = new Intent(context, MyProfileActivity.class);
                        intent.putExtra("uid", ownerId);
                        context.startActivity(intent);
                    } else {

                        Intent intent = new Intent(context, AnothersProfile.class);
                        intent.putExtra("uid", ownerId);
                        context.startActivity(intent);
                    }
                }
            });

            if (ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                holder.followImageView.setVisibility(View.INVISIBLE);
            }


            holder.userNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Intent intent = new Intent(context, MyProfileActivity.class);
                        intent.putExtra("uid", ownerId);
                        context.startActivity(intent);
                    } else {

                        Intent intent = new Intent(context, AnothersProfile.class);
                        intent.putExtra("uid", ownerId);
                        context.startActivity(intent);
                    }
                }
            });
            isFollowed(FirebaseAuth.getInstance().getCurrentUser().getUid(), ownerId, holder.followImageView);

            holder.followImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.followImageView.getTag().equals("like")) {
                        FirebaseDatabase.getInstance().getReference().child("FollowCount").child(ownerId).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue("Followed");
                        FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(ownerId)
                                .setValue("Following");
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false)
                                .setMessage("Do you want to unfollow?")
                                .setIcon(R.drawable.ic_round_exit_to_app_24)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FirebaseDatabase.getInstance().getReference().child("FollowCount").child(ownerId).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .removeValue();
                                        FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(ownerId)
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

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public int getItemCount() {
        return myAnsModelList.size();
    }

    private void isLiked(String questionId, final ImageView imageView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

    private void likesCount(final TextView likes, String questionId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount() + " Votes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView likeTv, answerTv, shareTv, unlikeTv, seeTV, userNameTv, postTimeTv, questionTv, descriptionTv;
        ImageView likeView, answerView, shareView, unlikeView;
        LinearLayout likeButton, answerButton, shareButton, unlikeButton, wholeItemId;
        CircularImageView profileImageView;
        ImageView followImageView, moreImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wholeItemId = itemView.findViewById(R.id.wholeItemId);
            likeTv = itemView.findViewById(R.id.likeTv);
            likeView = itemView.findViewById(R.id.like);
            likeButton = itemView.findViewById(R.id.likeId);
            answerTv = itemView.findViewById(R.id.commentTv);
            answerView = itemView.findViewById(R.id.answer);
            answerButton = itemView.findViewById(R.id.answerId);
            shareTv = itemView.findViewById(R.id.shareTv);
            shareView = itemView.findViewById(R.id.share);
            shareButton = itemView.findViewById(R.id.shareId);
            unlikeTv = itemView.findViewById(R.id.unlikeTV);
            unlikeView = itemView.findViewById(R.id.unLike);
            unlikeButton = itemView.findViewById(R.id.unlikeId);
            userNameTv = itemView.findViewById(R.id.postProfileName);
            postTimeTv = itemView.findViewById(R.id.postTime);
            profileImageView = itemView.findViewById(R.id.postProfileImageView);
            followImageView = itemView.findViewById(R.id.followButton);
            moreImageView = itemView.findViewById(R.id.more);
            questionTv = itemView.findViewById(R.id.titleTV);
            descriptionTv = itemView.findViewById(R.id.questionDescriptionTV);

        }


        public void setValue(final String ownerId, final String questionId, final String questionTime, final String title, final String description, final String ownerName, final String ownerProfile) {
            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(v.getContext(), FullQuestionActivity.class);
                    intent.putExtra("name", ownerName);
                    intent.putExtra("profile_image", ownerProfile);
                    intent.putExtra("time", questionTime);
                    intent.putExtra("title", title);
                    intent.putExtra("description", description);
                    intent.putExtra("owner_id", ownerId);
                    intent.putExtra("question_id", questionId);
                    context.startActivity(intent);

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), FullQuestionActivity.class);
                    intent.putExtra("name", ownerName);
                    intent.putExtra("profile_image", ownerProfile);
                    intent.putExtra("time", questionTime);
                    intent.putExtra("title", title);
                    intent.putExtra("description", description);
                    intent.putExtra("owner_id", ownerId);
                    intent.putExtra("question_id", questionId);
                    context.startActivity(intent);
                }
            });
        }

        public void answerCount(final TextView answerTv, String questionId) {
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("totalAnsCount").child(questionId);
            reference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    answerTv.setText(snapshot.getChildrenCount()+" answers");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

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

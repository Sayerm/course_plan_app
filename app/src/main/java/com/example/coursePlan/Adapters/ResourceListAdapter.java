package com.example.coursePlan.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursePlan.Activities.AnothersProfile;
import com.example.coursePlan.Activities.FullResourceActivity;
import com.example.coursePlan.Activities.MyProfileActivity;
import com.example.coursePlan.Constans.GetTimeAgo;
import com.example.coursePlan.Models.ResourceModel;
import com.example.coursePlan.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceListAdapter extends RecyclerView.Adapter<ResourceListAdapter.ViewHolder> {

    List<ResourceModel> resourceModelList;
    Context context;

    public ResourceListAdapter(List<ResourceModel> resourceModelList, Context context) {
        this.resourceModelList = resourceModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.resources_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        try {
            final String resId, ownerId, resTime, title, description, resourceCount,subject;
            ResourceModel resourceModel = resourceModelList.get(position);
            resId = resourceModel.getPostId();
            ownerId = resourceModel.getUserId();
            resTime = resourceModel.getTime();
            title = resourceModel.getTitle();
            description = resourceModel.getDescription();
            resourceCount = resourceModel.getFileCount();
            subject=resourceModel.getSubject();

            holder.questionTv.setVisibility(View.VISIBLE);
            holder.descriptionTv.setVisibility(View.VISIBLE);
            holder.questionTv.setText(title);
            holder.descriptionTv.setText(description);
            holder.resCount.setText(resourceCount + " Resource Attached");
            holder.subjectTextView.setText(subject);
            if (subject==null){
                holder.subjectTextView.setVisibility(View.GONE);
            }

            if (resourceModel.getStatus()!=null){
                holder.approveLayout.setVisibility(View.VISIBLE);
                holder.likeCommentLayout.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            holder.approveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Object> map=new HashMap<String, Object>();
                    map.put("status","Approved");
                    FirebaseDatabase.getInstance().getReference().child("Resource").child(resId).updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error!=null){
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            holder.declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference().child("Resource").child(resId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Declined", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Something went wrong.Try again...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


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
                        holder.setValue(ownerId, resId, resTime, title, description, ownerName, ownerProfile,subject);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            try {
                holder.postTimeTv.setVisibility(View.VISIBLE);
                holder.postTimeTv.setText(GetTimeAgo.getTimeAgo1(Long.parseLong(String.valueOf(resTime)), context));
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("RESOURCE_LIST_ADAPTER", "onBindViewHolder: " + e.getMessage());
            }

            isLiked(resId, holder.likeView);
            likesCount(holder.likeTv, resId);

            holder.answerCount(holder.answerTv, resId);


            holder.likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (holder.likeView.getTag().equals("like")) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(resId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue("Liked");
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(resId).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .removeValue();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("RESOURCES_ADAPTER", "onClick: " + e.getMessage());
                    }

                }
            });

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

            if (ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                holder.followImageView.setVisibility(View.INVISIBLE);
            }

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

                        alert.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                            }
                        });

                        alert.show();


                    }
                }
            });

            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Resource").child(resId).child("downloadUrl");

            databaseReference2.keepSynced(true);
            databaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final String fileName = snapshot.child("fileName").getValue(String.class);
                    final String fileUrl = snapshot.child("fileUrl").getValue(String.class);
                    holder.resourceNameTextView.setText(fileName);
                    holder.resourceNameTextView.setPaintFlags(holder.resourceNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("QUESTIONS_ADAPTER", "onBindViewHolder: " + e.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return resourceModelList.size();
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

    private void isLiked(String questionId, final ImageView imageView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(questionId);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageTintList(ColorStateList.valueOf(Color.RED));
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
                likes.setText(snapshot.getChildrenCount() + " loves");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView likeTv, answerTv, shareTv, unlikeTv, userNameTv, postTimeTv, questionTv, descriptionTv, resCount, resourceNameTextView;
        ImageView likeView, answerView, shareView, unlikeView;
        LinearLayout likeButton, answerButton, shareButton, unlikeButton, wholeItemId;
        CircularImageView profileImageView;
        ImageView followImageView, moreImageView, download;
        LinearLayout approveLayout,likeCommentLayout;
        Button approveButton,declineButton;
        TextView subjectTextView;

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
            resCount = itemView.findViewById(R.id.resourceCountTV);
            resourceNameTextView = itemView.findViewById(R.id.resourceName);
            download = itemView.findViewById(R.id.downloadRes);
            approveLayout=itemView.findViewById(R.id.approvalLt);
            approveButton=itemView.findViewById(R.id.approveBt);
            declineButton=itemView.findViewById(R.id.declineBt);
            likeCommentLayout=itemView.findViewById(R.id.likeCommentLt);
            subjectTextView=itemView.findViewById(R.id.subjectTV);
        }


        public void setValue(final String ownerId, final String questionId, final String questionTime, final String title, final String description, final String ownerName, final String ownerProfile, final String subject) {
            answerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), FullResourceActivity.class);
                    intent.putExtra("name", ownerName);
                    intent.putExtra("profile_image", ownerProfile);
                    intent.putExtra("time", questionTime);
                    intent.putExtra("title", title);
                    intent.putExtra("description", description);
                    intent.putExtra("owner_id", ownerId);
                    intent.putExtra("question_id", questionId);
                    intent.putExtra("subject", subject);
                    intent.putExtra("type", "resource");
                    context.startActivity(intent);

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), FullResourceActivity.class);
                    intent.putExtra("name", ownerName);
                    intent.putExtra("profile_image", ownerProfile);
                    intent.putExtra("time", questionTime);
                    intent.putExtra("title", title);
                    intent.putExtra("description", description);
                    intent.putExtra("owner_id", ownerId);
                    intent.putExtra("question_id", questionId);
                    intent.putExtra("type", "resource");
                    intent.putExtra("subject", subject);
                    context.startActivity(intent);
                }
            });

        }

        public void answerCount(final TextView answerTv, String questionId) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("totalAnsCount").child(questionId);
            reference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    answerTv.setText(snapshot.getChildrenCount() + " answers");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }
}

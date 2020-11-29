package com.example.coursePlan.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coursePlan.Activities.AnothersProfile;
import com.example.coursePlan.Activities.MyProfileActivity;
import com.example.coursePlan.Models.Users;
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

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.ViewHolder> {

    Context context;
    List<Users>usersList;

    public AllUsersAdapter(Context context, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Users users=usersList.get(position);
        holder.nameTV.setText(users.getName());
        try {

            try {
                final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(users.getUserId());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String online=snapshot.child("online").getValue(String.class);
                            try {
                                if (online.equals("true")){
                                    holder.onlineStatus.setVisibility(View.VISIBLE);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

        String status=users.getStts();
        if (TextUtils.isEmpty(status)){
            holder.statusTV.setText("Hey check my profile in here");
        }else {
            holder.statusTV.setText(status);
        }
        }catch (Exception e){
            e.printStackTrace();
            Log.i("QUESTION_LIST_FRAGMENT", "onBindViewHolder: "+e.getMessage());
        }
        Picasso.get().load(users.getThumb_image()).placeholder(R.drawable.ic_man).into(holder.profileImageView);

        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ownerId=users.getUserId();

                if (ownerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Intent intent = new Intent(context, MyProfileActivity.class);
                    intent.putExtra("uid", ownerId);
                    intent.putExtra("name",users.getName());
                    context.startActivity(intent);
                } else {

                    Intent intent = new Intent(context, AnothersProfile.class);
                    intent.putExtra("uid", ownerId);
                    intent.putExtra("name",users.getName());
                    context.startActivity(intent);
                }

              /*  Intent intent=new Intent(context, AnothersProfile.class);
                intent.putExtra("uid",users.getUserId());
                intent.putExtra("name",users.getName());
                context.startActivity(intent);*/
            }
        });

        if (users.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.followButton.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, AnothersProfile.class);
                intent.putExtra("uid",users.getUserId());
                intent.putExtra("name",users.getName());
                context.startActivity(intent);
            }
        });

        isFollowed(FirebaseAuth.getInstance().getCurrentUser().getUid(),users.getUserId(),holder.followButton);

        holder.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.followButton.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(users.getUserId()).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue("Followed");
                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(users.getUserId())
                            .setValue("Following");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false)
                            .setMessage("Do you want to unfollow?")
                            .setIcon(R.drawable.ic_round_exit_to_app_24)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(users.getUserId()).child("follower").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("FollowCount").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following").child(users.getUserId())
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
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV, statusTV;
        ImageView followButton;
        CircularImageView profileImageView,onlineStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.users_item_name);
            statusTV = itemView.findViewById(R.id.users_item_status);
            profileImageView = itemView.findViewById(R.id.user_item_imageView);
            followButton = itemView.findViewById(R.id.followButton);
            onlineStatus=itemView.findViewById(R.id.onlineImg);
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
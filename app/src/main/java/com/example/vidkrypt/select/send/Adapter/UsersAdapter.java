package com.example.vidkrypt.select.send.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vidkrypt.R;
import com.example.vidkrypt.select.model.Users;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    Context context;
    ArrayList<Users> users;

    public UsersAdapter(Context context, ArrayList<Users> users) {
        this.context = context;
        this.users = users;
    }

    public UsersAdapter(){

    }


    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_items,parent,false);


        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
         Users user =users.get(position);
         holder.userName.setText(user.getUserName());
         if(user.getUserProfileImage().equals(""))
         {
             holder.imageProfile.setImageResource(R.drawable.account);
         }else
         {
             Glide.with(context).load(user.getUserProfileImage()).into(holder.imageProfile);
         }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView userName,lastMessage,date;
        CircleImageView imageProfile;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            userName =itemView.findViewById(R.id.userName);
            lastMessage =itemView.findViewById(R.id.lastMessage);
            date =itemView.findViewById(R.id.date_and_time);
            imageProfile =itemView.findViewById(R.id.profileImage);
        }
    }
}

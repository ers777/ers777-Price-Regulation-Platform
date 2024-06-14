package com.example.android_mas.users;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_mas.ChatActivity;
import com.example.android_mas.R;
import com.example.android_mas.utils.ChatUtil;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private ArrayList<User> users = new ArrayList<>();

    public UsersAdapter(ArrayList<User> users){
        this.users=users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peson_item_rv,parent,false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.username_tv.setText(user.username);

        if (!user.profileImage.isEmpty()){
            Glide.with(holder.itemView.getContext()).load(user.profileImage).into(holder.profileImage_iv);
        }

        holder.itemView.setOnClickListener(v -> {
            ChatUtil.createChat(user, chatId -> {
                Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                intent.putExtra("chatId", chatId);
                holder.itemView.getContext().startActivity(intent);
            });
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

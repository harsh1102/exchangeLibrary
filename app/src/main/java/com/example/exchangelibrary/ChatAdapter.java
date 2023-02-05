package com.example.exchangelibrary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    Context context;
    ArrayList<ChatMessage> messagesList;

    public ChatAdapter(Context context, ArrayList<ChatMessage> messagesList) {
        this.context = context;
        this.messagesList = messagesList;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.message,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChatMessage message = messagesList.get(position);
        holder.messageText.setText(message.getMessageText());
        holder.messageUser.setText(message.getMessageUser());
        holder.messageTime.setText(message.getMessageTime());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView messageText, messageUser, messageTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageText);
            messageUser = itemView.findViewById(R.id.messageUser);
            messageTime = itemView.findViewById(R.id.messageTime);
        }
    }
}

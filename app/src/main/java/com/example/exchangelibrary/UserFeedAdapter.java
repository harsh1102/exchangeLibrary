package com.example.exchangelibrary;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserFeedAdapter extends RecyclerView.Adapter<UserFeedAdapter.MyViewHolder> {
    Context context;
    ArrayList<PostFeed> postFeedsList;

    public UserFeedAdapter(Context context, ArrayList<PostFeed> postFeedsList) {
        this.context = context;
        this.postFeedsList = postFeedsList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_feed,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PostFeed post = postFeedsList.get(position);
        //holder.username.setText(post.getUsername());
        holder.title.setText(post.getTitle());
        holder.author.setText(post.getAuthor());
        Picasso.get().load(Uri.parse(post.getCoverPage())).into(holder.coverPage);
        holder.summary.setText(post.getSummary());
        holder.genre.setText(post.getGenre());
        holder.review.setText(post.getReview());
        holder.rating.setText(post.getRating());
        holder.status.setText(post.getStatus());
        holder.location.setText(post.getLocation());

    }

    @Override
    public int getItemCount() {
        return postFeedsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView username, title, author, summary, genre, review, rating, location, status;
        ImageView coverPage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            summary = itemView.findViewById(R.id.summary);
            genre = itemView.findViewById(R.id.genre);
            review = itemView.findViewById(R.id.review);
            rating = itemView.findViewById(R.id.rating);
            location = itemView.findViewById(R.id.location);
            coverPage = itemView.findViewById(R.id.book_img);
            status = itemView.findViewById(R.id.status);
        }
    }
}

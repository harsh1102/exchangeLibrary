package com.example.exchangelibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostFeedAdapter extends RecyclerView.Adapter<PostFeedAdapter.MyViewHolder> {
    Context context;
    ArrayList<PostFeed> postFeedsList;

    public PostFeedAdapter(Context context, ArrayList<PostFeed> postFeedsList) {
        this.context = context;
        this.postFeedsList = postFeedsList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_feed,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PostFeed post = postFeedsList.get(position);
        holder.username.setText(post.getUsername());
        holder.title.setText(post.getTitle());
        holder.author.setText(post.getAuthor());
        Picasso.get().load(Uri.parse(post.getCoverPage())).into(holder.coverPage);
        holder.summary.setText(post.getSummary());
        holder.genre.setText(post.getGenre());
        holder.review.setText(post.getReview());
        holder.rating.setText(post.getRating());
        holder.status.setText(post.getStatus());
        holder.location.setText(post.getLocation());
        holder.rate.setRating(Float.parseFloat(post.getRating()));

    }

    @Override
    public int getItemCount() {
        return postFeedsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView username, title, author, summary, genre, review, rating, location, status;
        RatingBar rate;
        Button btn;
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
            rate = itemView.findViewById(R.id.ratingBar);
            coverPage = itemView.findViewById(R.id.book_img);
            btn = itemView.findViewById(R.id.btnGet);
            status = itemView.findViewById(R.id.status);

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ProfileActivity.class);
                    intent.putExtra("USERNAME", username.getText().toString());
                    view.getContext().startActivity(intent);
                }
            });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int noofstars = rate.getNumStars();
                    float getrating = rate.getRating();
                    Toast.makeText(itemView.getContext(), "Rating: "+getrating+"/"+noofstars, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

package com.example.weatherapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.social.PostModel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class UserPostsAdapter extends RecyclerView.Adapter<UserPostsAdapter.PostViewHolder> {

    private List<PostModel> postList;
    private PostDeleteListener deleteListener;

    public interface PostDeleteListener {
        void onPostDelete(String postId);
    }

    public UserPostsAdapter(List<PostModel> postList, PostDeleteListener listener) {
        this.postList = postList;
        this.deleteListener = listener;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView locationName, weather, postText, timestamp;
        ImageButton btnDelete;

        public PostViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.textLocation);
            weather = itemView.findViewById(R.id.textWeather);
            postText = itemView.findViewById(R.id.textPost);
            timestamp = itemView.findViewById(R.id.textTimestamp);
            btnDelete = itemView.findViewById(R.id.btnDeletePost);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_item, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostModel post = postList.get(position);
        holder.locationName.setText("From " + post.getLocationName());
        holder.weather.setText(post.getWeather());
        holder.postText.setText(post.getText());

        com.google.firebase.Timestamp date = post.getTimestamp();
        Date postDateTime = date.toDate();
        Instant instant = postDateTime.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = localDateTime.format(formatter);
        holder.timestamp.setText(formattedDate);

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onPostDelete(post.getPostId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
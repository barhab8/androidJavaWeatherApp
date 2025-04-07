package com.example.weatherapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import com.example.weatherapp.data.model.PostModel;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<PostModel> postList;

    public PostsAdapter(List<PostModel> postList) {
        this.postList = postList;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName, locationName, weather, postText, timestamp;

        public PostViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textUserName);
            locationName = itemView.findViewById(R.id.textLocation);
            weather = itemView.findViewById(R.id.textWeather);
            postText = itemView.findViewById(R.id.textPost);
            timestamp = itemView.findViewById(R.id.textTimestamp);
        }
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostModel post = postList.get(position);
        holder.userName.setText(post.getUserName());
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
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}

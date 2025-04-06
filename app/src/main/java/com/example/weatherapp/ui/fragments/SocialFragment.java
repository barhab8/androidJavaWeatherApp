package com.example.weatherapp.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.weatherapp.R;
import com.example.weatherapp.data.model.PostModel;
import com.example.weatherapp.ui.adapters.PostsAdapter;
import com.example.weatherapp.ui.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment {

    private EditText editTextPost;
    private Button buttonPost;
    private RecyclerView recyclerViewPosts;
    private PostsAdapter adapter;
    private List<PostModel> postList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_social, container, false);

        editTextPost = root.findViewById(R.id.editTextPost);
        buttonPost = root.findViewById(R.id.buttonPost);
        recyclerViewPosts = root.findViewById(R.id.recyclerViewPosts);

        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostsAdapter(postList);
        recyclerViewPosts.setAdapter(adapter);

        buttonPost.setOnClickListener(v -> {
            String text = editTextPost.getText().toString().trim();
            if (!text.isEmpty()) {
                // Default values — can be enhanced with GPS or API!
                String locationName = "Unknown Location";
                String weather = "Unknown Weather";

                FirebaseUtils.submitWeatherPost(
                        getContext(),
                        text,
                        locationName,
                        weather,
                        () -> {
                            editTextPost.setText(""); // Clear after posting
                            loadPosts(); // Refresh list
                        },
                        () -> {
                            // Error handled in FirebaseUtils via Toast
                        }
                );
            }
        });

        loadPosts();

        return root;
    }

    private void loadPosts() {
        try {
            FirebaseUtils.loadWeatherPosts(getContext(), posts -> {
                postList.clear();
                postList.addAll(posts);
                adapter.notifyDataSetChanged();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.example.weatherapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.social.PostModel;
import com.example.weatherapp.ui.adapters.PostsAdapter;
import com.example.weatherapp.ui.dialogs.AddPostDialog;
import com.example.weatherapp.ui.utils.FirebaseUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends Fragment implements AddPostDialog.OnPostAddedListener {

    private RecyclerView recyclerViewPosts;
    private PostsAdapter adapter;
    private List<PostModel> postList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_social, container, false);

        // Initialize RecyclerView
        recyclerViewPosts = root.findViewById(R.id.recyclerViewPosts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostsAdapter(postList);
        recyclerViewPosts.setAdapter(adapter);

        FloatingActionButton fabAddPost = root.findViewById(R.id.fabAddPost);
        fabAddPost.setOnClickListener(v -> showAddPostDialog());

        // Load existing posts
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

    private void showAddPostDialog() {
        AddPostDialog dialog = new AddPostDialog(requireContext(), this);
        dialog.show();
    }

    @Override
    public void onPostAdded() {
        loadPosts();
    }
}
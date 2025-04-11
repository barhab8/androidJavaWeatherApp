package com.example.weatherapp.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.data.social.PostModel;
import com.example.weatherapp.ui.activitys.AuthActivity;
import com.example.weatherapp.ui.adapters.UserPostsAdapter;
import com.example.weatherapp.ui.utils.FirebaseUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsFragment extends Fragment implements UserPostsAdapter.PostDeleteListener {

    private TextInputEditText etFirstName, etLastName, tvEmail;
    private MaterialButton btnLogout, btnSave;
    private RecyclerView recyclerUserPosts;
    private UserPostsAdapter postsAdapter;
    private List<PostModel> userPosts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnSave = view.findViewById(R.id.btnSave);
        recyclerUserPosts = view.findViewById(R.id.recyclerUserPosts);
        // Setup RecyclerView
        recyclerUserPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postsAdapter = new UserPostsAdapter(userPosts, this);
        recyclerUserPosts.setAdapter(postsAdapter);

        if (FirebaseUtils.getCurrentUser() != null) {
            String userId = FirebaseUtils.getCurrentUserId(requireContext());
            if (userId != null) {
                fetchUserDetails(userId);
                loadUserPosts(userId);
            }
        } else {
            Toast.makeText(requireContext(), "No user is logged in.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }

        btnSave.setOnClickListener(v -> {
            String updatedFirstName = etFirstName.getText().toString().trim();
            String updatedLastName = etLastName.getText().toString().trim();

            if (updatedFirstName.isEmpty() || updatedLastName.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserDetails(updatedFirstName, updatedLastName);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            FirebaseUtils.logoutUser(requireContext(), this::redirectToLogin);
        });

        return view;
    }

    private void fetchUserDetails(String userId) {
        FirebaseUtils.getUserDetails(requireContext(), userId,
                (firstName, lastName, email) -> {
                    etFirstName.setText(firstName);
                    etLastName.setText(lastName);
                    tvEmail.setText(email);
                },
                errorMessage -> {
                    Log.e("UserDetailsFragment", "Error fetching user details: " + errorMessage);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void loadUserPosts(String userId) {
        FirebaseUtils.getUserPosts(requireContext(), userId, new FirebaseUtils.WeatherPostsCallback() {
            @Override
            public void onSuccess(List<PostModel> posts) {
                userPosts.clear();
                if (posts.isEmpty()) {
                    recyclerUserPosts.setVisibility(View.GONE);
                } else {
                    recyclerUserPosts.setVisibility(View.VISIBLE);
                    userPosts.addAll(posts);
                }
                postsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateUserDetails(String firstName, String lastName) {
        String userId = FirebaseUtils.getCurrentUserId(requireContext());
        if (userId == null) return;

        FirebaseUtils.updateUserDetails(requireContext(), userId, firstName, lastName,
                () -> Toast.makeText(requireContext(), "Details updated successfully.", Toast.LENGTH_SHORT).show(),
                errorMessage -> {
                    Log.e("UserDetailsFragment", "Error updating user details: " + errorMessage);
                    Toast.makeText(requireContext(), "Failed to update details: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), AuthActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onPostDelete(String postId) {
        String userId = FirebaseUtils.getCurrentUserId(requireContext());
        if (userId == null) return;

        FirebaseUtils.deleteUserPost(requireContext(), postId, () -> {
            Toast.makeText(requireContext(), "Post deleted successfully", Toast.LENGTH_SHORT).show();
            loadUserPosts(userId); // Reload the posts after deletion
        }, errorMessage -> {
            Toast.makeText(requireContext(), "Failed to delete post: " + errorMessage, Toast.LENGTH_SHORT).show();
        });
    }
}
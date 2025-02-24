package com.example.weatherapp.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.ui.activitys.LoginActivity;
import com.example.weatherapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDetailsFragment extends Fragment {

    private TextView tvWelcome, tvFirstName, tvLastName;
    private EditText etFirstName, etLastName;
    private Button btnLogout, btnSave;

    private FirebaseAuth fbAuth;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvLastName = view.findViewById(R.id.tvLastName);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnSave = view.findViewById(R.id.btnSave);

        fbAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String userId = fbAuth.getCurrentUser() != null ? fbAuth.getCurrentUser().getUid() : null;

        if (userId != null) {
            fetchUserDetails(userId);
        } else {
            Toast.makeText(requireContext(), "No user is logged in.", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }

        btnSave.setOnClickListener(v -> {
            String updatedFirstName = etFirstName.getText().toString().trim();
            String updatedLastName = etLastName.getText().toString().trim();

            if (updatedFirstName.isEmpty() || updatedLastName.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in both fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            updateUserDetails(userId, updatedFirstName, updatedLastName);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", requireContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            fbAuth.signOut();
            redirectToLogin();
        });

        return view;
    }

    private void fetchUserDetails(String userId) {
        firestore.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");

                        tvFirstName.setText("First Name: " + firstName);
                        tvLastName.setText("Last Name: " + lastName);
                        tvWelcome.setText("Welcome, " + firstName + "!");

                        etFirstName.setText(firstName);
                        etLastName.setText(lastName);
                    } else {
                        Toast.makeText(requireContext(), "User details not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDetailsFragment", "Error fetching user details", e);
                    Toast.makeText(requireContext(), "Failed to load user details.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserDetails(String userId, String firstName, String lastName) {
        firestore.collection("users").document(userId)
                .update("firstName", firstName, "lastName", lastName)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Details updated successfully.", Toast.LENGTH_SHORT).show();
                    tvFirstName.setText("First Name: " + firstName);
                    tvLastName.setText("Last Name: " + lastName);
                    tvWelcome.setText("Welcome, " + firstName + "!");
                })
                .addOnFailureListener(e -> {
                    Log.e("UserDetailsFragment", "Error updating user details", e);
                    Toast.makeText(requireContext(), "Failed to update details.", Toast.LENGTH_SHORT).show();
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}

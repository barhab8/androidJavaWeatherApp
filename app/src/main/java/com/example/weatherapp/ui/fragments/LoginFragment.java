package com.example.weatherapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.activitys.AuthActivity;

public class LoginFragment extends Fragment {

    private EditText etLoginEmail, etLoginPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etLoginEmail = view.findViewById(R.id.etLoginEmail);
        etLoginPassword = view.findViewById(R.id.etLoginPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                etLoginEmail.setError("Required");
                etLoginPassword.setError("Required");
                return;
            }

            // Call login method from AuthActivity
            ((AuthActivity) requireActivity()).loginUser(email, password);
        });

        tvForgotPassword.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString().trim();
            if (email.isEmpty()) {
                etLoginEmail.setError("Enter your email first");
                return;
            }

            // Call password recovery method from AuthActivity
            ((AuthActivity) requireActivity()).recoverPassword(email);
        });

        return view;
    }
}

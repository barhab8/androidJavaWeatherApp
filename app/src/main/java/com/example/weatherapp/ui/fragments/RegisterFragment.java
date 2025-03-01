package com.example.weatherapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.activitys.AuthActivity;

public class RegisterFragment extends Fragment {

    private EditText etRegisterFname, etRegisterLname, etRegisterEmail, etRegisterPassword, etRegisterConfirmPassword;
    private Button btnRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etRegisterFname = view.findViewById(R.id.etRegisterFname);
        etRegisterLname = view.findViewById(R.id.etRegisterLname);
        etRegisterEmail = view.findViewById(R.id.etRegisterEmail);
        etRegisterPassword = view.findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = view.findViewById(R.id.etRegisterConfirmPassword);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String fname = etRegisterFname.getText().toString().trim();
            String lname = etRegisterLname.getText().toString().trim();
            String email = etRegisterEmail.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();
            String confirmPassword = etRegisterConfirmPassword.getText().toString().trim();

            if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call sign-up method from AuthActivity
            ((AuthActivity) requireActivity()).signUpUser(fname, lname, email, password);
        });

        return view;
    }
}

package com.example.weatherapp.ui.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.MyUser;
import com.example.weatherapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText etRegisterEmail, etRegisterPassword, etRegisterFname, etRegisterLname;
    Button btnRegister;
    TextView tvLoginPrompt;
    FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterFname = findViewById(R.id.etRegisterFname);
        etRegisterLname = findViewById(R.id.etRegisterLname);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginPrompt = findViewById(R.id.tvLoginPrompt);

        fbAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(view -> {
            String email = etRegisterEmail.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();
            String fname = etRegisterFname.getText().toString().trim();
            String lname = etRegisterLname.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || fname.isEmpty() || lname.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            fbAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseFirestore store = FirebaseFirestore.getInstance();
                            String userId = fbAuth.getCurrentUser().getUid();
                            MyUser user = new MyUser(fname, lname);

                            store.collection("users").document(userId).set(user)
                                    .addOnSuccessListener(unused -> {
                                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.apply();

                                        Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainScreenActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Error saving user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(RegisterActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        setupLoginLink();
    }

    private void setupLoginLink() {
        SpannableString spannableString = new SpannableString("Have an account? Login here.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };
        spannableString.setSpan(clickableSpan, 17, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLoginPrompt.setText(spannableString);
        tvLoginPrompt.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

package com.example.weatherapp.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.fragments.LoginFragment;
import com.example.weatherapp.ui.fragments.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private Button btnSwitch;
    private boolean isLogin = true;
    private FirebaseAuth fbAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        btnSwitch = findViewById(R.id.btn_switch);
        fbAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnSwitch.setOnClickListener(v -> toggleFragment());

        // If user is already logged in, go to main activity
        if (fbAuth.getCurrentUser() != null) {
            startActivity(new Intent(AuthActivity.this, MainScreenActivity.class));
            finish();
            return;
        }

        // Load login fragment initially
        toggleFragment();
    }

    private void toggleFragment() {
        Fragment fragment;
        if (isLogin) {
            fragment = new RegisterFragment();
            btnSwitch.setText("Switch to Login");
            ((TextView)findViewById(R.id.tvLoginSignup)).setText("SIGN UP");
        } else {
            fragment = new LoginFragment();
            btnSwitch.setText("Switch to Sign Up");
            ((TextView)findViewById(R.id.tvLoginSignup)).setText("LOGIN");
        }
        isLogin = !isLogin;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    public void signUpUser(String fname, String lname, String email, String password) {
        fbAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = fbAuth.getCurrentUser();
                        if (user != null) {
                            // Save user details in Firestore
                            String userId = user.getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("firstName", fname);
                            userData.put("lastName", lname);
                            userData.put("email", email);

                            firestore.collection("users").document(userId).set(userData)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(AuthActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(AuthActivity.this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                        }
                    } else {
                        Toast.makeText(AuthActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void loginUser(String email, String password) {
        fbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AuthActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AuthActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void recoverPassword(String email) {
        fbAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> Toast.makeText(AuthActivity.this, "Password reset email sent!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(AuthActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}

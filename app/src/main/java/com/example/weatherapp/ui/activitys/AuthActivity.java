package com.example.weatherapp.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.fragments.LoginFragment;
import com.example.weatherapp.ui.fragments.RegisterFragment;
import com.example.weatherapp.ui.utils.FirebaseUtils;

public class AuthActivity extends AppCompatActivity {

    private Button btnSwitch;
    private boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        btnSwitch = findViewById(R.id.btn_switch);

        btnSwitch.setOnClickListener(v -> toggleFragment());

        if (FirebaseUtils.getCurrentUser() != null) {
            startActivity(new Intent(AuthActivity.this, MainScreenActivity.class));
            finish();
            return;
        }

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
        FirebaseUtils.signUpUser(
                this,
                fname,
                lname,
                email,
                password,
                () -> {
                    startActivity(new Intent(AuthActivity.this, MainScreenActivity.class));
                    finish();
                },
                () -> {} // toast handled in utils
        );
    }

    public void loginUser(String email, String password) {
        FirebaseUtils.loginUser(
                this,
                email,
                password,
                () -> {
                    startActivity(new Intent(AuthActivity.this, MainScreenActivity.class));
                    finish();
                },
                () -> {} // toast handled in utils
        );
    }

    public void recoverPassword(String email) {
        FirebaseUtils.recoverPassword(this, email);
    }
}

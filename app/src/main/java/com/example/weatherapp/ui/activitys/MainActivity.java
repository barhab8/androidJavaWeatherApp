package com.example.weatherapp.ui.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;

import com.example.weatherapp.R;
import com.example.weatherapp.ui.fragments.LoginFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnMainRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent;
            intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViews();
        btnMainRegister.setOnClickListener(this);
    }

    private void findViews() {
        btnMainRegister = findViewById(R.id.btnMainRegister);
    }

    @Override
    public void onClick(View view) {
        if (view == btnMainRegister) {
            Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
            startActivity(intent);
        }
    }
}

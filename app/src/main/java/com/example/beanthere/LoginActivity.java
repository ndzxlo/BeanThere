package com.example.beanthere;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beanthere.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v->{
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        });

    }

}
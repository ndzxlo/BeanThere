package com.example.beanthere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.beanthere.databinding.ActivitySignupBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button button = findViewById(R.id.signUpButton);

        button.setOnClickListener(v->{
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        });
    }
}

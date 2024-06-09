package com.example.beanthere;

import com.example.beanthere.network.supaBaseClient;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.util.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beanthere.databinding.ActivityLoginBinding;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v->{
            loginUSer();
        });

    }

    private void loginUSer(){

        String email = binding.editTextTextEmailAddress.getText().toString();
        String password = binding.editTextTextPassword.getText().toString();


        supaBaseClient.loginUser(email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread( () -> Log.e("LOGIN", "Login failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    runOnUiThread(()->Log.i("LOGIN", "Login successful"));
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else{
                    runOnUiThread(()-> Log.e("LOGIN", "Login failed: " + response.message()));
                }
            }
        });
    }

}
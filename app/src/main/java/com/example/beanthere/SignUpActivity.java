package com.example.beanthere;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.util.*;

import com.example.beanthere.network.supaBaseClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beanthere.databinding.ActivitySignupBinding;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUpButton.setOnClickListener(v -> signUp());

        binding.redirectButton.setOnClickListener(v->
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class)) );
    }

    private void signUp() {
        String name = binding.signUpName.getText().toString();
        String email = binding.signUpEmailAddress.getText().toString();
        String password = binding.signUpPassword.getText().toString();
        String confirmPassword = binding.signUpConfirmPassword.getText().toString();

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        supaBaseClient.registerUser(name, email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.e("REGISTER", "Registration failed:" + e.getMessage());
                    Toast.makeText(SignUpActivity.this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("REGISTER", "Response body: " + responseBody);
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String authToken = jsonObject.getString("access_token");
                        String email = jsonObject.getJSONObject("user").getString("email");
                        supaBaseClient.storeAuthToken(SignUpActivity.this, authToken);
                        runOnUiThread(() -> {
                            Log.i("REGISTER", "Registration successful");
                            Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> Log.e("REGISTER", "Failed to parse response: " + e.getMessage()));
                    }
                } else {
                    runOnUiThread(() -> {
                        Log.e("REGISTER", "Registration failed: " + response.message());
                        Toast.makeText(SignUpActivity.this, "Registration failed: " + response.message(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}

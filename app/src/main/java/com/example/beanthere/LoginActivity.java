package com.example.beanthere;

import com.example.beanthere.network.supaBaseClient;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.util.*;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beanthere.databinding.ActivityLoginBinding;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v->{
            loginUSer();
        });

        binding.signupText.setOnClickListener(v-> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
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
                    String responseBody = response.body().string();
                    Log.d("Login response", "response: " + responseBody);
                    try{
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String authToken = jsonObject.getString("access_token");
                        supaBaseClient.storeAuthToken(LoginActivity.this, authToken);
                        runOnUiThread(()->Log.i("LOGIN", "Login successful"));
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }catch (Exception e){
                        runOnUiThread( () -> Log.e("LOGIN",
                                "Failed to parse response: " + e.getMessage()));
                    }
                }else{
                    runOnUiThread(()-> Log.e("LOGIN", "Login failed: " + response.message()));
                }
            }
        });
    }

}
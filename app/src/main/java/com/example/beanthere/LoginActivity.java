package com.example.beanthere;

import com.example.beanthere.network.supaBaseClient;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.util.*;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.beanthere.databinding.ActivityLoginBinding;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private EditText passwordEditText;
    Button loginButton;
    ProgressBar loginProgress;
    EditText emailText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginButton = findViewById(R.id.loginButton);
        loginProgress = findViewById(R.id.loginProgress);
        emailText = findViewById(R.id.editTextTextEmailAddress);
        passwordText = findViewById(R.id.editTextTextPassword);

        loginButton.setOnClickListener(v->{
            isFieldEmpty();
        });

        binding.signupText.setOnClickListener(v-> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });


    }


    private void loginUSer(){

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();



        supaBaseClient.loginUser(email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread( () -> {
                    showLoading(false);
                    Log.e("LOGIN", "Login failed: " + e.getMessage());
                });
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
                        runOnUiThread( () -> {
                            showLoading(false);
                            Log.e("LOGIN",
                                    "Failed to parse response: " + e.getMessage());
                        });
                    }
                }else{
                    runOnUiThread(()->{
                        showLoading(false);
                        Log.e("LOGIN", "Login failed: " + response.message());
                    });
                }
            }
        });
    }

    //check if user input is valid, if not show a toast message about missing input
    private void isFieldEmpty(){
        Drawable errorDrawable = ContextCompat.getDrawable(this, R.drawable.edit_text_error_border);
        Drawable normalBorder = ContextCompat.getDrawable(this, R.drawable.edit_text_border);


        if(TextUtils.isEmpty(emailText.getText()) && TextUtils.isEmpty(passwordText.getText())){
            emailText.setBackground(errorDrawable);//chang both email and password field borders to red
            passwordText.setBackground(errorDrawable);
            toastMaker("Enter your details");
        } else if(TextUtils.isEmpty(emailText.getText())) {
            emailText.setBackground(errorDrawable); //change only email field border to red
            passwordText.setBackground(normalBorder); //if password border was red return to normal
            toastMaker("Email can't be empty");
        } else if(passwordText.getText().toString().isEmpty()) {
            emailText.setBackground(normalBorder);
            passwordText.setBackground(errorDrawable);
            toastMaker("Password can't be empty");
        } else if (!isValidEmail(emailText.getText().toString())) {
            emailText.setBackground(errorDrawable);
            toastMaker("Enter a valid email");
        } else{
            showLoading(true);  //if both are valid, show the loading button and initiate login
            loginUSer();
        }

    }

    //when user clicks the login button, hide the login text-disable button, and show loading progress
    // so they know that app is still loading
    private void showLoading(boolean loading) {
        if (loading) {
            loginButton.setText("");
            loginButton.setEnabled(false);
            loginProgress.setVisibility(View.VISIBLE);
        } else {
            // if the login fails, we will revert button back to normal and hide the progress bar
            loginProgress.setVisibility(View.GONE);
            loginButton.setText(R.string.login_buttonText);
            loginButton.setEnabled(true);
        }
    }

    //check if the email format is valid
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    //pass string as input to show toast
    // i can't keep writing that toast message, it's too long
    private void toastMaker(String message){
        Toast.makeText(this, message,Toast.LENGTH_LONG).show();
    }

}
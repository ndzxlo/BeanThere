package com.example.beanthere.network;

import static android.provider.Settings.System.getString;

import okhttp3.*;

import com.example.beanthere.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;

public class supaBaseClient{

    private static final String SUPABASE_URL = "https://kwqthapmdmfaoxdbugld.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imt3cXRoYXBtZG1mYW94ZGJ1Z2xkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTc4ODM0NTksImV4cCI6MjAzMzQ1OTQ1OX0.siKe_QAcHKED6dtTz5Ye7Bta3O10PjFDlhxrhUPTCeM";
    private static final OkHttpClient client = new OkHttpClient();

    // Register new user
    public static void registerUser(String name, String email, String password, Callback callback) {
        String url = SUPABASE_URL + "/auth/v1/signup";

        // JSON payload
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);
        JsonObject userMetadata = new JsonObject();
        userMetadata.addProperty("full_name", name);
        json.add("data", userMetadata);

        RequestBody body = RequestBody.create(new Gson().toJson(json), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // Login existing user
    public static void loginUser(String email, String password, Callback callback) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";

        // JSON payload
        String json = new Gson().toJson(new User(email, password));

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    // Data model for user
    static class User {
        String email;
        String password;

        String name;

        User(String email, String password) {
            //this.name = name;
            this.email = email;
            this.password = password;
        }

        User (String name, String email, String password){
            this.name = name;
            this.email = email;
            this.password = password;
        }
    }
}


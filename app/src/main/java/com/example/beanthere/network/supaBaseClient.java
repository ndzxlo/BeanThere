package com.example.beanthere.network;

import static android.provider.Settings.System.getString;

import okhttp3.*;

import com.example.beanthere.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import org.json.JSONObject;

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

        RequestBody body = RequestBody.create(new Gson().toJson(json),
                MediaType.parse("application/json"));

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
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        RequestBody body = RequestBody.create(new Gson().toJson(json),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void addToFavourites(String userId, String placeID, String name, String address, String rating, String coordinates, Callback callback){
        String url = SUPABASE_URL + "/rest/v1/favourites";

        // JSON data to fill favourites table
        JsonObject json = new JsonObject();
        json.addProperty("user_id", userId);
        json.addProperty("place_id", placeID);
        json.addProperty("name", name);
        json.addProperty("address", address);
        json.addProperty("rating", rating);
        json.addProperty("latlng", coordinates);

        RequestBody body = RequestBody.create(new Gson().toJson(json),
                MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization","Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void getFavourites(String userId, Callback callback){

        String url = SUPABASE_URL + "/rest/v1/favourites?select=*&user_id=eq." + userId;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static void logoutUser(Context context, Callback callback) {
        String authToken = getAuthToken(context);
        if (authToken == null) {
            Log.e("LOGOUT", "No auth token found");
            return;
        }

        String url = SUPABASE_URL + "/auth/v1/logout";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        client.newCall(request).enqueue(callback);

        // Clear the locally stored token
        clearAuthToken(context);
    }

    public static void getUser(String authToken,Callback callback){

        //url to get current authenticated user's profile from supaBase
        String url = SUPABASE_URL + "/auth/v1/user";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        client.newCall(request).enqueue(callback);

    }

    public static void storeAuthToken(Context context, String authToken) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,                               // context
                    "secure_prefs",                        // file name
                    masterKey,                             // master key
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,   // key encryption scheme
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM  // value encryption scheme
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("AUTH_TOKEN", authToken);
            editor.apply();
        } catch (Exception e) {
            Log.e("STORE_TOKEN", "Failed to store auth token: " + e.getMessage());
        }
    }

    public static String getAuthToken(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,                               // context
                    "secure_prefs",                        // file name
                    masterKey,                             // master key
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,   // key encryption scheme
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM  // value encryption scheme
            );

            return sharedPreferences.getString("AUTH_TOKEN", null);
        } catch (Exception e) {
            Log.e("GET_TOKEN", "Failed to retrieve auth token: " + e.getMessage());
            return null;
        }
    }

    public static void clearAuthToken(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,                               // context
                    "secure_prefs",                        // file name
                    masterKey,                             // master key
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,   // key encryption scheme
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM  // value encryption scheme
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("AUTH_TOKEN");
            editor.apply();
        } catch (Exception e) {
            Log.e("CLEAR_TOKEN", "Failed to clear auth token: " + e.getMessage());
        }
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


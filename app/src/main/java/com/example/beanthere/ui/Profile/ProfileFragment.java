package com.example.beanthere.ui.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.*;
import android.widget.*;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beanthere.LoginActivity;
import com.example.beanthere.databinding.FragmentProfileBinding;
import com.example.beanthere.network.supaBaseClient;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getUserData();

        SwitchMaterial mySwitch = binding.darkModeSwitch;

        binding.logOutButton.setOnClickListener(v -> logoutUser());
        binding.darkModeSwitchButton.setOnClickListener(view ->
                mySwitch.setChecked(mySwitch.isChecked()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getUserData() {

        String authToken = supaBaseClient.getAuthToken(requireContext());

        supaBaseClient.getUser(authToken, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("UserData", "Get user data failed: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONObject metaData = jsonObject.getJSONObject("user_metadata");
                        String fullName = metaData.getString("full_name");
                        String email = jsonObject.getString("email");
                        requireActivity().runOnUiThread(() -> {
                            Log.i("UserData", "Get user data successful");
                            binding.UserName.setText(fullName);
                            binding.UserEmail.setText(email);
                        });
                    } catch (JSONException e) {
                        requireActivity().runOnUiThread(() -> {
                            Log.e("UserData", "Failed to parse response: " + e.getMessage());
                        });
                    }
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Log.e("UserData", "Failed to parse response: " + response.message());
                    });
                }
            }
        });
    }

    private void logoutUser() {
        supaBaseClient.logoutUser(requireContext(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Log.e("LOGOUT", "Logout failed: " + e.getMessage());
                    Toast.makeText(requireContext(), "Logout failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        Log.i("LOGOUT", "Logout successful");
                        Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                        requireActivity().finish();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Log.e("LOGOUT", "Logout failed: " + response.message());
                        Toast.makeText(requireContext(), "Logout failed: " + response.message(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}

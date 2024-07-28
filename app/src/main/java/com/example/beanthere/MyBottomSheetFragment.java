package com.example.beanthere;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.beanthere.network.supaBaseClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String ARG_PLACE_ID = "place_id";
    private static final String ARG_SHOP_NAME = "shop_name";
    private static final String ARG_SHOP_ADDRESS = "shop_address";
    private static final String ARG_SHOP_RATING = "shop_rating";
    private static final String ARG_LAT_LNG = "lat_lng";
    private static String name;
    private static String address;
    private static String rating;
    private String userId;

    public static MyBottomSheetFragment newInstance (String placeId, String shopName,String shopAddress, String shopRating,String latlng){
        MyBottomSheetFragment fragment = new MyBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLACE_ID, placeId);
        args.putString(ARG_SHOP_NAME, shopName);
        args.putString(ARG_SHOP_ADDRESS, shopAddress);
        args.putString(ARG_SHOP_RATING, shopRating);
        args.putString(ARG_LAT_LNG, latlng);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coffee_shop_bottomsheet, container, false);

        retrieveUserId();

        if (getArguments() != null) {
            name = getArguments().getString(ARG_SHOP_NAME);
            TextView shopNameTextView = view.findViewById(R.id.bottomsheet_shopName);
            shopNameTextView.setText(name);

            rating = getArguments().getString(ARG_SHOP_RATING);
            TextView shopRatingTextView = view.findViewById(R.id.bottomsheet_rating);

            address = getArguments().getString(ARG_SHOP_ADDRESS);
            TextView shopAddressTextView = view.findViewById(R.id.bottomsheet_shopAddress);
            shopAddressTextView.setText(address);

            if(rating !=null) {
                shopRatingTextView.setText(rating);
            }else{
                shopRatingTextView.setText("No rating");
            }
        }

        ImageView heart = view.findViewById(R.id.bottomsheet_favButton);
        heart.setOnClickListener(v->
            addFavourite());
        return view;
    }

    private void addFavourite(){

        if (userId == null) {
            Toast.makeText(requireContext(), "User ID not available. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = getArguments().getString(ARG_PLACE_ID);
        String lat = getArguments().getString(ARG_LAT_LNG);

        supaBaseClient.addToFavourites(userId,id, name, address, rating, lat, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(()-> Log.e("addToFavourites",
                        "Failed to add to favourites" + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Added to favourites", Toast.LENGTH_LONG).show());
                }else {
                    requireActivity().runOnUiThread(() -> {
                        try {
                            String errorResponse = response.body().string();
                            Log.e("addToFavourites", "Error response: " + errorResponse);
                            Toast.makeText(requireContext(), "Failed to add to favourites" + errorResponse, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("addToFavourites", "Error reading response body: " + e.getMessage());
                            Toast.makeText(requireActivity(), "shop not added:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    private void retrieveUserId() {
        String authToken = supaBaseClient.getAuthToken(requireContext());
        if (authToken == null) {
            Log.e("MyBottomSheetFragment", "No auth token found");
            return;
        }

        supaBaseClient.getUser(authToken, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MyBottomSheetFragment", "Failed to get user: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        userId = jsonObject.getString("id");
                    } catch (Exception e) {
                        Log.e("MyBottomSheetFragment", "Failed to parse user response: " + e.getMessage());
                    }
                } else {
                    Log.e("MyBottomSheetFragment", "Failed to get user: " + response.code());
                }
            }
        });
    }
}

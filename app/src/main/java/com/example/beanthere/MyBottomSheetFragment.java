package com.example.beanthere;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MyBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String ARG_SHOP_NAME = "shop_name";
    private static final String ARG_SHOP_RATING = "shop_rating";
    private static final String ARG_SHOP_ADDRESS = "shop_address";

    public static MyBottomSheetFragment newInstance (String shopName, String shopRating, String shopAddress){
        MyBottomSheetFragment fragment = new MyBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SHOP_NAME, shopName);
        args.putString(ARG_SHOP_RATING, shopRating);
        args.putString(ARG_SHOP_ADDRESS, shopAddress);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coffee_shop_bottomsheet, container, false);

        if (getArguments() != null) {
            String shopName = getArguments().getString(ARG_SHOP_NAME);
            TextView shopNameTextView = view.findViewById(R.id.bottomsheet_shopName);
            shopNameTextView.setText(shopName);

            String shopRating = getArguments().getString(ARG_SHOP_RATING);
            TextView shopRatingTextView = view.findViewById(R.id.bottomsheet_rating);

            String shopAddress = getArguments().getString(ARG_SHOP_ADDRESS);
            TextView shopAddressTextView = view.findViewById(R.id.bottomsheet_shopAddress);
            shopAddressTextView.setText(shopAddress);

            if(shopRating !=null) {
                shopRatingTextView.setText(shopRating);
            }else{
                shopRatingTextView.setText("No rating");
            };
        }

        ImageView heart = view.findViewById(R.id.bottomsheet_favButton);
        heart.setOnClickListener(v-> {
            if (heart.getDrawable().getConstantState() != null &&
                    heart.getDrawable().getConstantState().equals(
                            ResourcesCompat.getDrawable(getResources(), R.drawable.heart_regular, null).getConstantState())) {
                heart.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.heart_solid, null));
            } else {
                heart.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.heart_regular, null));
            }
        });
        return view;
    }
}

package com.example.beanthere.ui.Favourites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beanthere.databinding.FragmentFavouritesBinding;
import com.example.beanthere.network.supaBaseClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FavouritesFragment extends Fragment {

    private FragmentFavouritesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FavouritesViewModel favouritesViewModel =
                new ViewModelProvider(this).get(FavouritesViewModel.class);

        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFavourites;;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
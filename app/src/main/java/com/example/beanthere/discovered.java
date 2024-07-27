package com.example.beanthere;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.beanthere.databinding.FragmentDiscoveredBinding;
import com.example.beanthere.ui.Profile.ProfileViewModel;

public class discovered extends Fragment {

    private FragmentDiscoveredBinding binding;
    private static final int REQUEST_IMAGE_SELECT = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;

    private ImageView uploadedImages;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discovered, container, false);

        Button addImages = view.findViewById(R.id.uploadImage);

        return view;
    }

}
package com.example.beanthere;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.beanthere.databinding.FragmentDiscoveredBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contribute extends Fragment implements UploadedImageAdapter.OnFabClickListener{

    private FragmentDiscoveredBinding binding;
    private static final int REQUEST_IMAGE_SELECT = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private PlacesClient placesClient;
    private String discoveredPlaceId;
    private String discoveredPlaceLatLng;
    private String discoveredPlaceAddress;
    private ImageView uploadedImages;
    private ActivityResultLauncher<PickVisualMediaRequest> pickPlaceImages;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMenuImages;
    private List<UploadedImageModel> placeImageItems;
    private List<UploadedImageModel> menuImageItems;
    private UploadedImageAdapter placeImageAdapter;
    private UploadedImageAdapter menuImageAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the photo picker launcher
        pickPlaceImages = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(3), this::handlePickedPlaceImages);
        pickMenuImages = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(3), this::handlePickedMenuImages);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discovered, container, false);

        // Initialize the SDK
        String apiKey = BuildConfig.PLACES_API_KEY;
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(requireContext(), apiKey);
        }

        //create places client instance
        placesClient = Places.createClient(requireContext());

        autoComplete();

        RecyclerView placeImageRecyclerView = view.findViewById(R.id.placeImageRecyclerview);
        RecyclerView menuImageRecyclerView = view.findViewById(R.id.menuImageRecyclerview);

        placeImageItems = new ArrayList<>();
        menuImageItems = new ArrayList<>();

        placeImageAdapter = new UploadedImageAdapter(placeImageItems, requireContext(), this, true);
        menuImageAdapter = new UploadedImageAdapter(menuImageItems, requireContext(), this, false);

        placeImageRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        menuImageRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        placeImageRecyclerView.setAdapter(placeImageAdapter);
        menuImageRecyclerView.setAdapter(menuImageAdapter);

        Button placeUploadButton = view.findViewById(R.id.uploadPlaceButton);
        Button uploadMenuButton = view.findViewById(R.id.uploadMenuButton);

        placeUploadButton.setOnClickListener(v -> {
            launchPlaceImagePicker();
        });

        uploadMenuButton.setOnClickListener(v -> {
            launchMenuImagePicker();
        });

        return view;
    }

    private void autoComplete(){

        //initialize autocompleteSupportFragment
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocompleteFragment);

        autocompleteSupportFragment.setHint("enter coffee shop address");

        //data to return
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,
                Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                discoveredPlaceId = place.getId();
                discoveredPlaceAddress = place.getAddress();
                discoveredPlaceLatLng = place.getLatLng().toString();
                Log.i("autocomplete successful", "Place: " + place.getId());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(requireContext(),"Couldn't select address", Toast.LENGTH_LONG).show();
                Log.i("autocomplete failed", "An error occurred: " + status);
            }
        });
    }

    private void launchPlaceImagePicker() {
        pickPlaceImages.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void launchMenuImagePicker() {
        pickMenuImages.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void handlePickedPlaceImages(List<Uri> uris) {
        if (!uris.isEmpty()) {
            for (Uri uri : uris) {
                int position = placeImageItems.size();
                placeImageItems.add(new UploadedImageModel(uri.toString()));
                placeImageAdapter.notifyItemInserted(position);
            }
            Log.d("PhotoPicker", "Number of items selected: " + uris.size());
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    }

    private void handlePickedMenuImages(List<Uri> uris) {
        if (!uris.isEmpty()) {
            for (Uri uri : uris) {
                int position = menuImageItems.size();
                menuImageItems.add(new UploadedImageModel(uri.toString()));
                menuImageAdapter.notifyItemInserted(position);
            }
            Log.d("PhotoPicker", "Number of items selected: " + uris.size());
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    }

    public void onFabClick(int position, boolean isPlaceImage) {
        if(isPlaceImage) {
            if (position >= 0 && position < placeImageItems.size()) {
                placeImageItems.remove(position);
                placeImageAdapter.notifyItemRemoved(position);
                Log.d("PhotoPicker", "Image removed at position: " + position);
            }
        }else{
            if (position >= 0 && position < menuImageItems.size()) {
                menuImageItems.remove(position);
                menuImageAdapter.notifyItemRemoved(position);
                Log.d("PhotoPicker", "Image removed at position: " + position);
            }
        }
    }

    private void addNewLocation(){}
}
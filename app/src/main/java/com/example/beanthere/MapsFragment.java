package com.example.beanthere;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.beanthere.BuildConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.AdvancedMarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapCapabilities;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PinConfig;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.IsOpenRequest;
import com.google.android.libraries.places.api.net.IsOpenResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.Arrays;
import java.util.List;
import java.lang.Object;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // Check for location permissions
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permissions are granted, proceed with getting the location
                getDeviceLocation();
            } else {
                // Permissions are not granted
                Toast.makeText(requireContext(), "Location permissions are not granted", Toast.LENGTH_SHORT).show();
            }
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setIndoorEnabled(false);
            googleMap.setMinZoomPreference(16);
            googleMap.setMyLocationEnabled(true);

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Define a variable to hold the Places API key.
        String apiKey = BuildConfig.PLACES_API_KEY;
        
        //initialize the sdk
        Places.initializeWithNewPlacesApiEnabled(requireContext(), apiKey);
        //placesClient instance
        placesClient = Places.createClient(requireContext());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Location location = task.getResult();
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                                showCoffeeShops(currentLatLng, placesClient);
                            } else {
                                Log.d("Map", "Current location is null. Using defaults.");
                                Log.e("Map", "Exception: %s", task.getException());
                            }
                        });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void showCoffeeShops(LatLng current, PlacesClient placesClient) {
        //list of fields to include in the response for each place
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.RATING);

        //set coffee shop search to a 3km radius
        CircularBounds circle = CircularBounds.newInstance(current, 3000);

        //get placeType of coffee shop
        final List<String> includeTypes = Arrays.asList("coffee_shop", "cafe");

        //using builder to create a searchNearbyRequest object
        final SearchNearbyRequest searchNearbyRequest =
                SearchNearbyRequest.builder(/* location restriction = */ circle, placeFields)
                        .setIncludedTypes(includeTypes)
                        .setMaxResultCount(10)
                        .build();

        //call placesClient to perform search
        placesClient.searchNearby(searchNearbyRequest)
                .addOnSuccessListener(response -> {
                    List<Place> places = response.getPlaces();
                    places.forEach(place -> {
                        PinConfig.Builder pinConfigBuilder = PinConfig.builder();
                        pinConfigBuilder.setBackgroundColor(Color.rgb(139, 69, 19)); // Coffee brown color
                        pinConfigBuilder.setBorderColor(Color.WHITE);

                        // Set a coffee cup glyph
                        PinConfig.Glyph glyphText = new PinConfig.Glyph("☕", Color.WHITE);
                        pinConfigBuilder.setGlyph(glyphText);

                        PinConfig pinConfig = pinConfigBuilder.build();

                        // Create AdvancedMarkerOptions
                        AdvancedMarkerOptions advancedMarkerOptions = new AdvancedMarkerOptions()
                                .position(place.getLatLng())
                                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig))
                                .title(place.getName())
                                .snippet("Rating: " + place.getRating());

                        // Add the marker to the map
                        mMap.addMarker(advancedMarkerOptions);
                    });
                });
    }
}

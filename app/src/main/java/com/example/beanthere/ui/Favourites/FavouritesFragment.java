package com.example.beanthere.ui.Favourites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beanthere.FavouritesAdapter;
import com.example.beanthere.FavouritesModel;
import com.example.beanthere.MapsFragment;
import com.example.beanthere.databinding.FragmentFavouritesBinding;
import com.example.beanthere.network.supaBaseClient;
//import com.example.beanthere.FavouritesFragmentDirections;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FavouritesFragment extends Fragment implements FavouritesAdapter.OnItemClickListener {

    private FragmentFavouritesBinding binding;
    private String userId;
    private String userName;
    private RecyclerView recyclerView;
    private TextView noFavouritesText;
    private TextView addFavouritesText;
    private FavouritesAdapter adapter;
    private List<FavouritesModel> favouriteItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FavouritesViewModel favouritesViewModel =
                new ViewModelProvider(this).get(FavouritesViewModel.class);

        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.favouritesRecyclerView;
        noFavouritesText = binding.favouritesDefaultText;
        addFavouritesText = binding.AddToFavouritesText;

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FavouritesAdapter(favouriteItems, this);
        recyclerView.setAdapter(adapter);

        //fetch user id so we can use it when calling get method for user database info
        retrieveUserId();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void fetchFavourites(){

        supaBaseClient.getFavourites(userId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread( ()->
                        Log.e("favourites", "failed to fetch favourites " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            FavouritesModel item = new FavouritesModel(
                                    object.getString("place_id"),
                                    object.getString("name"),
                                    object.getString("address"),
                                    object.getDouble("rating"),
                                    object.getString("latlng")
                            );
                            favouriteItems.add(item);
                        }

                        requireActivity().runOnUiThread(() -> {
                            Log.i("UserData", "Get user data successful");
                            isFavourites(favouriteItems);
                        });
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() ->
                                Log.e("UserData", "Failed to parse response: " + e.getMessage()));
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                        Log.e("UserData", "Failed to parse response: " + response.message())
                    );
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
                        // we only fetch the favourites only after we receive the id to prevent fetch
                        //running before we get the id
                        fetchFavourites();
                    } catch (Exception e) {
                        Log.e("MyBottomSheetFragment", "Failed to parse user response: " + e.getMessage());
                    }
                } else {
                    Log.e("MyBottomSheetFragment", "Failed to get user: " + response.code());
                }
            }
        });
    }

    private void deleteFavourite(FavouritesModel favouritesModel){

        supaBaseClient.deleteFavourite(userId, favouritesModel.getPlaceID(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(()->{
                    showToast("Couldn't delete from favourites");
                    Log.e("DELETE_FAVOURITE", "Failed to delete favourite: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if( response.isSuccessful() ){
                    requireActivity().runOnUiThread( ()->{
                        int position = favouriteItems.indexOf(favouritesModel);
                        if (favouriteItems.size() == 1) {  // This was the last item
                            favouriteItems.remove(position);
                            adapter.notifyItemRemoved(position);
                            isFavourites(favouriteItems);  // This will hide the RecyclerView and show the "no favourites" text
                        } else {
                            favouriteItems.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                        showToast("Deleted from favourites");
                        Log.d("DELETE_FAVOURITE", "Favourite deleted successfully");
                    });

                }else{
                    requireActivity().runOnUiThread(()->{
                        showToast("Couldn't delete from favourites");
                        Log.e("DELETE_FAVOURITE", "Failed to delete favourite: " +response.message());
                    });

                }
            }
        });
    }

    private void isFavourites(List faves){
        if (faves.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noFavouritesText.setVisibility(View.VISIBLE);
            addFavouritesText.setVisibility(View.VISIBLE);
        } else{
            recyclerView.setVisibility(View.VISIBLE);
            noFavouritesText.setVisibility(View.GONE);
            addFavouritesText.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onShareButtonClick(FavouritesModel favouritesModel) {
        shareLocation(favouritesModel);
    }

    @Override
    public void onFavouriteButtonClick(FavouritesModel favouritesModel) {
        deleteFavourite(favouritesModel);
    }

    @Override
    public void onNavigateButtonClick(FavouritesModel favouritesModel) {
        String placeId = favouritesModel.getPlaceID();
//        NavDirections action = FavouritesFragmentDirections.actionFavouritesFragmentToMapsFragment(placeId);
//        Navigation.findNavController(requireView()).navigate(action);
    }

    private void shareLocation(FavouritesModel favouritesModel){
        Intent shareCoffeeShop = new Intent();
        shareCoffeeShop.setAction(Intent.ACTION_SEND);
        shareCoffeeShop.putExtra(Intent.EXTRA_TEXT, "Hey, I found this cool place on bean there called " +
                favouritesModel.getName() + ", it is located at " + favouritesModel.getAddress());
        shareCoffeeShop.setType("text/plain");
        Intent shareIntent = Intent.createChooser(shareCoffeeShop, null);
        startActivity(shareIntent);
    }

    private void showToast(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
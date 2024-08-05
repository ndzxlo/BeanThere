package com.example.beanthere;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder> {
    private List<FavouritesModel> favouritesModelList;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onShareButtonClick(FavouritesModel favouritesModel);
        void onFavouriteButtonClick(FavouritesModel favouritesModel);
        void onNavigateButtonClick(FavouritesModel favouritesModel);
    }

    public FavouritesAdapter(List<FavouritesModel> favouritesModelList, OnItemClickListener listener) {
        this.favouritesModelList = favouritesModelList;
        this.listener= listener;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourites_cardview, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        FavouritesModel item = favouritesModelList.get(position);
        holder.nameTextView.setText(truncateName(item.getName()));
        holder.addressTextView.setText(truncateAddress(item.getAddress()));
        holder.ratingTextView.setText(item.getRating().toString());
        holder.nameLetter.setText(firstLetter(item.getName()));
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return favouritesModelList.size();
    }

    static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView addressTextView;
        TextView ratingTextView;
        TextView nameLetter;
        ImageView favouritesButton;
        ImageView favouritesShareButton;
        Button favouritesNavigateButton;

        FavouriteViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.favouriteShopName);
            addressTextView = itemView.findViewById(R.id.favouritesAddressTex);
            ratingTextView = itemView.findViewById(R.id.favouriteRate);
            nameLetter = itemView.findViewById(R.id.favouritesLogo);
            favouritesButton = itemView.findViewById(R.id.favouriteButton);
            favouritesShareButton = itemView.findViewById(R.id.favouritesShareButton);
            favouritesNavigateButton = itemView.findViewById(R.id.favouriteNavigateButton);
        }

        void bind(final FavouritesModel item, final OnItemClickListener listener) {

            favouritesButton.setOnClickListener(v ->{
                listener.onFavouriteButtonClick(item);});

            favouritesShareButton.setOnClickListener(v -> {
                onclick(favouritesShareButton, itemView);
                listener.onShareButtonClick(item);
            });

            favouritesNavigateButton.setOnClickListener(v -> listener.onNavigateButtonClick(item));
        }
    }

    private String truncateAddress(String fullAddress) {
        String[] parts = fullAddress.split(",");
        if (parts.length >= 3) {
            return parts[0].trim() + ", " + parts[1].trim() + ", " + parts[2].trim();
        }
        return fullAddress; // Return the full address if it can't be truncated as expected
    }

    private String truncateName(String name) {
        String[] words = name.split(" ");
        if (words.length >= 3) {
            return words[0].trim() + " " + words[1].trim() + " " + words[2].trim() + "...";
        }
        return name;
    }
    /*take the shopname string and only return the first letter, this will be used as an "icon"
    * for each */
    private String firstLetter(String text) {
        if (text.length() <= 1) {
            return text;
        } else {
            return text.substring(0, 1);
        }
    }

    private static void onclick(ImageView imageView, View v){
        imageView.setBackground(ContextCompat.getDrawable(v.getContext(),
                R.drawable.cardview_onclick_background));

        // Remove the shade after a delay (e.g., 500 milliseconds)
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.setBackground(ContextCompat.getDrawable(v.getContext(),
                        R.drawable.cardview_background));
            }
        }, 500);
    }
}


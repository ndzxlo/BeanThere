package com.example.beanthere;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder> {
    private List<FavouritesModel> favouritesModelList;

    public FavouritesAdapter(List<FavouritesModel> favouritesModelList) {
        this.favouritesModelList = favouritesModelList;
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

        FavouriteViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.favouriteShopName);
            addressTextView = itemView.findViewById(R.id.favouritesAddressTex);
            ratingTextView = itemView.findViewById(R.id.favouriteRate);
            nameLetter = itemView.findViewById(R.id.favouritesLogo);
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

    private String firstLetter(String text) {
        if (text.length() <= 1) {
            return text;
        } else {
            return text.substring(0, 1);

        }
    }
}


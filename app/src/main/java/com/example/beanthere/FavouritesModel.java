package com.example.beanthere;

public class FavouritesModel {
    String placeID;
    String name;
    String address;
    Double rating;
    String latlng;

    public FavouritesModel(String placeID, String name, String address, Double rating, String latlng){
        this.placeID = placeID;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.latlng = latlng;
    }

    public String getPlaceID() {
        return placeID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getRating() {
        return rating;
    }

    public String getLatlng() {
        return latlng;
    }
}




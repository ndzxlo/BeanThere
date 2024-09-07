package com.example.beanthere;

public class UploadedImageModel {
    private String imageUri;

    public UploadedImageModel(String imageUri){
        this.imageUri = imageUri;
    }

    public String getImageUri(){
        return imageUri;
    }
}

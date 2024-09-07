package com.example.beanthere;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class UploadedImageAdapter extends RecyclerView.Adapter<UploadedImageAdapter.ViewHolder>{
    private List<UploadedImageModel>imageModelList;
    private Context context;
    private OnFabClickListener fabOnClickListener;
    private boolean isPlaceImage;

    public interface OnFabClickListener{
        void onFabClick(int position, boolean isPlaceImage);
    }

    public UploadedImageAdapter(List<UploadedImageModel> imageModelList, Context context, OnFabClickListener listener, boolean isPlaceImage) {
        this.imageModelList = imageModelList;
        this.context = context;
        this.fabOnClickListener = listener;
        this.isPlaceImage = isPlaceImage;
    }

    @NonNull
    @Override
    public UploadedImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.contribute_imageview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadedImageAdapter.ViewHolder holder, int position) {
        UploadedImageModel uploadedImageModel = imageModelList.get(position);
        Glide.with(context).load(uploadedImageModel.getImageUri()).into(holder.imageView);

        holder.fab.setOnClickListener(v->{
            if(fabOnClickListener !=null){
                fabOnClickListener.onFabClick(position, isPlaceImage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public FloatingActionButton fab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.menuImageView);
            fab = itemView.findViewById(R.id.deleteUploadButton);
        }
    }
}

package com.pin.recommend.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.pin.recommend.R;
import com.pin.recommend.model.StoryPicture;

import java.util.ArrayList;
import java.util.List;

public class ViewStoryPictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<StoryPicture> pictures;

    public ViewStoryPictureAdapter(Context context){
        this.context = context;
        this.pictures = new ArrayList<>();
    }

    public void setList(List<StoryPicture> storyPictures){
        this.pictures = storyPictures;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pick_story_picture, parent, false);

        ItemViewHolder vh = new ItemViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Bitmap bitmap = pictures.get(position).getBitmap(context, 200, 200);
        final ImageView previewImageView = ((ItemViewHolder)holder).previewImageView;
        previewImageView.setImageBitmap(bitmap);
        previewImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, previewImageView);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.pic_story_picture_popup, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove:
                                pictures.remove(position);
                                notifyDataSetChanged();
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView previewImageView;

        public ItemViewHolder(final View itemView){
            super(itemView);
            previewImageView = itemView.findViewById(R.id.preview_image);
        }
    }


}

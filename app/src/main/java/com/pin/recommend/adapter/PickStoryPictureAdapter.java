package com.pin.recommend.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.R;

import java.util.ArrayList;
import java.util.List;

public class PickStoryPictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private Context context;
    private List<Bitmap> pictures;

    public PickStoryPictureAdapter(Context context){
        this.context = context;
        this.pictures = new ArrayList<>();
    }

    public void setList(List<Bitmap> bitmaps){
        this.pictures = bitmaps;
        notifyDataSetChanged();
    }

    public void add(Bitmap bitmap){
        pictures.add(bitmap);
        notifyDataSetChanged();
    }

    public List<Bitmap> getList() {
        return pictures;
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
        Bitmap bitmap = pictures.get(position);
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

package com.pin.recommend.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.R;
import com.pin.util.DisplaySizeCheck;

import java.util.ArrayList;
import java.util.List;

public class PickStoryPictureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private AppCompatActivity context;
    private List<Bitmap> pictures;
    private Point display;
    private boolean canDelete = false;

    public PickStoryPictureAdapter(AppCompatActivity context){
        this.context = context;
        this.pictures = new ArrayList<>();
        display = DisplaySizeCheck.getDisplaySize(context);
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public void setCanDelete(boolean canDelete){
        this.canDelete = canDelete;
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

    private int transformPos1(){
        switch (getItemCount()){
            case 2:
                return (int) (display.x * 0.5);
            case 3:
                return (int) (display.x * 0.5);
            default:
                return display.x;
        }
    }

    private int transformPos2(){
        return (int) (display.x * 0.5);
    }

    private int transformPos3(){
        return display.x;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Bitmap bitmap = pictures.get(position);
        final ImageView previewImageView = ((ItemViewHolder)holder).previewImageView;
        previewImageView.setImageBitmap(bitmap);

        ViewGroup.LayoutParams layout = previewImageView.getLayoutParams();
        switch (position) {
            case 0:
                layout.width = transformPos1();
                layout.height = layout.width;
                break;
            case 1:
                layout.width = transformPos2();
                layout.height = layout.width;
                break;
            case 2:
                layout.width = transformPos3();
                layout.height = (int) (layout.width * 0.5);
        }
        previewImageView.setLayoutParams(layout);

        if(canDelete) {
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
        }else{
            if(onClickListener != null){
                previewImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickListener.onClick(position);
                    }
                });
            }
        }


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

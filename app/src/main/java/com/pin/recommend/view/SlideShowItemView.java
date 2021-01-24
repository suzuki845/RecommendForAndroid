package com.pin.recommend.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.pin.recommend.R;

public class SlideShowItemView extends BaseSliderView {

    private Bitmap bitmap;

    public SlideShowItemView(Context context, Bitmap bitmap) {
        super(context);
        this.bitmap = bitmap;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.item_slide_show, null);
        ImageView target = (ImageView)v.findViewById(R.id.image);
        target.setImageBitmap(bitmap);
        bindEventAndShow(v, target);
        return v;
    }
}

package com.pin.recommend.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.R;

public class StoryPicture implements Parcelable {

    public int id;

    public String uri;

    private Bitmap bitmap;

    public Bitmap getBitmap(Context context, int width, int height){
        if(bitmap == null){
            Bitmap bitmap = BitmapUtility.readPrivateImage(context, uri, width, height);
            this.bitmap = BitmapUtility.getRoundedCornerBitmap(bitmap, 50);
        }

        return bitmap;
    }

    public Bitmap testGetBitmap(Context context, int width, int height){
        if(bitmap == null) {
            Bitmap b = BitmapUtility.decodeResource(context, R.drawable.test, width, height);
            this.bitmap = BitmapUtility.getRoundedCornerBitmap(b, 50);
        }
        return bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.uri);
        dest.writeParcelable(this.bitmap, flags);
    }

    public StoryPicture() {
    }

    protected StoryPicture(Parcel in) {
        this.id = in.readInt();
        this.uri = in.readString();
        this.bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<StoryPicture> CREATOR = new Parcelable.Creator<StoryPicture>() {
        @Override
        public StoryPicture createFromParcel(Parcel source) {
            return new StoryPicture(source);
        }

        @Override
        public StoryPicture[] newArray(int size) {
            return new StoryPicture[size];
        }
    };
}

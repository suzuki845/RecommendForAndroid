package com.pin.recommend.model.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.R;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Story.class, parentColumns = "id", childColumns = "storyId", onDelete = CASCADE, onUpdate = CASCADE))
public class StoryPicture implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(index = true)
    public long storyId;

    public String uri;

    public Bitmap testGetBitmap(Context context, int width, int height){
        Bitmap bitmap = BitmapUtility.decodeResource(context, R.drawable.test, width, height);
        bitmap = BitmapUtility.getRoundedCornerBitmap(bitmap, 50);
        return bitmap;
    }

    public Drawable getBackgroundDrawable(Context context, int w, int h){
        Bitmap bitmap = BitmapUtility.readPrivateImage(context, uri, w, h);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public Bitmap getBitmap(Context context, int width, int height) {
        Bitmap bitmap = BitmapUtility.readPrivateImage(context, uri, width, height);
        return bitmap;
    }

    public boolean saveImage(Context context, Bitmap bitmap){
        if(BitmapUtility.fileExistsByPrivate(context, uri)){
            BitmapUtility.deletePrivateImage(context, uri);
        }

        String filename = BitmapUtility.generateFilename();
        String ext = ".png";
        boolean success = BitmapUtility.insertPrivateImage(context, bitmap, filename, ext);

        uri = filename + ext;
        return success;
    }

    public boolean deleteImage(Context context){
        if(uri != null){
            BitmapUtility.deletePrivateImage(context, uri);
            this.uri = null;
            return true;
        }
        return false;
    }

    public StoryPicture() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.storyId);
        dest.writeString(this.uri);
    }

    protected StoryPicture(Parcel in) {
        this.id = in.readLong();
        this.storyId = in.readLong();
        this.uri = in.readString();
    }

    public static final Creator<StoryPicture> CREATOR = new Creator<StoryPicture>() {
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

package com.pin.recommend.model.entity;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.model.AppDatabase;
import com.pin.recommend.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Account.class, parentColumns = "id", childColumns = "accountId", onDelete = CASCADE, onUpdate = CASCADE))
public class RecommendCharacter implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(index = true)
    public long accountId;

    public String name;

    public Date created = new Date();

    @Ignore
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    public RecommendCharacter(){}

    public String iconImageUri;

    public boolean saveIconImage(Context context, Bitmap bitmap){
        if(BitmapUtility.fileExistsByPrivate(context,iconImageUri )){
            BitmapUtility.deletePrivateImage(context, iconImageUri);
        }

        String filename = BitmapUtility.generateFilename();
        String ext = ".png";
        boolean success = BitmapUtility.insertPrivateImage(context, bitmap, filename, ext);

        iconImageUri = filename + ext;
        return success;
    }

    public String backgroundImageUri;

    public boolean saveBackgroundImage(Context context, Bitmap bitmap){
        if(backgroundImageUri != null && BitmapUtility.fileExistsByPrivate(context, backgroundImageUri)){
            BitmapUtility.deletePrivateImage(context, backgroundImageUri);
        }

        String filename = BitmapUtility.generateFilename();
        String ext = ".png";
        boolean success = BitmapUtility.insertPrivateImage(context, bitmap, filename, ext);

        backgroundImageUri = filename + ext;
        return success;
    }

    public Bitmap getIconImage(Context context, int width, int height){
        Bitmap bitmap;
        if(iconImageUri == null) {
            return null;
        }

        bitmap = BitmapUtility.readPrivateImage(context, iconImageUri, width, height);
        return bitmap;
    }

    public boolean hasIconImage(){
        if(iconImageUri != null){
            return true;
        }
        return false;
    }

    public boolean deleteIconImage(Context context){
        if(iconImageUri != null){
            BitmapUtility.deletePrivateImage(context, iconImageUri);
            this.iconImageUri = null;
            return true;
        }
        return false;
    }

    public Bitmap getBackgroundBitmap(Context context, int width, int height){
        return BitmapUtility.readPrivateImage(context, backgroundImageUri, width, height);
    }

    public boolean hasBackgroundImage(){
        return backgroundImageUri != null;
    }

    public Drawable getBackgroundDrawable(Context context, int w, int h){
        if(hasBackgroundImage()) {
            Bitmap bitmap = getBackgroundBitmap(context, w, h);
            return new BitmapDrawable(context.getResources(), bitmap);
        }
        if(backgroundColor != null) {
            return new ColorDrawable(backgroundColor);
        }
        return null;
    }

    public boolean deleteImageBackgroundImage(Context context){
        if(backgroundImageUri != null){
             BitmapUtility.deletePrivateImage(context, backgroundImageUri);
             this.backgroundImageUri = null;
             return true;
        }
        return false;
    }


    public Integer backgroundColor = Color.WHITE;
    public Integer getBackgroundColor(){return backgroundColor;}

    public Integer toolbarBackgroundColor;
    public Integer getToolbarBackgroundColor(Context context, int defaultColor){
        if(toolbarBackgroundColor != null) {
            return toolbarBackgroundColor;
        }
        return defaultColor;
    }

    public Integer toolbarTextColor;
    public Integer getToolbarTextColor(Context context, int defaultColor){
        if(toolbarTextColor != null) {
            return toolbarTextColor;
        }
        return defaultColor;
    }

    public Integer homeTextColor = Color.parseColor("#444444");
    public Integer getHomeTextColor(){ return homeTextColor; }

    public String getFormattedDate(){
        return FORMAT.format(created);
    }

    @Ignore
    static final int MILLIS_OF_DAY = 1000 * 60 * 60 * 24;
    public long getDiffDays(Calendar calendar1) {
        TimeUtil.resetTime(calendar1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(created);
        TimeUtil.resetTime(calendar2);

        //modify
        calendar2.add(Calendar.DAY_OF_MONTH, -1);

        long diffTime = calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
        long diffDays = diffTime / MILLIS_OF_DAY;
        return diffDays;
    }

    public long getDiffDays(Calendar calendar1, int days) {
        TimeUtil.resetTime(calendar1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(created);
        TimeUtil.resetTime(calendar2);
        calendar2.add(Calendar.DAY_OF_MONTH, days);

        long diffTime = calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
        long diffDays = diffTime / MILLIS_OF_DAY;
        return diffDays;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.accountId);
        dest.writeString(this.name);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeString(this.iconImageUri);
        dest.writeString(this.backgroundImageUri);
        dest.writeValue(this.backgroundColor);
        dest.writeValue(this.toolbarBackgroundColor);
        dest.writeValue(this.toolbarTextColor);
        dest.writeValue(this.homeTextColor);
    }

    protected RecommendCharacter(Parcel in) {
        this.id = in.readLong();
        this.accountId = in.readLong();
        this.name = in.readString();
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.iconImageUri = in.readString();
        this.backgroundImageUri = in.readString();
        this.backgroundColor = (Integer) in.readValue(Integer.class.getClassLoader());
        this.toolbarBackgroundColor = (Integer) in.readValue(Integer.class.getClassLoader());
        this.toolbarTextColor = (Integer) in.readValue(Integer.class.getClassLoader());
        this.homeTextColor = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<RecommendCharacter> CREATOR = new Creator<RecommendCharacter>() {
        @Override
        public RecommendCharacter createFromParcel(Parcel source) {
            return new RecommendCharacter(source);
        }

        @Override
        public RecommendCharacter[] newArray(int size) {
            return new RecommendCharacter[size];
        }
    };
}

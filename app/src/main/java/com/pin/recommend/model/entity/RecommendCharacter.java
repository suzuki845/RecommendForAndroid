package com.pin.recommend.model.entity;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.pin.imageutil.BitmapUtility;
import com.pin.recommend.R;
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

    public String iconImageUri;

    public String backgroundImageUri;

    public Integer backgroundColor = Color.WHITE;

    public Integer toolbarBackgroundColor;

    public Integer toolbarTextColor;

    public Integer homeTextColor = Color.parseColor("#444444");


    //v2
    public String aboveText;

    public String belowText;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    public boolean isZeroDayStart;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    public int elapsedDateFormat;

    public String fontFamily;
    //end v2

    //v3
    public int storySortOrder;

    @Ignore public static final int CREATED_AT_DESC = 0;
    @Ignore public static final int CREATED_AT_ASC = 1;
    @Ignore public static final int UPDATED_AT_DESC = 2;
    @Ignore public static final int UPDATED_AT_ASC = 3;
    //end v3

    //v4
    public float backgroundImageOpacity = 1;
    public Integer homeTextShadowColor;
    public Integer getHomeTextShadowColor(){ return homeTextShadowColor != null ? homeTextShadowColor : Color.parseColor("#ffffffff"); }
    //end v4


    @Ignore
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    public RecommendCharacter(){}

    public String getFontFamily(){
        return fontFamily != null ? fontFamily : "default";
    }

    public String getAboveText(){
        return aboveText != null ?  aboveText : "を推してから";
    }

    public String getBelowText(){
        return belowText != null ? belowText : "になりました";
    }

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

    public boolean deleteBackgroundImage(Context context){
        if(backgroundImageUri != null){
             BitmapUtility.deletePrivateImage(context, backgroundImageUri);
             this.backgroundImageUri = null;
             return true;
        }
        return false;
    }


    public Integer getBackgroundColor(){return backgroundColor;}

    public Integer getToolbarBackgroundColor(Context context, int defaultColor){
        if(toolbarBackgroundColor != null) {
            return toolbarBackgroundColor;
        }
        return defaultColor;
    }

    public Integer getToolbarTextColor(Context context, int defaultColor){
        if(toolbarTextColor != null) {
            return toolbarTextColor;
        }
        return defaultColor;
    }

    public Integer getHomeTextColor(){ return homeTextColor != null ? homeTextColor : Color.parseColor("#444444"); }

    public String getFormattedDate(){
        return FORMAT.format(created);
    }

    @Ignore
    static final int MILLIS_OF_DAY = 1000 * 60 * 60 * 24;
    public String getDiffDaysSingle(Calendar now) {
        TimeUtil.resetTime(now);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(created);
        TimeUtil.resetTime(calendar2);

        //modify
        if(!isZeroDayStart){
            calendar2.add(Calendar.DAY_OF_MONTH, -1);
        }

        long diffTime = now.getTimeInMillis() - calendar2.getTimeInMillis();
        long diffDays = diffTime / MILLIS_OF_DAY;
        return diffDays + "日";
    }

    public String getDiffDaysYMD(Calendar now){
        TimeUtil.resetTime(now);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(created);
        TimeUtil.resetTime(calendar2);

        if(!isZeroDayStart){
            calendar2.add(Calendar.DAY_OF_MONTH, -1);
        }

        long diffTime = now.getTimeInMillis() - calendar2.getTimeInMillis();
        Calendar diffCalendar = Calendar.getInstance();
        diffCalendar.setTimeInMillis(diffTime);

        return diffCalendar.get(Calendar.YEAR) - 1970 + "年"
                + diffCalendar.get(Calendar.MONTH) + "ヶ月"
                + diffCalendar.get(Calendar.DAY_OF_MONTH) + "日";
    }

    public String getDiffDays(Calendar now){
        if(elapsedDateFormat == 1){
            return getDiffDaysYMD(now);
        }
        return getDiffDaysSingle(now);
    }


    /*/ v1
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
*/


    //v2
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
        dest.writeString(this.aboveText);
        dest.writeString(this.belowText);
        dest.writeByte(this.isZeroDayStart ? (byte) 1 : (byte) 0);
        dest.writeInt(this.elapsedDateFormat);
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
        this.aboveText = in.readString();
        this.belowText = in.readString();
        this.isZeroDayStart = in.readByte() != 0;
        this.elapsedDateFormat = in.readInt();
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

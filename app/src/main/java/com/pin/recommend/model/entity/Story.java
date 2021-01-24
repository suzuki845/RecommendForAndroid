package com.pin.recommend.model.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.pin.recommend.model.AppDatabase;
import com.pin.recommend.util.TimeUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = RecommendCharacter.class, parentColumns = "id", childColumns = "characterId", onDelete = CASCADE, onUpdate = CASCADE))
public class Story implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(index = true)
    public long characterId;

    public String comment;

    public Date created;

    @Ignore
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    public String getFormattedDate(){
        return FORMAT.format(created);
    }

    @Ignore
    static final int MILLIS_OF_DAY = 1000 * 60 * 60 * 24;

    public long getDiffDays(Calendar calendar1) {
        //modify
        TimeUtil.resetTime(calendar1);
        calendar1.add(Calendar.DAY_OF_MONTH, 1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(created);


        long diffTime =  calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
        long diffDays = diffTime / MILLIS_OF_DAY;
        return diffDays;
    }

    public String getShortComment(int length){
        if(comment.length() >= length){
            return comment.substring(0, length);
        }

        return comment.substring(0, comment.length());
    }

    /*
    public List<Bitmap> getPicturesBitmap(Context context, int w, int h){
        List<Bitmap> result = new ArrayList<>();
        for(StoryPicture picture : pictures(context)){
            result.add(picture.getBitmap(context, w, h));
        }
        return result;
    }
*/
    public Story() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.characterId);
        dest.writeString(this.comment);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
    }

    protected Story(Parcel in) {
        this.id = in.readLong();
        this.characterId = in.readLong();
        this.comment = in.readString();
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}

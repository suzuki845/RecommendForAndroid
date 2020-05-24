package com.pin.recommend.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Story implements Parcelable {

    public int id;

    public String comment;

    public Date created;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    public String getFormattedDate(){
        return FORMAT.format(created);
    }

    static final int MILLIS_OF_DAY = 1000 * 60 * 60 * 24;

    public long getDiffDays(Calendar calendar2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(created);
        long diffTime = calendar1.getTimeInMillis() - calendar2.getTimeInMillis();
        long diffDays = diffTime / MILLIS_OF_DAY;
        return diffDays;
    }

    public List<StoryPicture> pictures;
    public List<StoryPicture> pictures(){
        return pictures;
    }
    public List<Bitmap> getPicturesBitmap(Context context, int w, int h){
        List<Bitmap> result = new ArrayList<>();
        for(StoryPicture p : pictures){
            result.add(p.testGetBitmap(context, w, h));
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.comment);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
        dest.writeTypedList(this.pictures);
    }

    public Story() {
    }

    protected Story(Parcel in) {
        this.id = in.readInt();
        this.comment = in.readString();
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
        this.pictures = in.createTypedArrayList(StoryPicture.CREATOR);
    }

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
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

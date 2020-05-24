package com.pin.recommend.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Character implements Parcelable {

    public Long id;

    public String name;

    public Date created;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy年MM月dd日");

    public Character(Long id, String name, Date created){
        this.id = id;
        this.name = name;
        this.created = created;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.created != null ? this.created.getTime() : -1);
    }

    protected Character(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        long tmpCreated = in.readLong();
        this.created = tmpCreated == -1 ? null : new Date(tmpCreated);
    }

    public static final Parcelable.Creator<Character> CREATOR = new Parcelable.Creator<Character>() {
        @Override
        public Character createFromParcel(Parcel source) {
            return new Character(source);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };
}

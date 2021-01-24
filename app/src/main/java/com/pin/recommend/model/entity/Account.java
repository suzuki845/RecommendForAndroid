package com.pin.recommend.model.entity;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.pin.recommend.model.AppDatabase;

import java.util.List;

@Entity
public class Account implements Parcelable {

    @Ignore
    public static final int ACCOUNT_ID = 1;

    @PrimaryKey
    public long id = ACCOUNT_ID;

    public Long fixedCharacterId;

    public int toolbarBackgroundColor = Color.parseColor("#eb34ab");
    public int getToolbarBackgroundColor(){ return toolbarBackgroundColor; }

    public int toolbarTextColor = Color.parseColor("#ffffff");
    public int getToolbarTextColor(){ return toolbarTextColor; }

    public Long getFixedCharacterId(Context context){
        return fixedCharacterId;
    }

    public void removeFixedCharacter(Context context){
        fixedCharacterId = null;
    }

    public void setFixedCharacter(Context context, long characterId){
        fixedCharacterId = characterId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeValue(this.fixedCharacterId);
        dest.writeInt(this.toolbarBackgroundColor);
        dest.writeInt(this.toolbarTextColor);
    }

    public Account() {
    }

    protected Account(Parcel in) {
        this.id = in.readLong();
        this.fixedCharacterId = (Long) in.readValue(Long.class.getClassLoader());
        this.toolbarBackgroundColor = in.readInt();
        this.toolbarTextColor = in.readInt();
    }

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}

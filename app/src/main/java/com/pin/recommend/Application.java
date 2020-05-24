package com.pin.recommend;

import android.content.Context;

import androidx.multidex.MultiDex;

public class Application extends com.pin.util.activeandroid.app.Application {

    public static final int REQUEST_PICK_IMAGE = 10000;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}

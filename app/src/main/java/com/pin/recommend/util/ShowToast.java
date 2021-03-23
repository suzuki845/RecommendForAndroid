package com.pin.recommend.util;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {
    public static void show(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}

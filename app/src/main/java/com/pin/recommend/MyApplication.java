package com.pin.recommend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.multidex.MultiDex;

import com.pin.recommend.model.viewmodel.AccountViewModel;

public class MyApplication extends android.app.Application implements ViewModelStoreOwner{

    public static final int REQUEST_PICK_IMAGE = 10000;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void setupStatusBarColor(Activity activity, int foregroundColor, int backgroundColor) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if(isNearWhite(backgroundColor) || isNearAlphaZero(backgroundColor)){
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(flags);
            }else{
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(flags);
            }
            activity.getWindow().setStatusBarColor(backgroundColor);
        }
    }

    public static boolean isNearWhite(int color){
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        if(red >= 180 && green >= 180 && blue >= 180){
            return true;
        }
        return false;
    }

    public static boolean isNearAlphaZero(int color){
        int alpha = Color.alpha(color);
        if (alpha <= 100){
            return true;
        }
        return false;
    }


    private ViewModelStore viewModelStore = new ViewModelStore();
    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return viewModelStore;
    }

    public static AccountViewModel getAccountViewModel(AppCompatActivity activity){
        return new ViewModelProvider((MyApplication)activity.getApplication(), activity.getDefaultViewModelProviderFactory()).get(AccountViewModel.class);
    }
}

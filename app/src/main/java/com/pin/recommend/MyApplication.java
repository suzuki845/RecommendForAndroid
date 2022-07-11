package com.pin.recommend;

import static com.pin.recommend.Constants.APP_START_COUNT;
import static com.pin.recommend.Constants.PREF_KEY_IS_LOCKED;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.pin.recommend.model.viewmodel.AccountViewModel;
import com.pin.recommend.util.PrefUtil;
import com.pin.util.Reward;

public class MyApplication extends android.app.Application implements ViewModelStoreOwner,  Application.ActivityLifecycleCallbacks{

    public static final int REQUEST_PICK_IMAGE = 10000;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
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





    private boolean isNeedPassCodeConfirmation = true;
    private static MyApplication app;

    public static MyApplication getInstance() {
        if (app == null) app = new MyApplication();
        return app;
    }

    @Override public void onCreate() {
        super.onCreate();
        PrefUtil.setSharedPreferences(getApplicationContext());
        registerActivityLifecycleCallbacks((ActivityLifecycleCallbacks) this);

        Reward reward = Reward.Companion.getInstance(this);
        reward.setAdUnitId(getResources().getString(R.string.ad_unit_id_for_reward));

        int appStartCount = PrefUtil.getInt(APP_START_COUNT);
        PrefUtil.putInt(APP_START_COUNT, ++appStartCount);
    }

    @Override public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            isNeedPassCodeConfirmation = true;
        }
    }

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override public void onActivityStarted(Activity activity) {
        Intent intent = activity.getIntent();
        boolean isPickImage  = intent.getBooleanExtra(Constants.PICK_IMAGE, false);
        if(!(activity instanceof MainActivity)){
            if (isNeedPassCodeConfirmation && PrefUtil.getBoolean(PREF_KEY_IS_LOCKED) && !isPickImage) {
                activity.startActivity(PassCodeConfirmationActivity.createIntent(getApplicationContext()));
            }
            isNeedPassCodeConfirmation = false;
        }
        intent.putExtra(Constants.PICK_IMAGE, false);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Reward reward = Reward.Companion.getInstance(this);
        reward.checkRewardTime();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

}

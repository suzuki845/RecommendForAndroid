package com.pin.recommend.util.admob;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by pin on 2018/11/10.
 */

public class AdMobManager {

    /*
     * SharedPreferencesのファイル名
     */
    private static final String AD_CONFIG_FILENAME = "ad_config";
    /*
     * --------------ここからの定数はSharedPreferencesのスキーマ----------------------------
     */
    private static final String FIRST_CLICKED_TIME_KEY = "first_clicked_time";

    private static final String IS_FIRST_CLICK_KEY = "is_first_click";

    private static final String CLICKED_COUNT_KEY = "clicked_count";

    private static final String AD_STOP_START_TIME_KEY = "ad_stop_start_time";
    /*
     * ------------------------------------------------------------------------------------
     */
    private Activity activity;

    private int allowAdLoadByElapsedTimeAtMinute = 30;

    private int allowRangeOfAdClickByTimeAtMinute = 3;

    private int allowAdClickLimit = 3;

    private boolean isTestMode = false;

    private AdView adView;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    public AdMobManager(Activity activity, AdView adView) {
        this.activity = activity;
        this.adView = adView;
        this.pref = activity.getSharedPreferences(AD_CONFIG_FILENAME, MODE_PRIVATE);
        this.editor = pref.edit();

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdOpened() {

                int count = pref.getInt(CLICKED_COUNT_KEY, 0) + 1;
                if (pref.getBoolean(IS_FIRST_CLICK_KEY, false)) {
                    editor.putInt(CLICKED_COUNT_KEY, count);
                } else {
                    editor.putBoolean(IS_FIRST_CLICK_KEY, true);
                    editor.putLong(FIRST_CLICKED_TIME_KEY, System.currentTimeMillis());
                    editor.putInt(CLICKED_COUNT_KEY, count);
                }

                long firstClickedTime = pref.getLong(FIRST_CLICKED_TIME_KEY, 0);
                long elapsedTimeFromFirstClick = (System.currentTimeMillis() - firstClickedTime)
                        / (1000 * 60);

                if (elapsedTimeFromFirstClick <= allowRangeOfAdClickByTimeAtMinute &&
                        pref.getInt(CLICKED_COUNT_KEY, 0) >= allowAdClickLimit - 1) {
                    editor.putLong(AD_STOP_START_TIME_KEY, System.currentTimeMillis());
                }

                editor.apply();
            }
        });
    }

    private void debug() {
        SharedPreferences pref = activity.getSharedPreferences(AD_CONFIG_FILENAME, MODE_PRIVATE);
        System.out.println(pref.getAll());
    }

    public void testMode(boolean isTestMode) {
        this.isTestMode = isTestMode;
    }

    public void setAllowAdLoadByElapsedTimeAtMinute(int minute) {
        allowAdLoadByElapsedTimeAtMinute = minute;
    }

    public void setAllowRangeOfAdClickByTimeAtMinute(int minute) {
        allowRangeOfAdClickByTimeAtMinute = minute;
    }

    public void setAllowAdClickLimit(int limit) {
        allowAdClickLimit = limit;
    }

    public void reset() {
        editor.putBoolean(IS_FIRST_CLICK_KEY, false);
        editor.putInt(CLICKED_COUNT_KEY, 0);
        editor.putLong(AD_STOP_START_TIME_KEY, 0);
        editor.putLong(FIRST_CLICKED_TIME_KEY, 0);
        editor.apply();
    }

    public void resetExceptAdStopStartTime() {
        editor.putBoolean(IS_FIRST_CLICK_KEY, false);
        editor.putInt(CLICKED_COUNT_KEY, 0);
        editor.putLong(FIRST_CLICKED_TIME_KEY, 0);
        editor.apply();
    }

    private InjusticeListener injusticeListener;

    public void setInjusticeListener(InjusticeListener listener) {
        this.injusticeListener = listener;
    }

    public interface InjusticeListener {
        void onInjustice();
    }

    private OnPreCheckAction checkAction;

    public interface OnPreCheckAction {
        void doCheck();
    }

    public void setOnPreCheckAction(OnPreCheckAction checkAction) {
        this.checkAction = checkAction;
    }


    public void checkAndLoad() {
        if (this.checkAction != null) {
            checkAction.doCheck();
        }

        long firstClickedTime = pref.getLong(FIRST_CLICKED_TIME_KEY, 0);
        long elapsedTimeFromFirstClick = (System.currentTimeMillis() - firstClickedTime)
                / (1000 * 60);
        if (elapsedTimeFromFirstClick >= allowRangeOfAdClickByTimeAtMinute) {
            resetExceptAdStopStartTime();
        }

        long adStopStartTime = pref.getLong(AD_STOP_START_TIME_KEY, 0);
        long elapsedTimeAtAdStopStart = (System.currentTimeMillis() - adStopStartTime)
                / (1000 * 60);

        if (adStopStartTime != 0 && injusticeListener != null) {
            injusticeListener.onInjustice();
        }

        if (elapsedTimeAtAdStopStart >= allowAdLoadByElapsedTimeAtMinute) {
            if (adStopStartTime == 0) {
                AdRequest adRequest = new AdRequest.Builder().build();
                if (this.isTestMode) {
                    /*
                    adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .build();
                     */
                }
                adView.loadAd(adRequest);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                reset();
            }
        } else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adView.setVisibility(View.GONE);
                }
            });
        }


    }


}






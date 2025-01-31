package com.pin.recommend

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.pin.recommend.domain.model.CharacterPinningManager
import com.pin.recommend.ui.MainActivity
import com.pin.recommend.ui.passcode.PassCodeConfirmationActivity
import com.pin.recommend.util.PrefUtil
import com.pin.util.admob.reward.RemoveAdReward
import com.pin.util.admob.reward.UserDidEarnRewardCounter

class MyApplication : Application(), ViewModelStoreOwner, Application.ActivityLifecycleCallbacks,
    SingletonImageLoader.Factory {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override val viewModelStore = ViewModelStore()
    private var isNeedPassCodeConfirmation = true

    override fun onCreate() {
        super.onCreate()
        val pref = PrefUtil(this)

        CharacterPinningManager(this).initialize()
        registerActivityLifecycleCallbacks(this as ActivityLifecycleCallbacks)
        var appStartCount = pref.getInt(Constants.APP_START_COUNT)
        pref.putInt(Constants.APP_START_COUNT, ++appStartCount)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isNeedPassCodeConfirmation = true
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        val intent = activity.intent
        val isPickImage = intent.getBooleanExtra(Constants.PICK_IMAGE, false)
        if (activity !is MainActivity) {
            val pref = PrefUtil(this)
            if (isNeedPassCodeConfirmation && pref.getBoolean(Constants.PREF_KEY_IS_LOCKED) && !isPickImage) {
                activity.startActivity(PassCodeConfirmationActivity.createIntent(applicationContext))
            }
            isNeedPassCodeConfirmation = false
        }
        intent.putExtra(Constants.PICK_IMAGE, false)
    }

    override fun onActivityResumed(activity: Activity) {
        val normalRemoveAdReward = RemoveAdReward.getInstance(activity)
        normalRemoveAdReward.checkRewardTime()
        val badgeGachaRemoveAdReward = BadgeGachaRemoveAdReward.getInstance(activity)
        badgeGachaRemoveAdReward.checkRewardTime()

        val normalRewardCounter = UserDidEarnRewardCounter.getInstance(activity)
        normalRewardCounter.checkRewardCoolDownElapsed()
        val badgeGachaRewardCounter = BadgeGachaUserDidEarnRewardCounter.getInstance(activity)
        badgeGachaRewardCounter.checkRewardCoolDownElapsed()
        /*
                normalRemoveAdReward.reset()
                badgeGachaRemoveAdReward.reset()
                normalRewardCounter.reset()
                badgeGachaRardCounter.reset()
        */
        setupStatusBarColor(activity, Color.parseColor("#000000"), Color.parseColor("#FFFFFF"))
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    companion object {
        const val REQUEST_PICK_IMAGE = 231132
        fun setupStatusBarColor(activity: Activity, foregroundColor: Int, backgroundColor: Int) {
            if (Build.VERSION.SDK_INT >= 21) {
                val decorView = activity.window.decorView
                var flags = decorView.systemUiVisibility
                if (isNearWhite(backgroundColor) || isNearAlphaZero(backgroundColor)) {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    decorView.systemUiVisibility = flags
                } else {
                    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    decorView.systemUiVisibility = flags
                }
                activity.window.statusBarColor = backgroundColor
            }
        }

        private fun isNearWhite(color: Int): Boolean {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return red >= 180 && green >= 180 && blue >= 180
        }

        private fun isNearAlphaZero(color: Int): Boolean {
            val alpha = Color.alpha(color)
            return alpha <= 100
        }

    }
}


class BadgeGachaRemoveAdReward(context: Context) : RemoveAdReward(context) {

    companion object {
        private var instance: BadgeGachaRemoveAdReward? = null

        fun getInstance(context: Context): BadgeGachaRemoveAdReward {
            instance?.let {
                return it
            }
            val i = BadgeGachaRemoveAdReward(context)
            i.setPrefix("BadgeGacha")
            instance = i
            return i
        }
    }
}

class BadgeGachaUserDidEarnRewardCounter(context: Context) : UserDidEarnRewardCounter(context) {
    companion object {
        private var instance: BadgeGachaUserDidEarnRewardCounter? = null
        fun getInstance(context: Context): BadgeGachaUserDidEarnRewardCounter {
            instance?.let {
                return it
            }
            val i = BadgeGachaUserDidEarnRewardCounter(context)
            i.setPrefix("BadgeGacha")
            instance = i
            return i
        }
    }
}
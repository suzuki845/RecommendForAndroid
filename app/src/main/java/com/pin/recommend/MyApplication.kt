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
import com.pin.recommend.model.AccountModel
import com.pin.recommend.util.PrefUtil
import com.pin.util.Reward.Companion.getInstance

class MyApplication : Application(), ViewModelStoreOwner, Application.ActivityLifecycleCallbacks {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override val viewModelStore = ViewModelStore()
    private var isNeedPassCodeConfirmation = true
    override fun onCreate() {
        super.onCreate()
        AccountModel(this).initialize()
        PrefUtil.setSharedPreferences(applicationContext)
        registerActivityLifecycleCallbacks(this as ActivityLifecycleCallbacks)
        val reward = getInstance(this)
        reward.setAdUnitId(resources.getString(R.string.ad_unit_id_for_reward))
        var appStartCount = PrefUtil.getInt(Constants.APP_START_COUNT)
        PrefUtil.putInt(Constants.APP_START_COUNT, ++appStartCount)
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
            if (isNeedPassCodeConfirmation && PrefUtil.getBoolean(Constants.PREF_KEY_IS_LOCKED) && !isPickImage) {
                activity.startActivity(PassCodeConfirmationActivity.createIntent(applicationContext))
            }
            isNeedPassCodeConfirmation = false
        }
        intent.putExtra(Constants.PICK_IMAGE, false)
    }

    override fun onActivityResumed(activity: Activity) {
        val reward = getInstance(this)
        reward.checkRewardTime()
        setupStatusBarColor(activity, Color.parseColor("#000000"), Color.parseColor("#FAFAFA"))
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

        fun isNearWhite(color: Int): Boolean {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return if (red >= 180 && green >= 180 && blue >= 180) {
                true
            } else false
        }

        fun isNearAlphaZero(color: Int): Boolean {
            val alpha = Color.alpha(color)
            return if (alpha <= 100) {
                true
            } else false
        }

        private var app: MyApplication? = null

        @JvmStatic
        val instance: MyApplication?
            get() {
                if (app == null) app = MyApplication()
                return app
            }
    }
}
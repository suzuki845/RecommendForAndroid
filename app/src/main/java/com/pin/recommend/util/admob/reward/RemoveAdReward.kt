package com.pin.util.admob.reward

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pin.recommend.util.PrefUtil
import java.util.Date

open class RemoveAdReward(val context: Context) {

    companion object {
        private var instance: RemoveAdReward? = null
        fun getInstance(context: Context): RemoveAdReward {
            instance?.let {
                return it
            }
            val i = RemoveAdReward(context)
            instance = i
            return i
        }
    }

    val REWARD_START_TIME = "RemoveAdReward.REWARD_START_TIME"
    val TIME_LEFT = "RemoveAdReward.TIME_LEFT"
    val IS_NO_THANKS = "RemoveAdReward.IS_NO_THANKS"

    private val pref by lazy { PrefUtil(context) }

    private var prefix: String? = null

    private val _isNotify = MutableLiveData(false)
    val isNotify: LiveData<Boolean> = _isNotify

    private val _isBetweenRewardTime = MutableLiveData(false)
    val isBetweenRewardTime: LiveData<Boolean> = _isBetweenRewardTime

    fun setPrefix(prefix: String) {
        this.prefix = prefix
    }

    private fun getRewardStartTimeKey(): String {
        return if (prefix == null) REWARD_START_TIME else "$REWARD_START_TIME.$prefix"
    }

    private fun getTimeLeftKey(): String {
        return if (prefix == null) TIME_LEFT else "$TIME_LEFT.$prefix"
    }

    private fun getIsNoThanksKey(): String {
        return if (prefix == null) IS_NO_THANKS else "$IS_NO_THANKS.$prefix"
    }

    fun checkNotify() {
        val rewardStartTime = pref.getLong(getRewardStartTimeKey())
        val timeLeftToSecond = pref.getInt(getTimeLeftKey()) * 60 * 60 * 1000
        val now = System.currentTimeMillis()
        val timeLeft = rewardStartTime + timeLeftToSecond
        val isNoThanks = pref.getBoolean(getIsNoThanksKey())
        println("Reward -> now: $now, timeLeft: $timeLeft, isNoThanks: $isNoThanks")
        println("Reward -> now: ${Date(now)}, timeLeft: ${Date(timeLeft)}")
        _isNotify.value = timeLeft <= now && !isNoThanks
    }

    fun checkRewardTime() {
        val rewardStartTime = pref.getLong(getRewardStartTimeKey())
        val timeLeftToSecond = pref.getInt(getTimeLeftKey()) * 60 * 60 * 1000
        val now = System.currentTimeMillis()
        val timeLeft = rewardStartTime + timeLeftToSecond
        _isBetweenRewardTime.value = timeLeft > now
        println("Reward -> isBetweenRewardTime:　$isBetweenRewardTime")
    }

    fun check() {
        checkNotify()
        checkRewardTime()
    }

    fun forceNotify() {
        _isNotify.value = true
    }

    fun noThanks() {
        pref.putBoolean(getIsNoThanksKey(), true)
    }

    fun setTimeLeft(hours: Int) {
        _isBetweenRewardTime.value = true
        pref.putLong(getRewardStartTimeKey(), System.currentTimeMillis())
        pref.putInt(getTimeLeftKey(), hours)
    }

    fun reset() {
        pref.putLong(getRewardStartTimeKey(), 0L)
        pref.putInt(getTimeLeftKey(), 0)
        pref.putBoolean(getIsNoThanksKey(), false)
    }


}
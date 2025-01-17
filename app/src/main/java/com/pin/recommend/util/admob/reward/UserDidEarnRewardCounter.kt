package com.pin.util.admob.reward

import android.content.Context
import com.pin.util.PrefUtil

open class UserDidEarnRewardCounter(val context: Context) {

    companion object {
        private var instance: UserDidEarnRewardCounter? = null
        fun getInstance(context: Context): UserDidEarnRewardCounter {
            instance?.let {
                return it
            }
            val i = UserDidEarnRewardCounter(context)
            instance = i
            return i
        }
    }

    val USER_DID_EARN_REWARD_COUNT_KEY =
        "UserDidEarnRewardCounter.USER_DID_EARN_REWARD_COUNT_KEY"
    val LAST_USER_DID_EARN_REWARD_DATE_KEY =
        "UserDidEarnRewardCounter.LAST_USER_DID_EARN_REWARD_DATE_KEY"

    private var prefix: String? = null

    fun setPrefix(prefix: String) {
        this.prefix = prefix
    }

    private fun getUserDidEarnRewardContKey(): String {
        return if (prefix == null) USER_DID_EARN_REWARD_COUNT_KEY else "$USER_DID_EARN_REWARD_COUNT_KEY.$prefix"
    }

    private fun getLastUserDidEarnRewardDateKey(): String {
        return if (prefix == null) LAST_USER_DID_EARN_REWARD_DATE_KEY else "$LAST_USER_DID_EARN_REWARD_DATE_KEY.$prefix"
    }

    private val pref: PrefUtil = PrefUtil(context)

    private fun getLastUserDidEarnRewardDate(): Long {
        return pref.getLong(getLastUserDidEarnRewardDateKey())
    }

    fun checkRewardCoolDownElapsed() {
        val remainingHours = remainingRewardCoolDownElapsedTimeToHours()
        println("UserDidEarnRewardCounter.checkRewardCoolDownElapsed(): $remainingHours ")
        if (remainingHours <= 0) {
            reset()
        }
    }

    fun remainingRewardCoolDownElapsedTimeToHours(): Int {
        val rewardStartTime = getLastUserDidEarnRewardDate()
        val timeLeftToMillis = 16 * 60 * 60 * 1000L
        val timeLeft = rewardStartTime + timeLeftToMillis
        val now = System.currentTimeMillis()
        val hours = (timeLeft - now) / (1000 * 60 * 60)
        return hours.toInt()
    }

    fun increment(): Int {
        val count = pref.getInt(getUserDidEarnRewardContKey()) + 1
        pref.putInt(getUserDidEarnRewardContKey(), count)
        pref.putLong(getLastUserDidEarnRewardDateKey(), System.currentTimeMillis())
        return count
    }

    fun count(): Int {
        return pref.getInt(getUserDidEarnRewardContKey())
    }

    fun reset() {
        pref.putInt(getUserDidEarnRewardContKey(), 0)
        pref.putLong(getLastUserDidEarnRewardDateKey(), 0L)
    }

}
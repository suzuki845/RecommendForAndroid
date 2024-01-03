package com.pin.recommend.model.entity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.pin.imageutil.BitmapUtility
import com.pin.recommend.R
import com.pin.recommend.util.TimeUtil.Companion.resetTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Entity(
    foreignKeys = [ForeignKey(
        entity = Account::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("accountId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
class RecommendCharacter {
    //json化するためのフィールド
    @Ignore
    var payments: List<Payment>? = null

    @Ignore
    var events: List<Event>? = null

    @Ignore
    var stories: List<Story>? = null

    //ここまで
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @JvmField
    @ColumnInfo(index = true)
    var accountId: Long = 0

    @JvmField
    var name: String? = null

    @JvmField
    var created: Date? = Date()

    @JvmField
    var iconImageUri: String? = null

    @JvmField
    var backgroundImageUri: String? = null

    @JvmField
    var backgroundColor: Int? = Color.WHITE

    @JvmField
    var toolbarBackgroundColor: Int? = null

    @JvmField
    var toolbarTextColor: Int? = null

    @JvmField
    var homeTextColor: Int? = Color.parseColor("#444444")

    //v2
    @JvmField
    var aboveText: String? = null

    @JvmField
    var belowText: String? = null

    @JvmField
    @ColumnInfo(defaultValue = "0")
    var isZeroDayStart = false

    @JvmField
    @ColumnInfo(defaultValue = "0")
    var elapsedDateFormat = 0

    @JvmField
    var fontFamily: String? = null

    //end v2
    //v3
    @JvmField
    var storySortOrder = 0

    //end v3
    //v4
    @JvmField
    var backgroundImageOpacity = 1f

    @JvmField
    var homeTextShadowColor: Int? = null
    fun getHomeTextShadowColor(): Int {
        return if (homeTextShadowColor != null) homeTextShadowColor!! else Color.parseColor("#ffffffff")
    }

    constructor()

    fun getFontFamily(): String {
        return if (fontFamily != null) fontFamily!! else "default"
    }

    fun getAboveText(): String {
        return if (aboveText != null) aboveText!! else "を推してから"
    }

    fun getBelowText(): String {
        return if (belowText != null) belowText!! else "になりました"
    }

    fun getIconImage(context: Context, width: Int, height: Int): Bitmap? {
        if (iconImageUri == null) {
            val d = context.getDrawable(R.drawable.ic_person_300dp)
            return BitmapUtility.drawableToBitmap(d)
        }
        return BitmapUtility.readPrivateImage(context, iconImageUri ?: "", width, height)
    }

    fun hasIconImage(): Boolean {
        return iconImageUri != null
    }

    fun deleteIconImage(context: Context?): Boolean {
        if (iconImageUri != null) {
            BitmapUtility.deletePrivateImage(context, iconImageUri)
            iconImageUri = null
            return true
        }
        return false
    }

    fun getBackgroundBitmap(context: Context, width: Int, height: Int): Bitmap? {
        return BitmapUtility.readPrivateImage(context, backgroundImageUri ?: "", width, height)
    }

    fun hasBackgroundImage(): Boolean {
        return backgroundImageUri != null
    }

    fun getBackgroundImageDrawable(context: Context, w: Int, h: Int): Drawable? {
        if (hasBackgroundImage()) {
            val bitmap = getBackgroundBitmap(context, w, h)
            return BitmapDrawable(context.resources, bitmap)
        }
        return null
    }

    fun deleteBackgroundImage(context: Context?): Boolean {
        if (backgroundImageUri != null) {
            BitmapUtility.deletePrivateImage(context, backgroundImageUri)
            backgroundImageUri = null
            return true
        }
        return false
    }

    fun getHomeTextColor(): Int {
        return if (homeTextColor != null) homeTextColor!! else Color.parseColor("#444444")
    }

    val formattedDate: String
        get() = FORMAT.format(created)

    fun getDiffDaysSingle(now: Calendar): String {
        resetTime(now)
        val calendar2 = Calendar.getInstance()
        calendar2.time = created
        resetTime(calendar2)

        //modify
        if (!isZeroDayStart) {
            calendar2.add(Calendar.DAY_OF_MONTH, -1)
        }
        val diffTime = now.timeInMillis - calendar2.timeInMillis
        val diffDays = diffTime / MILLIS_OF_DAY
        return diffDays.toString() + "日"
    }

    fun getElapsedDays(now: Date?): Long {
        val current = Calendar.getInstance()
        current.time = now
        resetTime(current)
        val calendar2 = Calendar.getInstance()
        calendar2.time = created
        resetTime(calendar2)

        //modify
        if (!isZeroDayStart) {
            calendar2.add(Calendar.DAY_OF_MONTH, -1)
        }
        val diffTime = current.timeInMillis - calendar2.timeInMillis
        return diffTime / MILLIS_OF_DAY
    }

    fun getDiffDaysYMD(now: Calendar): String {
        resetTime(now)
        val calendar2 = Calendar.getInstance()
        calendar2.time = created
        resetTime(calendar2)
        if (!isZeroDayStart) {
            calendar2.add(Calendar.DAY_OF_MONTH, -1)
        }
        val diffTime = now.timeInMillis - calendar2.timeInMillis
        val diffCalendar = Calendar.getInstance()
        diffCalendar.timeInMillis = diffTime
        return ((diffCalendar[Calendar.YEAR] - 1970).toString() + "年"
                + diffCalendar[Calendar.MONTH] + "ヶ月"
                + diffCalendar[Calendar.DAY_OF_MONTH] + "日")
    }

    fun getDiffDays(now: Calendar): String {
        return if (elapsedDateFormat == 1) {
            getDiffDaysYMD(now)
        } else getDiffDaysSingle(now)
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        @Ignore
        val CREATED_AT_DESC = 0

        @Ignore
        val CREATED_AT_ASC = 1

        @Ignore
        val UPDATED_AT_DESC = 2

        @Ignore
        val UPDATED_AT_ASC = 3

        //end v4
        @Ignore
        private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")

        @Ignore
        val MILLIS_OF_DAY = 1000 * 60 * 60 * 24

        fun fromJson(json: String): RecommendCharacter {
            return Gson().fromJson(json, RecommendCharacter::class.java)
        }

    }
}
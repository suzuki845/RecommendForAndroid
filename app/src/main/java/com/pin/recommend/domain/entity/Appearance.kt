package com.pin.recommend.domain.entity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.pin.recommend.R
import com.pin.recommend.util.BitmapUtility

data class Appearance(
    val iconImage: Bitmap? = null,
    val backgroundImage: Bitmap? = null,
    val backgroundColor: Int = Color.parseColor("#00ffffff"),
    val homeTextColor: Int = Color.parseColor("#ff000000"),
    val homeTextShadowColor: Int? = null,
    val elapsedDateFormat: Int = 0,
    val fontFamily: String? = null,
    val backgroundImageOpacity: Float = 1f
) {

    fun typeFace(context: Context): Typeface? {
        val typeface = fontFamily?.let {
            if (it == "Default") return@let null
            if (it == "default") return@let null
            if (it == "デフォルト") return@let null
            Typeface.createFromAsset(context.assets, "fonts/$it.ttf")
        }
        return typeface
    }

}

data class SerializableAppearance(
    val iconImageUri: String? = null,
    val backgroundImageUri: String? = null,
    val backgroundColor: Int = Color.parseColor("#00ffffff"),
    val homeTextColor: Int = Color.parseColor("#ff000000"),
    val homeTextShadowColor: Int? = null,
    val elapsedDateFormat: Int = 0,
    val fontFamily: String? = null,
    val backgroundImageOpacity: Float = 1f
) {
    fun getIconImage(context: Context, width: Int, height: Int): Bitmap? {
        if (iconImageUri == null) {
            return ContextCompat.getDrawable(context, R.drawable.ic_person_300dp)?.let {
                return BitmapUtility.drawableToBitmap(it)
            }
        }

        return BitmapUtility.readPrivateImage(context, iconImageUri, width, height)
    }

    fun getBackgroundBitmap(context: Context, width: Int, height: Int): Bitmap? {
        if (backgroundImageUri == null) {
            return null
        }
        return BitmapUtility.readPrivateImage(context, backgroundImageUri, width, height)
    }

    fun toAppearance(
        context: Context,
        iconImageSize: Point,
        backgroundImageSize: Point
    ): Appearance {
        return Appearance(
            getIconImage(context, iconImageSize.x, iconImageSize.y),
            getBackgroundBitmap(context, backgroundImageSize.x, backgroundImageSize.y),
            backgroundColor,
            homeTextColor,
            homeTextShadowColor,
            elapsedDateFormat,
            fontFamily,
            backgroundImageOpacity
        )
    }

    fun toJson(): String {
        return Gson().toJson(SerializableAppearance::class.java)
    }

    companion object {
        fun fromJson(json: String): SerializableAppearance? {
            return Gson().fromJson(json, SerializableAppearance::class.java)
        }
    }

}
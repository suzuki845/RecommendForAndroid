package com.pin.recommend.widget

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson

data class PinnedAnniversaryWidget(
    val widgetId: Int,
    val anniversary: AnniversaryWidgetItem,
) {

    fun getCharacterName(): String {
        return anniversary.characterName
    }

    fun getAnniversaryName(): String {
        return anniversary.anniversary.name
    }

    fun getRemainingDays(): String {
        return anniversary.anniversary.getRemainingDays
    }

    fun getMessage(): String {
        return anniversary.anniversary.message
    }

    fun getBackgroundColor(): Int {
        return anniversary.appearance.backgroundColor
    }

    fun getTextColor(): Int {
        return anniversary.appearance.homeTextColor
    }

    fun getTextShadowColor(): Int? {
        return anniversary.appearance.homeTextShadowColor
    }

    fun getIconImage(context: Context, w: Int, h: Int): Bitmap? {
        return anniversary.getIconImage(context, w, h)
    }

    fun getBackgroundImage(context: Context, w: Int, h: Int): Bitmap? {
        return anniversary.getBackgroundImage(context, w, h)
    }


    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): PinnedAnniversaryWidget? {
            return Gson().fromJson(json, PinnedAnniversaryWidget::class.java)
        }
    }

}
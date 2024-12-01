package com.pin.recommend.widget

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.pin.recommend.model.entity.AnniversaryId
import com.pin.recommend.model.entity.AnniversaryInterface
import com.pin.recommend.model.entity.SerializableAppearance
import java.util.Date

class DisplayContentWidget(
    val appWidgetId: Int,
    val characterName: String,
    val anniversary: AnniversaryInterface,
    val appearance: SerializableAppearance
) {

    fun getId(): AnniversaryId {
        return anniversary.getId()
    }

    fun getAnniversaryName(): String {
        return anniversary.getName()
    }

    fun getRemainingDays(date: Date): Long? {
        return anniversary.getRemainingDays(date)
    }

    fun getMessage(date: Date): String {
        return anniversary.getMessage(date)
    }

    fun getBackgroundColor(): Int {
        return appearance.backgroundColor
    }

    fun getTextColor(): Int {
        return appearance.homeTextColor
    }

    fun getTextShadowColor(): Int? {
        return appearance.homeTextShadowColor
    }

    fun getIconImage(context: Context, w: Int, h: Int): Bitmap? {
        return appearance.getIconImage(context, w, h)
    }

    fun getBackgroundImage(context: Context, w: Int, h: Int): Bitmap? {
        return appearance.getBackgroundBitmap(context, w, h)
    }


    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): PinnedContentWidget? {
            return Gson().fromJson(json, PinnedContentWidget::class.java)
        }
    }


}
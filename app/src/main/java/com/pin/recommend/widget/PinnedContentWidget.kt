package com.pin.recommend.widget

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.pin.recommend.model.entity.AnniversaryId
import com.pin.recommend.model.entity.SerializableAppearance

data class PinnedContentWidget(
    val widgetId: Int,
    val content: ContentWidgetItem,
) {

    fun getId(): AnniversaryId {
        return content.getId()
    }

    fun getCharacterName(): String {
        return content.characterName
    }

    fun getAnniversaryName(): String {
        return content.anniversary.name
    }

    fun getRemainingDays(): String {
        return content.anniversary.getRemainingDays
    }

    fun getMessage(): String {
        return content.anniversary.message
    }

    fun getBackgroundColor(): Int {
        return content.appearance.backgroundColor
    }

    fun getTextColor(): Int {
        return content.appearance.homeTextColor
    }

    fun getTextShadowColor(): Int? {
        return content.appearance.homeTextShadowColor
    }

    fun getIconImage(context: Context, w: Int, h: Int): Bitmap? {
        return content.getIconImage(context, w, h)
    }

    fun getBackgroundImage(context: Context, w: Int, h: Int): Bitmap? {
        return content.getBackgroundImage(context, w, h)
    }

    fun getAppearance(): SerializableAppearance {
        return content.appearance
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
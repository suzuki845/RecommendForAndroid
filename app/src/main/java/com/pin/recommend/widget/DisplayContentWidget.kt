package com.pin.recommend.widget

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.pin.recommend.model.entity.ContentId
import com.pin.recommend.model.entity.Event
import com.pin.recommend.model.entity.SerializableAppearance
import com.pin.recommend.model.entity.TypedEntity

class DisplayContentWidget(
    val appWidgetId: Int,
    val characterName: String,
    val content: TypedEntity,
    val appearance: SerializableAppearance
) {

    fun getId(): ContentId {
        return content.id
    }

    fun getContentType(): String {
        return content.type
    }

    fun getAnniversaryName(): String {
        return content.name
    }

    fun getRemainingDays(): Long? {
        return content.remainingDays
    }

    fun getMessage(): String {
        return content.message
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

    fun getBadgeSummary(): Int {
        return content.badgeSummary
    }

    fun getRecentEvents(): List<Event> {
        return content.recentEvents
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
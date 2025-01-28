package com.pin.recommend.widget

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.pin.recommend.domain.entity.ContentId
import com.pin.recommend.domain.entity.Event
import com.pin.recommend.domain.entity.SerializableAppearance
import com.pin.recommend.domain.entity.TypedEntity

data class ContentWidgetItem(
    val characterName: String,
    val content: TypedEntity,
    val appearance: SerializableAppearance
) {

    fun getId(): ContentId {
        return content.id
    }

    fun getIconImage(context: Context, w: Int, h: Int): Bitmap? {
        return appearance.getIconImage(context, w, h)
    }

    fun getBackgroundImage(context: Context, w: Int, h: Int): Bitmap? {
        return appearance.getBackgroundBitmap(context, w, h)
    }

    fun recentEvents(): List<Event> {
        return content.recentEvents
    }

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): ContentWidgetItem {
            return Gson().fromJson(json, ContentWidgetItem::class.java)
        }
    }
}
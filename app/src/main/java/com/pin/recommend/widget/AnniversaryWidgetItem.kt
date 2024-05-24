package com.pin.recommend.widget

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.pin.recommend.model.entity.Anniversary
import com.pin.recommend.model.entity.SerializableAppearance

data class AnniversaryWidgetItem(
    val characterName: String,
    val anniversary: Anniversary,
    val appearance: SerializableAppearance
) {

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
        fun fromJson(json: String): AnniversaryWidgetItem {
            return Gson().fromJson(json, AnniversaryWidgetItem::class.java)
        }
    }
}
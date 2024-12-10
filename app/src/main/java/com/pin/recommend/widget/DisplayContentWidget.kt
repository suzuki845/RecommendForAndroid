package com.pin.recommend.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
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

    fun getBackgroundImageOpacity(): Float {
        return appearance.backgroundImageOpacity
    }

    fun getFilteredIconImage(context: Context, w: Int, h: Int): Bitmap? {
        // 背景画像を取得
        val originalBitmap = appearance.getIconImage(context, w, h) ?: return null

        // 背景色を取得
        val backgroundColor = appearance.backgroundColor

        // 背景色にアルファを追加
        val alpha = (backgroundColor shr 24) and 0xFF
        val filteredColor = backgroundColor and 0x00FFFFFF // 色部分のみ取り出し
        val finalColor = (alpha shl 24) or filteredColor // 最終的なフィルター色を作成

        // フィルター処理した画像を格納するための新しいBitmapを作成
        val resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val paint = Paint()

        // 背景色を使ってフィルターを適用
        paint.colorFilter = PorterDuffColorFilter(finalColor, PorterDuff.Mode.SRC_ATOP)

        // 新しいビットマップサイズに合わせて描画範囲を設定
        val srcRect = Rect(0, 0, originalBitmap.width, originalBitmap.height)
        val dstRect = Rect(0, 0, w, h)

        // 画像にフィルターを適用して描画
        canvas.drawBitmap(originalBitmap, srcRect, dstRect, paint)

        return resultBitmap
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
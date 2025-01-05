package com.pin.recommend.ui.component

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.TextView

fun TextView.setShadowColor(color: Int) {
    setShadowLayer(3f, 0.3f, 0.3f, color)
}

fun View.toBitmap(): Bitmap {
    // Viewのサイズを測定
    this.measure(
        View.MeasureSpec.makeMeasureSpec(this.width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(this.height, View.MeasureSpec.EXACTLY)
    )
    this.layout(0, 0, this.measuredWidth, this.measuredHeight)

    // ビットマップを作成
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    // CanvasにViewを描画
    val canvas = Canvas(bitmap)
    this.draw(canvas)

    return bitmap
}
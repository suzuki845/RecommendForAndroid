package com.pin.recommend.ui.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ToteBagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val handlePaint = Paint().apply {
        color = Color.parseColor("#F2EDED") // バッグ持ち手の色
        style = Paint.Style.FILL
    }

    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#FAF6E8") // バッグ本体の色
        style = Paint.Style.FILL
    }

    private val badgeBitmaps = mutableListOf<Pair<Bitmap, Rect>>()

    var badges: List<Bitmap> = emptyList()
        set(value) {
            field = value
            badgeBitmaps.clear()
            invalidate()
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null) // ソフトウェアレンダリング（必要に応じて変更）
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultWidth = 200 * context.resources.displayMetrics.density.toInt() // 200dp
        val defaultHeight = 300 * context.resources.displayMetrics.density.toInt() // 300dp

        val width = resolveSize(defaultWidth, widthMeasureSpec)
        val height = resolveSize(defaultHeight, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val widthScale = width / 200f // 基準幅を200dpとしてスケールを計算
        val heightScale = height / 200f // 基準高さを200dpとしてスケールを計算

        // バッグの持ち手を描画
        drawHandle(canvas, widthScale, heightScale)

        // バッグ本体を描画
        drawBody(canvas, widthScale, heightScale)

        // バッジを描画
        drawBadges(canvas, widthScale, heightScale)
    }

    private fun drawHandle(canvas: Canvas, widthScale: Float, heightScale: Float) {
        // 中央の持ち手
        canvas.drawRect(
            80f * widthScale, 10f * heightScale,
            120f * widthScale, 20f * heightScale,
            handlePaint
        )

        // 左側の持ち手
        canvas.drawPath(
            createTrapezoidPath(
                60f * widthScale, 50f * heightScale,
                70f * widthScale, 10f * heightScale,
                90f * widthScale, 10f * heightScale,
                80f * widthScale, 50f * heightScale
            ),
            handlePaint
        )

        // 右側の持ち手
        canvas.drawPath(
            createTrapezoidPath(
                140f * widthScale, 50f * heightScale,
                130f * widthScale, 10f * heightScale,
                110f * widthScale, 10f * heightScale,
                120f * widthScale, 50f * heightScale
            ),
            handlePaint
        )
    }

    private fun drawBody(canvas: Canvas, widthScale: Float, heightScale: Float) {
        canvas.drawRect(
            0f * widthScale, 50f * heightScale,
            200f * widthScale, 200f * heightScale,
            bodyPaint
        )
    }

    val badgeMaxCount = 8
    private fun drawBadges(canvas: Canvas, widthScale: Float, heightScale: Float) {
        if (badgeBitmaps.isEmpty() && badges.isNotEmpty()) {
            val badgeSize = 20f * widthScale
            val padding = 4f * widthScale
            val startX = padding
            val startY = 55f * heightScale

            badges.forEachIndexed { index, badgeBitmap ->
                val row = index / badgeMaxCount // 一行に5つ表示
                val column = index % badgeMaxCount
                val x = startX + column * (badgeSize + padding)
                val y = startY + row * (badgeSize + padding)

                val rect = Rect(
                    x.toInt(),
                    y.toInt(),
                    (x + badgeSize).toInt(),
                    (y + badgeSize).toInt()
                )
                badgeBitmaps.add(badgeBitmap to rect)
            }
        }

        badgeBitmaps.forEach { (bitmap, rect) ->
            // 円形クリップを適用
            val path = Path().apply {
                addOval(
                    RectF(
                        rect.left.toFloat(),
                        rect.top.toFloat(),
                        rect.right.toFloat(),
                        rect.bottom.toFloat()
                    ),
                    Path.Direction.CW
                )
            }

            // クリッピングを適用する前に現在の状態を保存
            canvas.save()

            // バッジ領域を円形にクリップ
            canvas.clipPath(path)

            // バッジを描画
            canvas.drawBitmap(bitmap, null, rect, null)

            // クリッピングをリセット
            canvas.restore()
        }
    }

    private fun createTrapezoidPath(
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        x3: Float, y3: Float,
        x4: Float, y4: Float
    ) = android.graphics.Path().apply {
        moveTo(x1, y1)
        lineTo(x2, y2)
        lineTo(x3, y3)
        lineTo(x4, y4)
        close()
    }
}


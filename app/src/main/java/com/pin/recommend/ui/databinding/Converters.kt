package com.pin.recommend.ui.databinding

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.databinding.InverseMethod
import java.text.SimpleDateFormat
import java.util.Date

object IntConverter {
    @InverseMethod("toInt")
    @JvmStatic
    fun toString(value: Int?): String? {
        return value?.toString()
    }

    @JvmStatic
    fun toInt(value: String?): Int? {
        return value?.toIntOrNull() ?: 0
    }
}

object DoubleConverter {
    @InverseMethod("toDouble")
    @JvmStatic
    fun toString(value: Double?): String? {
        return value?.toString() ?: "0"
    }

    @JvmStatic
    fun toDouble(value: String?): Double? {
        return value?.toDoubleOrNull() ?: 0.toDouble()
    }
}

object DateConverter {
    private val FORMAT = SimpleDateFormat("yyyy年MM月dd日")

    @InverseMethod("toDate")
    @JvmStatic
    fun toString(value: Date?): String? {
        if (value == null) return null
        return FORMAT.format(value)
    }

    @JvmStatic
    fun toDate(value: String?): Date? {
        if (value == null) return null
        return FORMAT.parse(value)
    }
}

object DateMonthConverter {
    private val FORMAT = SimpleDateFormat("yyyy年M月")

    @InverseMethod("toDate")
    @JvmStatic
    fun toString(value: Date?): String? {
        if (value == null) return null
        return FORMAT.format(value)
    }

    @JvmStatic
    fun toDate(value: String?): Date? {
        if (value == null) return null
        return FORMAT.parse(value)
    }
}

object ColorConverter {
    @JvmStatic
    fun toBitmap(colorString: String?): Bitmap? {
        if (colorString == null) return null
        val color = Color.parseColor(colorString)
        val bitmap = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(color)
        return bitmap
    }
}
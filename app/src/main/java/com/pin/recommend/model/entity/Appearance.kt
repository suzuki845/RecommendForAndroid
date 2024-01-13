package com.pin.recommend.model.entity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface

data class Appearance(
    val iconImage: Bitmap? = null,
    val backgroundImage: Bitmap? = null,
    val backgroundColor: Int = Color.parseColor("#00ffffff"),
    val homeTextColor: Int = Color.parseColor("#ff000000"),
    val homeTextShadowColor: Int? = null,
    val elapsedDateFormat:Int = 0,
    val fontFamily: String? = null,
    val backgroundImageOpacity: Float = 1f
){

    fun typeFace(context: Context): Typeface?{
        val typeface = fontFamily?.let {
            if (it == null) return@let null
            if (it == "Default") return@let null
            if (it == "default") return@let null
            if (it == "デフォルト") return@let null
            Typeface.createFromAsset(context.assets, "fonts/$it.ttf")
        }
        return typeface
    }

}
package com.pin.recommend.model.entity

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
    val typeface: Typeface? = null,
    val backgroundImageOpacity: Float = 1f
)
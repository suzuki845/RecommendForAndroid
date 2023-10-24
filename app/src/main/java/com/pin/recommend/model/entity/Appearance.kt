package com.pin.recommend.model.entity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface

class Appearance(
    val iconImage: Bitmap? = null,
    val backgroundImage: Bitmap? = null,
    val backgroundColor: Int = Color.parseColor("#ffffff"),
    val homeTextColor: Int = Color.parseColor("#444444"),
    val homeTextShadowColor: Int? = null,
    val elapsedDateFormat:Int = 0,
    val typeface: Typeface? = null,
    val backgroundImageOpacity: Float = 1f
){
    val test = Color.parseColor("#ffffff")

}
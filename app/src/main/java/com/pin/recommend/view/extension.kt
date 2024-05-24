package com.pin.recommend.view

import android.widget.TextView

fun TextView.setShadowColor(color: Int) {
    setShadowLayer(3f, 0.3f, 0.3f, color)
}